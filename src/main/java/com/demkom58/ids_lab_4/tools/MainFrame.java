package com.demkom58.ids_lab_4.tools;

import javax.swing.*;

public class MainFrame extends JFrame {

    /**
     * This method passes set up information to main window.
     * It sets the name, the close operation, the opening
     * position and the initial size.
     */
    public MainFrame() {
        this.setTitle("Conway's Game of Life");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(this.getExtendedState() | MAXIMIZED_BOTH);
    }
}
