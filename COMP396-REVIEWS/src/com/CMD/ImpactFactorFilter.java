package com.CMD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.ImExport.ImportXML;
import com.SQL.DatabaseConnector;

public class ImpactFactorFilter {
	
	final String CrtYEAR = "2012";
	
	HashMap<String, ArrayList<String>[]> data;
	ArrayList<String[]> ranked;
	DatabaseConnector db;
	
	public ImpactFactorFilter(){
		data = new HashMap<String, ArrayList<String>[]>();
		ranked = new ArrayList<String[]>();
		db = new DatabaseConnector();
		
		getData();
		filter();
		showRank();
	}
	
	private void getData(){
		ImportXML x = new ImportXML("./res/TestLibrary.xml");
		data = x.returnData();
	}
	
	private void filter(){
		Iterator<Entry<String, ArrayList<String>[]>> it = data.entrySet().iterator();
		it.next();
		while(it.hasNext()){
			
			
			String[] temp = new String[2];
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
			
			temp[0] = ""+current;
			if(current == -1.0){
				temp[1] = e.getKey() + "\n"+e.getValue()[1].get(1)+"\n"+ "Current: No IF Available\n";
				if(atpub == -1.0){
					temp[1] += "At Publication: No IF Available";
				}else{
					temp[1] += "At Publication: "+atpub+" ("+(current-atpub)+")";
				}
			}else{
				temp[1] = e.getKey() + "\n"+e.getValue()[1].get(1)+"\nCurrent: "+ current+"\n";
				if(atpub == -1.0){
					temp[1] += "At Publication: No IF Available";
				}else{
					temp[1] += "At Publication: "+atpub+" ("+(current-atpub)+")";
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
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		ImpactFactorFilter f = new ImpactFactorFilter();
	}
}
