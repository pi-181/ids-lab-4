package com.demkom58.ids_lab_4.gui;

import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        this.setTitle("Conway's Game of Life");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(this.getExtendedState() | MAXIMIZED_BOTH);
    }
}
