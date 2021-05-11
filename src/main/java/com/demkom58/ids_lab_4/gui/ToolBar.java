package com.demkom58.ids_lab_4.gui;

import javax.swing.*;
import java.awt.*;

public class ToolBar extends JPanel {
    private final JButton goToTick;

    public ToolBar(GUI gui) {
        setLayout(new FlowLayout());
        this.goToTick = new JButton("Go To");

        // Allow a user to jump to any tick that he or she desires < 200
        goToTick.addActionListener(e -> {
            GoTo tickSelector = new GoTo();
            String[] options = {"Submit", "Cancel"};
            int optionType = JOptionPane.DEFAULT_OPTION;
            int messageType = JOptionPane.PLAIN_MESSAGE;
            int reply = JOptionPane.showOptionDialog(null, tickSelector, "",
                    optionType, messageType, null, options, options[0]);
            if (reply == -1 || reply == 1) return;
            int tick = tickSelector.getTick("What tick do you want to go to?  ");
            if (tick == -1)
                JOptionPane.showMessageDialog(null, "Invalid input entered: must be int larger than zero");
            else if (tick == -2)
                return;
            else gui.processAndLoadTick(tick);
        });


        add(goToTick);

    }
}
