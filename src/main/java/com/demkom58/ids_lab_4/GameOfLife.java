package com.demkom58.ids_lab_4;

public class GameOfLife {
    public byte[][] initialBoard;
    public int numRows;
    public int numCols;
    private GridThread[] pool;
    private int numThreads;
    private byte[][] grid;
    private byte[][] nextGrid;
    private final byte[][] tempGrid;

    public GameOfLife(int numRows, int numCols, int numThreads) {
        this.grid = new byte[numRows][numCols];
        this.initialBoard = new byte[numRows][numCols];
        this.nextGrid = new byte[numRows][numCols];
        this.tempGrid = new byte[numRows][numCols];
        this.numRows = numRows;
        this.numCols = numCols;
        this.numThreads = numThreads;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public void setThreadNumber(int numThreads) {
        this.numThreads = numThreads;
    }

    public void configureThreads() {
        this.pool = new GridThread[this.numThreads];
        int rRem = 0;
        int cRem = 0;

        // Check to see if a chunk of processors should have a larger row block
        int rThreads = this.numThreads / 2;
        int rRangeMod = this.numRows % rThreads;
        int rRange = this.numRows / rThreads;
        if (rRangeMod != 0)
            rRem = this.numRows % rRange;

        // Check to see if a chunk of processors should have a larger col block
        int cRange = this.numCols / 2;
        if (this.numCols % 2 != 0)
            cRem = 1;

        // Create the threads array and add the proper coordinates for each
        int r = 0;
        int cnt = 0;
        for (int i = 0; i < rThreads - 1; i++, r += rRange) {
            // Create the left and the right thread block for the current row
            this.pool[cnt++] = new GridThread(this, this.grid, this.nextGrid, r, 0, r + rRange - 1, cRange - 1);
            this.pool[cnt++] = new GridThread(this, this.grid, this.nextGrid, r, cRange, r + rRange - 1, cRange * 2 + cRem - 1);
        }
        // Create the bottom left and the bottom right threads
        this.pool[cnt++] = new GridThread(this, this.grid, this.nextGrid, r, 0, r + rRange - 1, cRange - 1);
        this.pool[cnt++] = new GridThread(this, this.grid, this.nextGrid, r, cRange, r + rRange - 1, cRange * 2 + cRem - 1);
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

    private int calculateCorner(int r, int c) {
        int topBound = r;
        int bottomBound = r;
        int leftBound = c;
        int rightBound = c;
        int n = numRows - 1;
        int m = numCols - 1;
        int numNeighbors = 0;

        if (r == 0 && c == 0) { // top left corner
            bottomBound++;
            rightBound++;
            numNeighbors += grid[n][m];
            numNeighbors += grid[0][m] + grid[1][m];
            numNeighbors += grid[n][0] + grid[n][1];
        } else if (r == 0 && c == m) { // top right corner
            bottomBound++;
            leftBound--;
            numNeighbors += grid[n][0];
            numNeighbors += grid[0][0] + grid[1][0];
            numNeighbors += grid[n][m] + grid[n][m - 1];
        } else if (r == n && c == 0) { // bottom left corner
            topBound--;
            rightBound++;
            numNeighbors += grid[0][m];
            numNeighbors += grid[0][0] + grid[0][1];
            numNeighbors += grid[n][m] + grid[n - 1][m];
        } else if (r == n && c == m) { // bottom right corner
            topBound--;
            leftBound--;
            numNeighbors += grid[0][0];
            numNeighbors += grid[0][m] + grid[0][m - 1];
            numNeighbors += grid[n][0] + grid[n - 1][0];
        } else return -1; // Not a corner

        for (int i = topBound; i <= bottomBound; i++) {
            for (int j = leftBound; j <= rightBound; j++) {
                if (!(i == r && j == c)) {
                    numNeighbors += grid[i][j];
                }
            }
        }
        return numNeighbors;
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

        int topBound = r - 1;
        int bottomBound = r + 1;
        int leftBound = c - 1;
        int rightBound = c + 1;
        int numNeighbors = 0;

        // Check for Cell not on the perimeter
        if (!topEdge && !bottomEdge && !leftEdge && !rightEdge) {
            for (int i = topBound; i <= bottomBound; i++) {
                for (int j = leftBound; j <= rightBound; j++) {
                    if (!(i == r && j == c)) { // Don't check (r,c) in grid
                        numNeighbors += this.grid[i][j];
                    }
                }
            }
            return numNeighbors;
        }

        // Return the amount of neighbors for a corner cell
        int cornerResult = calculateCorner(r, c);
        if (cornerResult != -1)
            return cornerResult;

        // Cell is not on the corner nor in the middle.
        // Access the Cell's that are wrapped and get the proper bounds.
        if (topEdge) {
            topBound = r;
            bottomBound = r + 1;
            for (int i = leftBound; i <= rightBound; i++) { // loop across bottom
                numNeighbors += this.grid[numRows - 1][i];
            }
        } else if (bottomEdge) {
            topBound = r - 1;
            bottomBound = r;
            for (int i = leftBound; i <= rightBound; i++) { // loop across top
                numNeighbors += this.grid[0][i];
            }
        } else if (leftEdge) {
            leftBound = c;
            rightBound = c + 1;
            for (int i = topBound; i <= bottomBound; i++) { // loop across right
                numNeighbors += this.grid[i][numCols - 1];
            }
        } else { // rightEdge
            leftBound = c - 1;
            rightBound = c;
            for (int i = topBound; i <= bottomBound; i++) { // loop across left
                numNeighbors += this.grid[i][0];
            }
        }

        // Get the Cell's neighbors which are not wrapped
        for (int i = topBound; i <= bottomBound; i++) {
            for (int j = leftBound; j <= rightBound; j++) {
                if (!(i == r && j == c)) { // Don't check (r,c) in grid
                    numNeighbors += this.grid[i][j];
                }
            }
        }
        return numNeighbors;
    }

    public void play(int stepCount) {
        // If multithreaded, handle the threads
        if (this.numThreads > 1) {
            System.out.println(numThreads + " threads handle...");
            for (int i = 0; i < stepCount; i++) {
                configureThreads();
                // Start all of the threads for computation
                for (int t = 0; t < this.numThreads; t++)
                    this.pool[t].start();
            }
            return;
        }

        System.out.println("Single thread handle...");
        // If single threaded
        int numCells = numRows * numCols;
        for (int i = 0; i < stepCount; i++) {
            // Check the state of every cell in the grid
            int j = 0;
            byte[] newGrid = new byte[numCells];
            for (int r = 0; r < numRows; r++) {
                for (int c = 0; c < numCols; c++) {
                    newGrid[j] = checkState(r, c);
                    j++;
                }
            }

            // Update this grid with new states of the Cells
            int rIndex = 0;
            int cIndex = 0;
            for (int c = 0; c < numCells; c++) {
                if (newGrid[c] == 1) {
                    grid[rIndex][cIndex] = 1;
                } else {
                    grid[rIndex][cIndex] = 0;
                }
                cIndex++;
                if ((c + 1) % numCols == 0) {
                    rIndex++;
                    cIndex = 0;
                }
            }
        }
    }

    public boolean isAlive(int r, int c) {
        return this.grid[r][c] == 1;
    }

    public void joinThreads() {
        if (this.numThreads == 1)
            return;

        try {
            for (GridThread t : this.pool) t.join();
            this.grid = this.nextGrid;
            this.nextGrid = this.tempGrid;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
