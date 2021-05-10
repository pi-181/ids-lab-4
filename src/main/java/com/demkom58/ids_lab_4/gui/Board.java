package com.demkom58.ids_lab_4.gui;

import com.demkom58.ids_lab_4.GameOfLife;

import javax.swing.*;
import java.awt.*;

public class Board extends JPanel {

    protected int rows;
    protected int cols;
    private GameOfLife game;
    private final GUI gui;

    public Board(GUI gui) {
        this.gui = gui;
    }

    public void addGame(GameOfLife game) {
        this.game = game;
        this.rows = game.numRows;
        this.cols = game.numCols;
    }

    private void paintBoard(Graphics g) {
        Dimension d = this.getSize();
        int height = (int) d.getHeight();
        int width = (int) d.getWidth();
        if (rows < 3 || cols < 3)
            return;

        if (gui.disableMapBox.getState()) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            return;
        }

        if (rows * cols <= height * width) { // Less cells than pixels
            paintSmallerBoard(g, height, width);
        } else { // More cells than pixels
            paintLargerBoard(g, height, width);
        }
    }

    private void paintSmallerBoard(Graphics g, int h, int w) {
        super.paintComponent(g);
        int deltaR = h / rows + 1;
        int deltaC = w / cols + 1;
        int rLoc = 0;
        for (int r = 0; r < rows; r++) {
            int cLoc = 0;
            for (int c = 0; c < cols; c++) {
                if (this.game.isAlive(r, c)) { // Fill the alive cell
                    g.setColor(gui.currentColor);
                    g.fillRect(cLoc, rLoc, deltaC, deltaR);
                } else { // Fill the dead cell
                    g.setColor(Color.WHITE);
                    g.fillRect(cLoc, rLoc, deltaC, deltaR);
                }
                cLoc += deltaC;
            }
            rLoc += deltaR;
        }
    }

    private void paintLargerBoard(Graphics g, int h, int w) {
        if (gui.frame.getExtendedState() != Frame.MAXIMIZED_BOTH) {
            System.err.println("Waiting for screen to be maximized to print...");
            return;
        }

        super.paintComponent(g);
        int deltaR = rows / h;
        int deltaC = cols / w;
        int remR = rows % h;
        int remC = cols % w;
        boolean extraR = true;

        int x = 0;
        for (int i = 0; i < h; i++) {
            int y = 0;
            for (int j = 0; j < w; j++) {

                if (remR > 0 && remC > 0) {
                    extraR = true;
                    if (computeRect(x, y, deltaR + 1, deltaC + 1)) { // Compute the larger subgrid
                        g.setColor(gui.currentColor);
                    } else g.setColor(Color.WHITE);

                    g.fillRect(j, i, 1, 1);
                    y += deltaC + 1;
                    remR--;
                    remC--;
                } else if (remR > 0) {
                    extraR = true;
                    if (computeRect(x, y, deltaR + 1, deltaC)) { // Compute the larger subgrid
                        g.setColor(gui.currentColor);
                    } else g.setColor(Color.WHITE);

                    g.fillRect(j, i, 1, 1);
                    y += deltaC;
                    remR--;
                } else if (remC > 0) {
                    extraR = false;
                    if (computeRect(x, y, deltaR, deltaC + 1)) { // Compute the larger subgrid
                        g.setColor(gui.currentColor);
                    } else g.setColor(Color.WHITE);

                    g.fillRect(j, i, 1, 1);
                    y += deltaC + 1;
                    remC--;
                } else {
                    extraR = false;
                    if (computeRect(x, y, deltaR, deltaC)) { // Compute the larger subgrid
                        g.setColor(gui.currentColor);
                    } else g.setColor(Color.WHITE);

                    g.fillRect(j, i, 1, 1);
                    y += deltaC;
                }
            }

            // Increment the x range value
            x += extraR ? deltaR + 1 : deltaR;
        }
    }

    public boolean computeRect(int x, int y, int xAdd, int yAdd) {
        int total = xAdd * yAdd;
        int aliveCount = 0;

        for (int i = x; i < x + xAdd; i++) {
            for (int j = y; j < y + yAdd; j++) {
                if (this.game.isAlive(i, j))
                    aliveCount++;
            }
        }

        // If half or more are live, return true
        return aliveCount >= total / 2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        paintBoard(g);
    }
}
