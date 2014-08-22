package com.Test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import com.Scrape.ConnectPubMedTrends;

public class TestPubmedGraphs {
	JDialog googletrends;
	JDialog pickTrends;
	JFreeChart chart;
	ChartPanel cp;
	XYDataset ds;
	String[] inputs;

	public TestPubmedGraphs(String input) {
		pubmedTrends(input);
	}
	
	private void reload(String input){
		googletrends.removeAll();
		googletrends.setVisible(false);
		pickTrends.removeAll();
		pickTrends.setVisible(false);
		
		pubmedTrends(input);
	}

	private void pubmedTrends(String input) {
		googletrends = new JDialog();
		googletrends.setTitle("PubMed Keyword Trends");

		ds = createDataset(input);
		// JFreeChart chart = ChartFactory.createBarChart("Keyword Trends",
		// "Year", "No Papers Published", (CategoryDataset) ds);
		chart = ChartFactory.createXYLineChart("Keyword Trends",
				"Year", "No. Papers with Keyword", ds,
				PlotOrientation.VERTICAL, true, true, false);
		
		cp = new ChartPanel(chart);
		cp.setPopupMenu(setPopup());

		googletrends.add(cp);
		googletrends.pack();
		googletrends.setSize(600, 400);
		googletrends.setLocationRelativeTo(null);
		googletrends.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		googletrends.setVisible(true);
	}

	private XYDataset createDataset(String input) {
		inputs = input.split(", ");

		DefaultXYDataset ds = new DefaultXYDataset();

		for (String key : inputs) {
			ArrayList<Double> year = new ArrayList<Double>();
			ArrayList<Double> entry = new ArrayList<Double>();

			ConnectPubMedTrends p = new ConnectPubMedTrends(key);
			HashMap<Integer, Integer> map = p.getYearMap();
			if (!map.isEmpty()) {
				Iterator<Entry<Integer, Integer>> i = map.entrySet().iterator();
				while (i.hasNext()) {
					Entry<Integer, Integer> e = i.next();
					year.add(Double.parseDouble("" + e.getKey()));
					entry.add(Double.parseDouble("" + e.getValue()));

				}
				Object[] yearObj = year.toArray();
				Object[] entryObj = entry.toArray();

				double[] yeardouble = new double[yearObj.length];
				double[] entrydouble = new double[entryObj.length];

				for (int j = 0; j < yeardouble.length; j++) {
					yeardouble[j] = Double.parseDouble("" + yearObj[j]);
				}

				for (int j = 0; j < entrydouble.length; j++) {
					entrydouble[j] = Double.parseDouble("" + entryObj[j]);

				}

				double[][] data = { yeardouble, entrydouble };

				ds.addSeries(key, data);
			}
		}
		return ds;

	}
	
	public JPopupMenu setPopup(){
		JPopupMenu m = new JPopupMenu();
		JMenuItem select = new JMenuItem("Select Keywords to Display");
		select.setMnemonic(KeyEvent.VK_S);
		select.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				toSelectMenu();
			}
			
		});
		JMenuItem cancel = new JMenuItem("Cancel");
		cancel.setMnemonic(KeyEvent.VK_C);
		m.add(select);
		m.add(cancel);
		return m;
	}
	
	public void toSelectMenu(){
		pickTrends = new JDialog();
		pickTrends.setTitle("Pick Trends to Show");
		final JButton button = new JButton("Re-draw");
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		final JCheckBox[] options = new JCheckBox[ds.getSeriesCount()];
		for(int i = 0; i<ds.getSeriesCount(); i++){
			System.out.println(ds.getSeriesKey(i).toString());
			options[i] = new JCheckBox(ds.getSeriesKey(i).toString());
			content.add(options[i]);
		}
		content.add(Box.createVerticalStrut(15));
		content.add(button);

		button.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String toReturn = "";
					for(JCheckBox b : options){
						if(b.isSelected()){
							toReturn += b.getText() +", ";
						}
					}
					System.out.println(toReturn);
				reload(toReturn);
			}
			
		});
		
		pickTrends.add(new JScrollPane(content));
		pickTrends.pack();
		pickTrends.setSize(200, 200);
		pickTrends.setLocationRelativeTo(null);
		pickTrends.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pickTrends.setVisible(true);
				
	}

	public static void main(String[] args) {
		TestPubmedGraphs g = new TestPubmedGraphs("Hypertension, Stroke, Insomnia");
	}
}
