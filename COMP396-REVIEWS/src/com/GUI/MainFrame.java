package com.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Panel;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

/**
 * Main window responsible for holding the container.
 * 
 * @author mkgo
 * 
 */
public class MainFrame extends JFrame {

	JMenuBar menuBar = new JMenuBar();
	JMenu menuFile;
	JPanel statusPanel = new JPanel();
	JLabel statusLabel = new JLabel("");

	public MainFrame() {
		setTitle("Article Quality Sorter");
		setSize(600, 400);
		setLocationRelativeTo(null);

		this.add(addGrid());
		setMenu();
		this.setJMenuBar(menuBar);
		setStatus();
	}

	private void setMenu() {
		menuFile = new JMenu("File");
		JMenuItem exit = new JMenuItem("Exit");
		menuFile.add(exit);
		menuBar.add(menuFile);
	}

	private void setStatus() {
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setPreferredSize(new Dimension(this.getWidth(), 16));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		statusLabel.setText("Front Page");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLabel);
	}

	private Panel addGrid() {
		Panel p = new Panel();

		return p;
	}
}