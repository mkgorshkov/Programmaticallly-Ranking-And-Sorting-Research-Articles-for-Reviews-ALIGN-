package com.Scrape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.ImExport.ImportXML;
import com.SQL.DatabaseConnector;

/**
 * Organizes the different types of data that exists for the individual papers in a format 
 * which can be displayed in the main table.
 * @author Maxim Gorshkov
 *
 */
public class ImputDataOrganizer {
	
	private final String CRT_YEAR = "2012";
	private final String CRT_YEAR_CITATIONS = "2014";
	
	private HashMap<String, ArrayList<String>[]> pOverallData;
	private ArrayList<String[]> pRanked;
	private DatabaseConnector pDatabaseConnection;
	private String pXMLPath;
	
	/**
	 * Constructor. Takes in the path to the XML file and starts to collect data.
	 * @param path - String.
	 */
	public ImputDataOrganizer(String path){
		pOverallData = new HashMap<String, ArrayList<String>[]>();
		pRanked = new ArrayList<String[]>();
		pDatabaseConnection = new DatabaseConnector();
		pXMLPath = path;
		
		getData();
		filter();
		sortRank();
	}
	
	/**
	 * Parses the XML file.
	 */
	private void getData(){
		ImportXML x = new ImportXML(pXMLPath);
		pOverallData = x.returnData();
	}
	
	/**
	 * Filters data through the various scraping tools to retrieve overall data.
	 */
	private void filter(){
		Iterator<Entry<String, ArrayList<String>[]>> it = pOverallData.entrySet().iterator();
		it.next();
		while(it.hasNext()){			
			String[] temp = new String[16];

			Entry<String, ArrayList<String>[]> e = it.next();
			
			String pubyear = e.getValue()[3].get(0);
			
			String a = e.getKey();
			System.out.println(a);
			double current = pDatabaseConnection.getImpactFactor(e.getValue()[1].get(0), CRT_YEAR);
			double atpub = 0.0;

			if(Integer.parseInt(pubyear) >= Integer.parseInt(CRT_YEAR)){
				atpub = current;
			}else{
				atpub = pDatabaseConnection.getImpactFactor(e.getValue()[1].get(0), pubyear);
			}
			
			
			
			//firstAuthor
			ConnectHIndex h = new ConnectHIndex(e.getValue()[0].get(0));
			temp[7] = ""+h.getHIndex();
			//LastAuthor
			ConnectHIndex h2 = new ConnectHIndex(e.getValue()[0].get(e.getValue()[0].size()-1));
			temp[13] = ""+h2.getHIndex();
			
			GetIFRankInField g = new GetIFRankInField(e.getValue()[1].get(1), pubyear);
			 temp[14] = ""+g.gethIndex();
			 temp[2] = ""+g.getRanking();
			
			GetIFRankInField g2 = new GetIFRankInField(e.getValue()[1].get(1), CRT_YEAR);
			temp[15] = ""+g2.gethIndex();
			temp[3] = ""+g2.getRanking();
			
			ConnectScholarCited j = new ConnectScholarCited(
					e.getKey());
			if(j.getCitations() == -1){
				temp[6] = "Not Available";
				temp[12] = "Not Available";
			}else{
				temp[6] = ""+j.getCitations();
				int yearDif = (Integer.parseInt(CRT_YEAR_CITATIONS) - Integer.parseInt(pubyear));
				if (yearDif < 1){
					yearDif = 1;
				}
				temp[12] = "" + Double.parseDouble(""+j.getCitations())/Double.parseDouble(""+yearDif);
			}
			
			
			temp[5] = e.getValue()[2].toString();
			
			temp[0] = ""+current;
			temp[2] = "";
			if(current == -1.0){
				temp[1] = e.getKey();
				temp[2] = "No IF Available";
				if(atpub == -1.0){
					temp[3] = "No IF Available";
				}else{
					temp[3] = ""+atpub;
					temp[4] = "("+(current-atpub)+")";
				}
			}else{
				temp[1] = e.getKey();
				temp[2] = ""+current;
				if(atpub == -1.0){
					temp[3] = "No IF Available";
				}else{
					temp[3] = ""+atpub;
					temp[4] = "("+(current-atpub)+")";
				}
			}
			pRanked.add(temp);
			System.out.println(temp);
		}
	}
	/**
	 * Sorts the papers based on the rank of the impact factor.
	 */
	private void sortRank(){
		Collections.sort(pRanked, new Comparator<String[]>(){

			@Override
			public int compare(String[] o1, String[] o2) {
				double a = Double.parseDouble(o1[0]);
				double b = Double.parseDouble(o2[0]);
				
				if(a >= b){
					return -1;
				}
				return 1;
			}
			
		});
		
		for(int i = 0; i<pRanked.size(); i++){
			pRanked.get(i)[10] = calcPercentile(pRanked.get(i)[6], pRanked, 6);
			pRanked.get(i)[11] = calcPercentile(pRanked.get(i)[7], pRanked, 7);
		}
	}
	
	/**
	 * Calculates the percentile based on the values of the other parameters
	 * @param current - Item being compared.
	 * @param input - All items.
	 * @param id - What attribute to compare.
	 * @return String - Percentile.
	 */
	private String calcPercentile(String current, ArrayList<String[]> input, int id){
		String toReturn = "Not available";
		try{
			double currentD = Double.parseDouble(current);
			int below = 0;
			int equal = 0;
			
			for (int i = 0; i < input.size(); i++) {
				try{
					Double.parseDouble(input.get(i)[id]);
					if(Double.parseDouble(input.get(i)[id]) == currentD){
						equal++;
					}else if(Double.parseDouble(input.get(i)[id]) < currentD){
						below++;
					}
				}catch(java.lang.NumberFormatException e){
					
				}
				
			}
			
			toReturn = "";
			toReturn = ""+((below + 0.5*equal)/input.size())*100;
			
			return toReturn;
		}catch(java.lang.NumberFormatException e){
			
		}
		return toReturn;
	}
	
	/**
	 * Return the full attributes of the paper to be used in the table.
	 * @return pRanked ArrayList<String[]> 	
	 */
	public ArrayList<String[]> returnRanked(){
		System.out.println(pRanked);
		return pRanked;
	}
}
