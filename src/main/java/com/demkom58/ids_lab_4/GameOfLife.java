package com.demkom58.ids_lab_4;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GameOfLife {
    private ExecutorService executorService;
    private Future<?>[] futureTasks;
    private HandleTask[] tasks;

    public byte[][] initialBoard;
    public int numRows;
    public int numCols;
    private int numThreads;
    private byte[][] grid;
    private byte[][] nextGrid;
    private final byte[][] tempGrid;

    public GameOfLife(int numRows, int numCols, int numThreads) {
        setThreadNumber(numThreads);

        this.grid = new byte[numRows][numCols];
        this.initialBoard = new byte[numRows][numCols];
        this.nextGrid = new byte[numRows][numCols];
        this.tempGrid = new byte[numRows][numCols];
        this.numRows = numRows;
        this.numCols = numCols;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public void setThreadNumber(int numThreads) {
        this.numThreads = numThreads;
        this.executorService = Executors.newFixedThreadPool(numThreads);
        this.futureTasks = new Future[numThreads];
        this.tasks = new HandleTask[numThreads];
    }

    public void configureThreads() {
        int rRangeMod = this.numRows % numThreads;
        int rRange = this.numRows / numThreads;

        for (int i = 0; i < numThreads; i++) {
            final int sr = i * rRange;
            int er = (i + 1) * rRange - 1;

            if (i == numThreads - 1)
                er += rRangeMod;

            this.tasks[i] = new HandleTask(i, this, this.nextGrid, sr, 0, er, numCols - 1);
        }
    }

    public void replaceGrid(byte[][] newGrid) {
        try {
            for (int r = 0; r < numRows; r++) {
                for (int c = 0; c < numCols; c++) {
                    this.grid[r][c] = newGrid[r][c];
                    this.initialBoard[r][c] = newGrid[r][c];
                }
            }
        } catch (Exception e) {
            System.err.format("Likely invalid grid dimensions");
            System.exit(1);
        }
    }

    public byte[][] getGrid() {
        byte[][] retGrid = new byte[numRows][numCols];

        for (int r = 0; r < numRows; r++)
            for (int c = 0; c < numCols; c++)
                retGrid[r][c] = this.grid[r][c];

        return retGrid;
    }

    public byte checkState(int r, int c) {
        boolean isAlive = (this.grid[r][c] == 1);
        int numNeighbors = getNumberOfNeighbors(r, c);

        // Apply rules of the game to determine state of the cell
        if (isAlive && (numNeighbors < 2 || numNeighbors > 3)) {
            return 0;
        } else if (isAlive && (numNeighbors == 2 || numNeighbors == 3)) {
            return 1;
        } else if (!isAlive && numNeighbors == 3) {
            return 1;
        } else {
            return this.grid[r][c];
        }
    }

    private int getNumberOfNeighbors(int r, int c) {
        boolean leftEdge = (c == 0);
        boolean rightEdge = (c == numCols - 1);
        boolean topEdge = (r == 0);
        boolean bottomEdge = (r == numRows - 1);

        int numNeighbors = 0;
        if (!bottomEdge) {
            numNeighbors += this.grid[r+1][c];
            if (!leftEdge) numNeighbors += this.grid[r+1][c-1];
            if (!rightEdge) numNeighbors += this.grid[r+1][c+1];
        }

        if (!topEdge) {
            numNeighbors += this.grid[r-1][c];
            if (!leftEdge) numNeighbors += this.grid[r-1][c-1];
            if (!rightEdge) numNeighbors += this.grid[r-1][c+1];
        }

        if (!leftEdge) numNeighbors += this.grid[r][c-1];
        if (!rightEdge) numNeighbors += this.grid[r][c+1];

        return numNeighbors;
    }

    public void play(int stepCount) {
        for (int i = 0; i < stepCount; i++) {
            configureThreads();
            for (int t = 0; t < this.numThreads; t++)
                futureTasks[t] = executorService.submit(this.tasks[t]);
        }
    }

    public boolean isAlive(int r, int c) {
        return this.grid[r][c] == 1;
    }

    public void joinThreads() {
        try {
            for (Future<?> t : this.futureTasks) t.get();
            this.grid = this.nextGrid;
            this.nextGrid = this.tempGrid;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
