package com.demkom58.ids_lab_4.gui;

import com.demkom58.ids_lab_4.GameOfLife;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Objects;
import java.util.Random;

public class GUI {
    private static final String[] GUI_COLORS = {"Green", "Red", "Blue", "Orange", "Yellow"};
    public final Checkbox disableMapBox = new Checkbox("Disable map?");

    public MainFrame frame;
    public Color currentColor;
    private GameOfLife game;
    private int rowCount;
    private int colCount;
    private int threadCount = 1;
    private Board cellPanel;
    private StatisticsPanel statsPanel;

    public GUI(int rowCount, int colCount) {
        this.frame = new MainFrame();
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.currentColor = Color.GREEN;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            GUI gui = new GUI(0, 0);
            gui.createAndShowGUI();
        });
        System.out.println("App exited.");
    }

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
        TickControl tickControl = new TickControl(Color.ORANGE);
        GameMenuBar menu = new GameMenuBar();
        ToolBar toolBar = new ToolBar(this);
        toolBar.add(colorSelector);
        toolBar.add(disableMapBox);

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
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
    }

    public void updateColor(Color color) {
        this.currentColor = color;
        this.cellPanel.repaint();
    }

    public void updateAndShowGUI(byte[][] newGrid, int newTick) {
        if (newTick == statsPanel.getTick() && newTick > 0) return;

        // Update the new colors of the cells and keep track of dead/alive count
        int numAlive = 0;
        int numDead = 0;

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                if (newGrid[r][c] == 1) numAlive++;
                else numDead++;
            }
        }

        // Update the current grid with a new one and update stats
        this.statsPanel.update(numAlive, numDead, newTick);
        this.frame.revalidate();
        this.frame.repaint();
    }

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

    public void nextTick(boolean display) {
        if (this.rowCount < 3) {
            JOptionPane.showMessageDialog(null, "You must load in a new game before stepping through it");
            return;
        }

        int nextTick = this.statsPanel.getTick() + 1;
        byte[][] copyGrid = new byte[rowCount][colCount];
        byte[][] gameGrid = this.game.getGrid();

        for (int r = 0; r < rowCount; r++)
            for (int c = 0; c < colCount; c++)
                copyGrid[r][c] = gameGrid[r][c];

        this.game.replaceGrid(copyGrid);
        this.game.play(1);
        this.game.joinThreads();

        if (display) this.updateAndShowGUI(this.game.getGrid(), nextTick);
    }

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
            final double elapsedTime = ((double) (System.currentTimeMillis() - startTime)) / 1000.0;
            JOptionPane.showMessageDialog(null,
                    "Done for " + elapsedTime + "ms, using " + game.getNumThreads() + " threads.\n" +
                    "Matrix " + game.numRows + "x" + game.numCols + " and " + tick + " ticks."
            );
        } else if (tick < currentTick) {
            JOptionPane.showMessageDialog(null, "Cannot go backwards, please load or generate a grid.");
        }
    }

    public byte[][] generateRandomGrid(int r, int c, int seed) {
        final Random random = new Random(seed);

        byte[][] newGrid = new byte[r][c];
        for (int i = 0; i < r; i++)
            for (int j = 0; j < c; j++)
                newGrid[i][j] = (byte) (random.nextDouble() > 0.5 ? 1 : 0);

        return newGrid;
    }

    class GameMenuBar extends JMenuBar {
        public GameMenuBar() {
            JMenu game = new JMenu("Game");
            game.setMnemonic(KeyEvent.VK_A);
            game.getAccessibleContext().setAccessibleDescription("Game Related Commands");
            JMenuItem randBoard = new JMenuItem("Generate", KeyEvent.VK_T);
            JMenuItem numProcessors = new JMenuItem("Threads", KeyEvent.VK_T);

            final GUI gui = GUI.this;
            numProcessors.addActionListener(e -> {
                ThreadCountInput threadInput = new ThreadCountInput(gui.threadCount);
                final int rc = threadInput.getUserInput(gui.rowCount, gui.colCount);
                if (rc == -1)
                    return;
                gui.threadCount = threadInput.numThreads();
                gui.game.setThreadNumber(threadInput.numThreads());
            });

            randBoard.addActionListener(e -> {
                RandomBoardInput boardInput = new RandomBoardInput();
                int rc = boardInput.getUserInput();
                gui.rowCount = boardInput.getNumRows();
                gui.colCount = boardInput.getNumCols();
                if (rc == -1)
                    return;

                byte[][] newGrid = generateRandomGrid(gui.rowCount, gui.colCount, boardInput.getSeed());
                gui.game = new GameOfLife(gui.rowCount, gui.colCount, gui.threadCount);
                gui.game.replaceGrid(newGrid);
                configureNewGrid(newGrid);
            });

            game.add(randBoard);
            game.add(numProcessors);

            this.add(game);
        }
    }

    class TickControl extends JPanel {
        private final JButton next;

        public TickControl(Color nextColor) {
            this.setLayout(new FlowLayout());
            this.next = new JButton("Play Tick");
            this.next.setBackground(nextColor);
            next.addActionListener(e -> nextTick(true));
            add(next);
        }
    }
}
