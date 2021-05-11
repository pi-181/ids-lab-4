package com.demkom58.ids_lab_4;

public class HandleTask implements Runnable {

    private final GameOfLife game;
    private final byte[][] writeGrid;
    private final int x0;
    private final int y0; // The top left coordinate of the mini-grid
    private final int x1;
    private final int y1; // The bottom right coordinate of the mini-grid

    public HandleTask(int id, GameOfLife game, byte[][] writeGrid, int x0, int y0, int x1, int y1) {
        this.game = game;
        this.writeGrid = writeGrid;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;

        // System.out.println("GridThread-" + id + " (" + x0 + "," + y0 + ") to (" + x1 + "," + y1 + ")"
        // + " handle chunk from (" + x0 + "," + y0 + ") to (" + x1 + "," + y1 + ")");
    }

    private void compute() {
        for (int r = x0; r <= x1; r++)
            for (int c = y0; c <= y1; c++)
                this.writeGrid[r][c] = this.game.checkState(r, c);
    }

    @Override
    public void run() {
        compute();
    }
}
