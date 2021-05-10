package com.demkom58.ids_lab_4.tools;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class RangeDialogBox extends JPanel {

    private static final String[] LABEL_TEXTS = {"Starting Tick ", " Ending Tick "};
    private static final int COLS = 4;
    private final HashMap<String, JTextField> labelFieldMap = new HashMap<String, JTextField>();

    /**
     * Constructor for a dialog box to take in a range of ticks.
     * These ticks represent all of the game tick's for which there
     * will be a saved file for.
     */
    public RangeDialogBox() {
        setLayout(new GridBagLayout());
        for (int i = 0; i < LABEL_TEXTS.length; i++) {
            String msgPrompt = LABEL_TEXTS[i];
            add(new JLabel(msgPrompt));
            JTextField textField = new JTextField(COLS);
            labelFieldMap.put(msgPrompt, textField);
            add(textField);
        }
        setBorder(BorderFactory.createTitledBorder("Enter a tick range from 0 to 200"));
    }

    /**
     * Takes in a string which is the label of the input box
     * and returns the value that was entered by the user.
     *
     * @param labelText - a String which will be validated against accepted labels
     * @return the value entered by the user as a string if labelText matches that input field
     * @throws IllegalArgumentException is thrown if labelText does not exist
     */
    public String getText(String labelText) {
        JTextField textField = labelFieldMap.get(labelText);
        if (textField != null) {
            return textField.getText();
        } else {
            throw new IllegalArgumentException(labelText);
        }
    }
}
