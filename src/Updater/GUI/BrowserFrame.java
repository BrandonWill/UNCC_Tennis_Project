package Updater.GUI;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Updater.Browser;

import javax.swing.*;


/**
 * @author Brandon
 */
public class BrowserFrame extends JFrame {

    public BrowserFrame() {
        initComponents();
    }

    private void initComponents() {
        jTabbedPane1 = new JTabbedPane();
        settingsPanel = new JPanel();
        queuePanel = new JPanel();
        farmingPanel = new JPanel();
        logPane = new JPanel();
        browserPanel = new JPanel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GroupLayout settingsPanelLayout = new GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
                settingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 1216, Short.MAX_VALUE)
        );
        settingsPanelLayout.setVerticalGroup(
                settingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 586, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Log", new LogPanel());

        GroupLayout browserPanelLayout = new GroupLayout(browserPanel);
        browserPanel.setLayout(browserPanelLayout);
        browserPanelLayout.setHorizontalGroup(
                browserPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 1216, Short.MAX_VALUE)
        );
        browserPanelLayout.setVerticalGroup(
                browserPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 586, Short.MAX_VALUE)
        );

        new Browser();

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jTabbedPane1, GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>

    private JPanel browserPanel;
    private JTabbedPane jTabbedPane1;
    private JPanel logPane;
    private JPanel queuePanel;
    private JPanel farmingPanel;
    private JPanel settingsPanel;

    public JTabbedPane getjTabbedPane1() {
        return jTabbedPane1;
    }


}

