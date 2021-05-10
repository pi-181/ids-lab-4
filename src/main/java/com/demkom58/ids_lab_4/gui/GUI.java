package com.demkom58.ids_lab_4.gui;

import com.demkom58.ids_lab_4.Board;
import com.demkom58.ids_lab_4.GameOfLife;
import com.demkom58.ids_lab_4.tools.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;

public class GUI {
    private static final String[] GUI_COLORS = {"Green", "Red", "Blue", "Orange", "Yellow"};

    public MainFrame frame;
    public Color currentColor;
    private GameOfLife game;
    private int rowCount;
    private int colCount;
    private int threadCount = 1;
    private Board cellPanel;
    private StatisticsPanel statsPanel;

    /**
     * The constructor for the Game Of Life graphical interface.
     *
     * @param rowCount - the number of rows in the grid
     * @param colCount - the number of columns in the grid
     */
    public GUI(int rowCount, int colCount) {
        this.frame = new MainFrame();
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.currentColor = Color.GREEN;
    }

    /**
     * GUI Main Method
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            GUI gui = new GUI(0, 0);
            gui.createAndShowGUI();
        });
        System.out.println("App exited.");
    }

    /**
     * Driver method to create the GUI and display it to the user.
     */
    public void createAndShowGUI() {
        this.cellPanel = new Board(this);
        this.statsPanel = new StatisticsPanel(0, 0);

        // Create the containerPanel and northern panel
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BorderLayout());

        // Create a JComboBox to take in user selected colors
        JComboBox<String> colorSelector = new JComboBox<>(GUI_COLORS);
        colorSelector.addActionListener(e -> {
            if (rowCount < 3) {
                JOptionPane.showMessageDialog(null, "You must load in a game before selecting a color");
                return;
            }

            switch ((String) Objects.requireNonNull(colorSelector.getSelectedItem())) {
                case "Green" -> updateColor(Color.GREEN);
                case "Red" -> updateColor(Color.RED);
                case "Orange" -> updateColor(Color.ORANGE);
                case "Yellow" -> updateColor(Color.YELLOW);
                case "Blue" -> updateColor(Color.BLUE);
            }
        });

        // Add tick control buttons to go forward
        TickControl tickControl = new TickControl(Color.GREEN);
        GameMenuBar menu = new GameMenuBar();
        ToolBar toolBar = new ToolBar(this);
        toolBar.add(colorSelector);

        // Add statistics and control objects to bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 7));
        bottomPanel.add(statsPanel);
        bottomPanel.add(tickControl);

