package com.CMD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.ImExport.ImportXML;
import com.SQL.DatabaseConnector;
import com.Scrape.ConnectHIndex;
import com.Scrape.ConnectScholarCited;

public class ImpactFactorFilter {
	
	final String CrtYEAR = "2014";
	
	HashMap<String, ArrayList<String>[]> data;
	ArrayList<String[]> ranked;
	DatabaseConnector db;
	String XMLPath;
	
	public ImpactFactorFilter(String path){
		data = new HashMap<String, ArrayList<String>[]>();
		ranked = new ArrayList<String[]>();
		db = new DatabaseConnector();
		XMLPath = path;
		
		getData();
		filter();
		showRank();
	}
	
	private void getData(){
		ImportXML x = new ImportXML(XMLPath);
		data = x.returnData();
	}
	
	private void filter(){
		Iterator<Entry<String, ArrayList<String>[]>> it = data.entrySet().iterator();
		it.next();
		while(it.hasNext()){			
			String[] temp = new String[14];

			Entry<String, ArrayList<String>[]> e = it.next();
			
			String pubyear = e.getValue()[3].get(0);
			
			String a = e.getKey();
			double current = db.getImpactFactor(e.getValue()[1].get(0), CrtYEAR);
			double atpub = 0.0;

			if(Integer.parseInt(pubyear) >= Integer.parseInt(CrtYEAR)){
				atpub = current;
			}else{
				atpub = db.getImpactFactor(e.getValue()[1].get(0), pubyear);
			}
			
			//firstAuthor
			ConnectHIndex h = new ConnectHIndex(e.getValue()[0].get(0));
			temp[7] = ""+h.hIndex();
			//LastAuthor
			ConnectHIndex h2 = new ConnectHIndex(e.getValue()[0].get(e.getValue()[0].size()-1));
			temp[13] = ""+h2.hIndex();
			
			ConnectScholarCited j = new ConnectScholarCited(
					e.getKey());
			if(j.getCitations() == -1){
				temp[6] = "Not Available";
				temp[12] = "Not Available";
			}else{
				temp[6] = ""+j.getCitations();
				int yearDif = (Integer.parseInt(CrtYEAR) - Integer.parseInt(pubyear));
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
			ranked.add(temp);
		}
	}
	
	private void showRank(){
		Collections.sort(ranked, new Comparator<String[]>(){

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
		
		for(int i = 0; i<ranked.size(); i++){
			System.out.println("Rank "+(i+1));
			System.out.println(ranked.get(i)[1]);
			System.out.println(ranked.get(i)[2]);
			ranked.get(i)[8] = calcPercentile(ranked.get(i)[2], ranked, 2);

			System.out.println(ranked.get(i)[3]);
			ranked.get(i)[9] = calcPercentile(ranked.get(i)[3], ranked, 3);

			System.out.println(ranked.get(i)[4]);
			
			System.out.println(ranked.get(i)[6]);
			ranked.get(i)[10] = calcPercentile(ranked.get(i)[6], ranked, 6);

			System.out.println(ranked.get(i)[7]);
			ranked.get(i)[11] = calcPercentile(ranked.get(i)[7], ranked, 7);

			System.out.println();
		}
	}
	
	private String calcPercentile(String current, ArrayList<String[]> input, int id){
		String toReturn = "Not available";
		try{
			double total = 0;
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
	 * get(i) [1] Title
	 * get(i) [2] Current Impact
	 * get(i) [3] Old Impact
	 * get(i) [4] Keywords
	 * @return
	 */
	public ArrayList<String[]> returnRanked(){
		return ranked;
	}
	
	public static void main(String[] args) {
		ImpactFactorFilter f = new ImpactFactorFilter("./res/TestLibrary.xml");
	}
}
