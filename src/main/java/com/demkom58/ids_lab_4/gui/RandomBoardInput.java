package com.demkom58.ids_lab_4.gui;

import javax.swing.*;
import java.awt.*;

public class RandomBoardInput extends JPanel {

    private final JTextField rowsInput;
    private final JTextField colsInput;
    private final JTextField seedInput;
    private int numRows = -1;
    private int numCols = -1;
    private int seed = -1;

    public RandomBoardInput() {
        setLayout(new GridLayout(3, 3));

        this.rowsInput = new JTextField("2000", 7);
        this.colsInput = new JTextField("2000", 7);
        this.seedInput = new JTextField("303030", 7);

        add(new JLabel("How many rows? "));
        add(this.rowsInput);

        add(new JLabel("How many columns? "));
        add(this.colsInput);

        add(new JLabel("Map seed? "));
        add(this.seedInput);
    }

    public int getUserInput() {
        final String[] options = {"Submit", "Cancel"};
        int reply = JOptionPane.showOptionDialog(null, this, "Random board dimensions",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (reply == 1 || reply == -1)
            return -1;

        final String rows = rowsInput.getText();
        final String cols = colsInput.getText();
        final String seed = seedInput.getText();
        if (rows == null || cols == null || seed == null || rows.isEmpty() || cols.isEmpty() || seed.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enter values to all fields!");
            return -1;
        }

        try {
            this.numRows = Integer.parseInt(rows);
            this.numCols = Integer.parseInt(cols);
            this.seed = Integer.parseInt(seed);

            if (this.numRows < 3 || this.numCols < 3) {
                JOptionPane.showMessageDialog(null, "Both the row and column count must be larger than 3");
                return -1;
            }

            return 0;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid input entered");
            return -1;
        }
    }

    public int getNumRows() {
        return this.numRows;
    }

    public int getNumCols() {
        return this.numCols;
    }

    public int getSeed() {
        return seed;
    }
}
