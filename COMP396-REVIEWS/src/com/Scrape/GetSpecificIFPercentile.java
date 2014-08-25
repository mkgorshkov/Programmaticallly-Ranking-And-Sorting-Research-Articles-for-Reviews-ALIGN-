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
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class GetSpecificIFPercentile {
	String journal;
	String year;
	int ranking;
	int hIndex;
	

	// DatabaseConnector db;

	public GetSpecificIFPercentile(String jrnl, String yr) {
		journal = jrnl;
		year = yr;
		
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

	}

	private void search() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		HtmlPage home = null;

		String inputBroken = journal.replace(" ", "+");
		Document doc = Jsoup.connect("http://www.scimagojr.com/journalsearch.php?q="+inputBroken).userAgent("Chrome").get();

		 accessForm(doc);
		
		
	}
	
	private void accessForm(Document doc) throws IOException{
		if(doc.toString().contains("Sorry, your Search Query did not match any journal.")){
			ranking = -1;
		}else{
			int index = doc.toString().indexOf("1. ");
			String subDoc = doc.toString().substring(index+12, index+100);
			index = subDoc.indexOf("\">");
			subDoc = subDoc.substring(0, index);
			subDoc = subDoc.replace("&amp;", "&");
			Document doc2 = Jsoup.connect("http://www.scimagojr.com/"+subDoc).userAgent("Chrome").get();
			accessNext(doc2);
		}
		
	}
	
	private void accessNext(Document doc) throws IOException{
		if(doc.toString().contains("H Index")){
			int index = doc.toString().indexOf("H Index");
			String subString = doc.toString().substring(index+18);
			index = subString.indexOf("<");
			subString = subString.substring(0, index);
			hIndex = Integer.parseInt(subString);
		}else{
			hIndex = -1;
		}
		int index2 = doc.toString().indexOf("category");
		String subString2 = doc.toString().substring(index2);
		index2 = subString2.indexOf("\"");
		subString2 = subString2.substring(9, index2);
		
		Document doc3 = Jsoup.connect("http://www.scimagojr.com/journalrank.php?area=0&category="+subString2+"&country=all&year="+year+"&order=sjr&min=0&min_type=cd").userAgent("Chrome").get();
		accessLast(doc3, subString2);
	}
	
	private void accessLast(Document doc, String subString) throws IOException{
		int titleCount = 0;
		int page = -1;
		for(int i = 0; i<10; i++){
			if(doc.toString().toLowerCase().contains("details\">"+journal.toLowerCase()+"</a>")){
				System.out.println(i);
				page = i-1;
				break;
			}else{
				doc = Jsoup.connect("http://www.scimagojr.com/journalrank.php?area=0&category="+subString+"&country=all&year="+year+"&order=sjr&min=0&min_type=cd&page="+i).userAgent("Chrome").get();
			}
			
			Document copyDoc = doc.clone();
			String[] s = copyDoc.toString().split("<td class=\"tit\">");
			for(int j = 0; j<s.length; j++){
				if(s[j].contains(journal)){
					break;
				}else{
					titleCount++;
				}
			}
		}
	
		ranking = titleCount-1;
		
	}
	
	public int getRanking(){
		return ranking;
	}
	public int gethIndex(){
		return hIndex;
	}
//	public static void main(String[] args) {
//		long start = System.currentTimeMillis();
//		GetSpecificIFPercentile j = new GetSpecificIFPercentile(
//				"Canadian Journal of Cardiology", "2014");
//		System.out.println(System.currentTimeMillis() - start);
//	}
}