        // Add to frame and display
        containerPanel.add(toolBar, BorderLayout.NORTH);
        containerPanel.add(cellPanel, BorderLayout.CENTER);
        containerPanel.add(bottomPanel, BorderLayout.SOUTH);
        this.frame.setJMenuBar(menu);
        this.frame.getContentPane().add(containerPanel);
        this.frame.pack();
        this.frame.setVisible(true);
    }

    /**
     * Update the color of the alive cells.
     *
     * @param color the new color of the cells
     */
    public void updateColor(Color color) {
        this.currentColor = color;
        this.cellPanel.repaint();
    }

    /**
     * This method updates the GUI display with a new grid
     * from a different tick.
     *
     * @param newGrid - a 2D array of ints used to load the new grid
     * @param newTick - the tick corresponding to the grid
     */
    public void updateAndShowGUI(byte[][] newGrid, int newTick) {
        if (newTick == statsPanel.getTick() && newTick > 0) return;
        // Update the new colors of the cells and keep track of dead/alive count
        int numAlive = 0;
        int numDead = 0;
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                int newState = newGrid[r][c];
                if (newState == 1) { // Birth it
                    numAlive++;
                } else { // Kill it
                    numDead++;
                }
            }
        }
        // Update the current grid with a new one and update stats
        this.statsPanel.update(numAlive, numDead, newTick);
        this.frame.revalidate();
        this.frame.repaint();
    }

    /**
     * This method is called when an entirely new grid needs to be uploaded
     * into the game interface. It assumes that newGrid holds only 0s and 1s
     * and it assumes that it has rowCount*colCount total entries.
     *
     * @param newGrid - 2D array of integers which becomes the new grid
     */
    public void configureNewGrid(byte[][] newGrid) {
        // Reset current cell panel
        this.cellPanel.removeAll();
        cellPanel.setLayout(new GridLayout(this.rowCount, this.colCount));

        // Update the new colors of the cells and keep track of dead/alive count
        int numAlive = 0;
        int numDead = 0;
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                if (newGrid[r][c] == 1) {
                    numAlive++;
                } else {
                    numDead++;
                }
            }
        }
        this.statsPanel.update(numAlive, numDead, 0);
        this.cellPanel.addGame(this.game);
        this.frame.revalidate();
        this.frame.repaint();
    }

    /**
     * This method updates the GUI with the next tick of
     * the game. It also stores the previous grid.
     *
     * @param display - a boolean value telling the GUI to update or not
     */
    public void nextTick(boolean display) {
        if (this.rowCount < 3) {
            JOptionPane.showMessageDialog(null, "You must load in a new game before stepping through it");
            return;
        }
        int nextTick = this.statsPanel.getTick() + 1;
        byte[][] copyGrid = new byte[rowCount][colCount];
        byte[][] gameGrid = this.game.getGrid();
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                copyGrid[r][c] = gameGrid[r][c];
            }
        }
        this.game.replaceGrid(copyGrid); // Load the newest grid into the GameOfLife object
        this.game.play(1);               // Update the grid for one round
        this.game.joinThreads();
        if (display) {
            this.updateAndShowGUI(this.game.getGrid(), nextTick);
        }
    }

    /**
     * This method takes in a tick and jumps to that tick.
     *
     * @param tick - the tick to go to
     */
    protected void processAndLoadTick(int tick) {
        if (this.rowCount < 3 || this.colCount < 3) {
            JOptionPane.showMessageDialog(null, "You must first load in a game.");
            return;
        }

        int currentTick = this.statsPanel.getTick();
        if (tick > currentTick) {
            System.out.println("Starting jump to tick: " + tick);
            long startTime = System.currentTimeMillis();
            while (this.statsPanel.getTick() < tick - 1) {
                nextTick(false);
                this.statsPanel.updateTick(true);
            }

            nextTick(true);
            double elapsedTime = ((double) (System.currentTimeMillis() - startTime)) / 1000.0;
            System.out.println("TIME: " + elapsedTime);
        } else if (tick < currentTick) {
            JOptionPane.showMessageDialog(null, "Cannot go backwards, please load or generate a grid.");
        }
    }

    /**
     * Creates a new 2D grid and randomly fills it with alive cells
     *
     * @param r - the number of rows
     * @param c - the number of columns
     * @return the new grid
     */
    public byte[][] generateRandomGrid(int r, int c) {
        byte[][] newGrid = new byte[r][c];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                double d = Math.random();
                if (d > 0.5) {
                    newGrid[i][j] = 1;
                } else {
                    newGrid[i][j] = 0;
                }
            }
        }
        return newGrid;
    }

    class GameMenuBar extends JMenuBar {
        /**
         * Constructor for the toolbar and menu.
         */
        public GameMenuBar() {
            ToolMenu tools = new ToolMenu();
            JMenu game = new JMenu("Game");
            game.setMnemonic(KeyEvent.VK_A);
            game.getAccessibleContext().setAccessibleDescription("Game Related Commands");
            JMenuItem randBoard = new JMenuItem("Generate", KeyEvent.VK_T);
            JMenuItem numProcessors = new JMenuItem("Threads", KeyEvent.VK_T);


            // Allow a user to specify the number of processors
            final GUI gui = GUI.this;
            numProcessors.addActionListener(e -> {
                ThreadCountInput threadInput = new ThreadCountInput(gui.threadCount);
                final int rc = threadInput.getUserInput(gui.rowCount, gui.colCount);
                if (rc == -1) // Return if invalid input
                    return;
                gui.threadCount = threadInput.numThreads();
                gui.game.setThreadNumber(threadInput.numThreads());
            });


            // Allow a user to randomly generate a board given a specific size
            randBoard.addActionListener(e -> {
                RandomBoardInput getDims = new RandomBoardInput();
                int rc = getDims.getUserInput();
                gui.rowCount = getDims.getNumRows();
                gui.colCount = getDims.getNumCols();
                if (rc == -1)
                    return;

                byte[][] newGrid = generateRandomGrid(gui.rowCount, gui.colCount);
                gui.game = new GameOfLife(gui.rowCount, gui.colCount, gui.threadCount);
                gui.game.replaceGrid(newGrid);
                configureNewGrid(newGrid);
            });

            game.add(randBoard);
            game.add(numProcessors);

            this.add(game);
            this.add(tools);
        }
    }

    class TickControl extends JPanel {

        private final JButton next;

        /**
         * The constructor for TickControl takes in two colors and assigns
         * them to the back and next buttons which are added to a JPanel.
         *
         * @param nextColor - Sets the 'Next' button to nextColor
         */
        public TickControl(Color nextColor) {
            this.setLayout(new FlowLayout());
            this.next = new JButton("Next");
            this.next.setBackground(nextColor);
            next.addActionListener(e -> nextTick(true));
            add(next);
        }
    }
}
