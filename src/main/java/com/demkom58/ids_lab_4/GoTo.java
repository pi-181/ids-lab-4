package com.demkom58.ids_lab_4;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class GoTo extends JPanel {

    private static final String message = "What tick do you want to go to?  ";
    private static final int COLS = 4;
    private final Map<String, JTextField> labelFieldMap = new HashMap<>();

    /**
     * Constructor for an input to go to a certain tick.
     */
    public GoTo() {
        add(new JLabel(message));
        JTextField textField = new JTextField(COLS);
        labelFieldMap.put(message, textField);
        add(textField);
        setBorder(BorderFactory.createTitledBorder("Enter a tick from 0 to 200"));
    }

    /**
     * @param labelText - a String which maps to the user input field
     * @return the integer value that the user entered, -1 if input was invalid
     * and -2 if no input was entered.
     */
    public int getTick(String labelText) {
        JTextField textField = labelFieldMap.get(labelText);
        if (textField == null)
            return -2;

        try {
            int returnValue = Integer.parseInt(textField.getText());
            if (returnValue < 0 || returnValue > 200) {
                return -1;
            } else {
                return returnValue;
            }
        } catch (Exception e) {
            return -1;
        }
    }
}
