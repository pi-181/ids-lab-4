package com.demkom58.ids_lab_4.gui;

import javax.swing.*;
import java.awt.*;

public class StatisticsPanel extends JPanel {

    private int tick;
    private int numAlive, numDead;
    private int diffNumAlive, diffNumDead;
    private final JPanel currPanel;
    private final JPanel diffPanel;
    private final JLabel labelAlive;
    private final JLabel labelDead;
    private final JLabel diffAlive;
    private final JLabel diffDead;
    private final JLabel tickLabel;

    public StatisticsPanel(int numAlive, int numDead) {
        this.tick = 0;
        this.numAlive = numAlive;
        this.numDead = numDead;
        this.diffNumAlive = 0;
        this.diffNumDead = 0;
        this.tickLabel = new JLabel();
        this.currPanel = new JPanel();
        this.diffPanel = new JPanel();
        currPanel.setLayout(new BoxLayout(currPanel, BoxLayout.Y_AXIS));
        diffPanel.setLayout(new BoxLayout(diffPanel, BoxLayout.Y_AXIS));
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 7));

        this.labelAlive = new JLabel("ALIVE:  0");
        this.labelDead = new JLabel("DEAD:  0");
        this.currPanel.add(labelAlive);
        this.currPanel.add(labelDead);

        this.diffAlive = new JLabel("ALIVE COMPARED TO LAST TICK:  0");
        this.diffDead = new JLabel("DEAD COMPARED TO LAST TICK:  0");
        this.diffPanel.add(diffAlive);
        this.diffPanel.add(diffDead);
        display();
    }

    public int getTick() {
        return this.tick;
    }

    public void update(int newNumAlive, int newNumDead, int newTick) {
        diffNumAlive = newNumAlive - numAlive;
        diffNumDead = newNumDead - numDead;
        numAlive = newNumAlive;
        numDead = newNumDead;
        tick = newTick;
        display();
    }

    public void updateTick(boolean increaseTick) {
        if (increaseTick)
            tick++;
        else
            tick--;
    }

    private void display() {

        // Display the current tick of the game
        this.tickLabel.setText(String.format("TICK:  %d", tick));

        // Display the stats from the current tick
        labelAlive.setText(String.format("ALIVE:  %d", numAlive));
        labelDead.setText(String.format("DEAD:  %d", numDead));

        // Display the comparative stats between current and previous ticks
        // Build the text which states the different in the amount alive/dead
        StringBuilder aliveBuilder = new StringBuilder("ALIVE COMPARED TO LAST TICK:  ");
        if (diffNumAlive > 0) aliveBuilder.append("+");
        if (diffNumAlive < 0) aliveBuilder.append("-");
        aliveBuilder.append(Math.abs(diffNumAlive));
        StringBuilder deadBuilder = new StringBuilder("DEAD COMPARED TO LAST TICK:  ");
        if (diffNumDead > 0) deadBuilder.append("+");
        if (diffNumDead < 0) deadBuilder.append("-");
        deadBuilder.append(Math.abs(diffNumDead));

        // Add text to the JLabels
        diffAlive.setText(aliveBuilder.toString());
        diffDead.setText(deadBuilder.toString());

        // Add to StatisticsPanel object
        this.add(tickLabel);
        this.add(currPanel);
        this.add(diffPanel);
    }
}
