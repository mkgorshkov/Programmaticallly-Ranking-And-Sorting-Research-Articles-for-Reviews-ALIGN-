package com.Scrape;

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

/**
 * Generate a plot of the number of papers published with a specific keyword
 * since the first recorded use to the current date.
 * 
 * @author Maxim Gorshkov
 *
 */
public class PubmedGraphs {
	private JDialog pTrendsDialog;
	private JDialog pPickTrends;
	private JFreeChart pChart;
	private ChartPanel pChartPanel;
	private XYDataset pDataset;
	private String[] pInputs;

	/**
	 * Constrcutor taking in the keywords.
	 * @param input - String
	 */
	public PubmedGraphs(String input) {
		pubmedTrends(input);
	}
	
	/**
	 * Reload the graphs once user has chosen filtered keywords.
	 * @param input - String
	 */
	private void reload(String input){
		pTrendsDialog.removeAll();
		pTrendsDialog.setVisible(false);
		pPickTrends.removeAll();
		pPickTrends.setVisible(false);
		
		pubmedTrends(input);
	}

	/**
	 * Make the dialog from the dataset
	 * @param input - String.
	 */
	private void pubmedTrends(String input) {
		pTrendsDialog = new JDialog();
		pTrendsDialog.setTitle("PubMed Keyword Trends");

		pDataset = createDataset(input);
	
		pChart = ChartFactory.createXYLineChart("Keyword Trends",
				"Year", "No. Papers with Keyword", pDataset,
				PlotOrientation.VERTICAL, true, true, false);
		
		pChartPanel = new ChartPanel(pChart);
		pChartPanel.setPopupMenu(setPopup());

		pTrendsDialog.add(pChartPanel);
		pTrendsDialog.pack();
		pTrendsDialog.setSize(600, 400);
		pTrendsDialog.setLocationRelativeTo(null);
		pTrendsDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pTrendsDialog.setVisible(true);
	}

	/**
	 * Create the dataset from the keywords.
	 * @param input - String
	 * @return XYDataset
	 */
	private XYDataset createDataset(String input) {
		pInputs = input.split(", ");

		DefaultXYDataset ds = new DefaultXYDataset();

		for (String key : pInputs) {
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
	
	/**
	 * Set popup with prompt to let user select subset of keywords.
	 * @return JPopupMenu
	 */
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
	
	/**
	 * Build the selection menu for the subset of keywords.
	 */
	public void toSelectMenu(){
		pPickTrends = new JDialog();
		pPickTrends.setTitle("Pick Trends to Show");
		final JButton button = new JButton("Re-draw");
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		final JCheckBox[] options = new JCheckBox[pDataset.getSeriesCount()];
		for(int i = 0; i<pDataset.getSeriesCount(); i++){
			System.out.println(pDataset.getSeriesKey(i).toString());
			options[i] = new JCheckBox(pDataset.getSeriesKey(i).toString());
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
		
		pPickTrends.add(new JScrollPane(content));
		pPickTrends.pack();
		pPickTrends.setSize(200, 200);
		pPickTrends.setLocationRelativeTo(null);
		pPickTrends.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pPickTrends.setVisible(true);
				
	}

}
