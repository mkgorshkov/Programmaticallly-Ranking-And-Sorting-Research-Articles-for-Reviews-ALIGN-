package com.Filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.SQL.DatabaseConnector;

public class ImpactFactor {
	
	HashMap<String, Double> data;
	
	public ImpactFactor(){
		data = new HashMap<String, Double>();
		fillImpactFactors();
	}
	
	private void fillImpactFactors(){
		
		ArrayList<String> title = new ArrayList<String>();
		ArrayList<String> impact = new ArrayList<String>();
		
		try {
			Document doc = Jsoup.connect("http://www.citefactor.org/journal-impact-factor-list-2012.html").get();
			Elements tables = doc.select(".tableizer-table");
			Elements tr = tables.select("tr");
			Elements td = tables.select("td");

			int c = 0;
			for(Element s : td){
				if(c == 0){
					title.add(s.toString().substring(4, s.toString().length()-5));
				}
				if(c == 3){
					impact.add(s.toString().substring(4, s.toString().length()-5));
				}
				c = (c+1)%8;
				//System.out.println(c);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		title.remove(title.size()-1);
		
		System.out.println(title.size());
		System.out.println(impact.size());
		
		DatabaseConnector d = new DatabaseConnector();
		
		for(int i = 0; i<title.size(); i++){
			Double im = 0.0;
			
			if(!impact.get(i).isEmpty() && !impact.get(i).equals(" ")){
				if(impact.get(i).contains("/td&gt;")){
					im = Double.parseDouble(impact.get(i).substring(0, impact.get(i).length()-7));
				}else{
					im = Double.parseDouble(impact.get(i));
				}
			}
			
			data.put(title.get(i), im);
			System.out.println(d.addImpactFactors(title.get(i), im));
		}
	}
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		ImpactFactor f = new ImpactFactor();		
		
		System.out.println("Time: "+(System.currentTimeMillis() - start)+" ms");
	}
	
	public HashMap<String, Double> getImpactFactor(){
		return (HashMap<String, Double>) data.clone();
	}
	
}
