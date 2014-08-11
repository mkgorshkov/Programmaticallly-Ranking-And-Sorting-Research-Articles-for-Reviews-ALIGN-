package com.Scrape;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;

public class ConnectTrends {
	String keyword;
	WebClient webClient;
	HashMap<Integer, Integer> valuesByMonth;
	HashMap<Integer, Integer> valuesByYear;
	int totalJournals;
	int citations;

	// DatabaseConnector db;

	public ConnectTrends(String k) {
		keyword = k;
		valuesByMonth = new HashMap<Integer, Integer>();
		valuesByYear = new HashMap<Integer, Integer>();
		try {
			search();
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(valuesByMonth);
		System.out.println(valuesByYear);

	}

	private void search() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		
		Document doc = Jsoup.connect("http://www.google.com/trends/fetchComponent?q="+keyword+"&cid=TIMESERIES_GRAPH_0&export=3").get();
		System.out.println(doc);
		//process(doc);

	}

	private void process(Document doc)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {

		Element e = doc.getElementById("disc_col");
		e = e.getElementsByAttribute("href").get(0);
		Attributes a = e.attributes();
		open(a.get("href"));
	}
	
	private void open(String s) throws IOException{
		Document doc = Jsoup.connect("http://www.ncbi.nlm.nih.gov"+s).userAgent("Chrome").get();
		Elements el1 = doc.getElementsByClass("timelineData");
		Elements yearLinks = el1.get(0).getElementsByAttribute("value");
		
		for(Element e : yearLinks){
			valuesByMonth.put(Integer.parseInt(e.attr("value")), Integer.parseInt(e.text()));
		}
		
		generateValuesByYear();
	}
	
	private void generateValuesByYear(){
		Iterator i = valuesByMonth.keySet().iterator();
		int currentYear = 0;
		int totalValue = 0;
		while(i.hasNext()){
			String tempStr = ""+i.next();
			int temp = Integer.parseInt(tempStr.substring(0, 4));
			System.out.println(temp);
			int tempCount = valuesByMonth.get(Integer.parseInt(tempStr));
			System.out.println(tempCount);

			if(valuesByYear.containsKey(temp)){
				int tempValue = valuesByYear.get(temp);
				tempValue += tempCount;
				valuesByYear.remove(temp);
				valuesByYear.put(temp, tempValue);
			}else{
				valuesByYear.put(temp, tempCount);
			}
		}
	}

	public HashMap<Integer, Integer> getMonthMap() {
		return valuesByMonth;
	}
	
	public HashMap<Integer, Integer> getYearMap(){
		return valuesByYear;
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		ConnectTrends j = new ConnectTrends(
				"Aerosol therapy");
		System.out.println(System.currentTimeMillis() - start);
	}
}