package com.demkom58.ids_lab_4.gui;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class GoTo extends JPanel {
    private static final String message = "What tick do you want to go to?  ";
    private static final int COLS = 4;
    private final Map<String, JTextField> labelFieldMap = new HashMap<>();

    public GoTo() {
        add(new JLabel(message));

        final var textField = new JTextField(COLS);
        labelFieldMap.put(message, textField);
        add(textField);

        setBorder(BorderFactory.createTitledBorder("Enter a tick from 0 to 200"));
    }

    public int getTick(String labelText) {
        final var textField = labelFieldMap.get(labelText);
        if (textField == null)
            return -2;

        try {
            final int returnValue = Integer.parseInt(textField.getText());
            return returnValue < 0 || returnValue > 200 ? -1 : returnValue;
        } catch (Exception e) {
            return -1;
        }
    }
}
