package com.Helpers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.GUI.MainFrame;

public class addButtonListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton j = (JButton) e.getSource();
		if(j.getText().equals("Connect")){
			JFrame frame = (JFrame) SwingUtilities.getRoot(j);
			frame.removeAll();
			frame.validate();
			frame.repaint();
		}
	}

}
