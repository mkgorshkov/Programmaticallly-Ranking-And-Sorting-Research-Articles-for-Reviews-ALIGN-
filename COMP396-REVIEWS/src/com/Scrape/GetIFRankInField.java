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

/**
 * Return the H-Index of the journal in which the paper is published and rank the journal amongst others in 
 * the specific field in which the journal is published.
 * 
 * @author Maxim Gorshkov
 *
 */
public class GetIFRankInField {
	private String pJournal;
	private String pYear;
	private int pRanking;
	private int pHIndex;
	

	/**
	 * Constructor taking in journal name and specific year.
	 * @param jrnl - String
	 * @param yr - String
	 */
	public GetIFRankInField(String jrnl, String yr) {
		pJournal = jrnl;
		pYear = yr;
		
		try {
			search();
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Make a general search for the journal name on the SCIMagoJR website.
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void search() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {

		String inputBroken = pJournal.replace(" ", "+");
		Document doc = Jsoup.connect("http://www.scimagojr.com/journalsearch.php?q="+inputBroken).userAgent("Chrome").get();

		 accessForm(doc);
	}
	
	/**
	 * As long as the journal is on the website, we continue to gather information about the specific journal. Otherwise, we give it 
	 * a -1 ranking.
	 * @param doc - Document
	 * @throws IOException
	 */
	private void accessForm(Document doc) throws IOException{
		if(doc.toString().contains("Sorry, your Search Query did not match any journal.")){
			pRanking = -1;
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
	
	/**
	 * If the HIndex exists, save it. Continue on within the specific category to get the ranking.
	 * @param doc
	 * @throws IOException
	 */
	private void accessNext(Document doc) throws IOException{
		if(doc.toString().contains("H Index")){
			int index = doc.toString().indexOf("H Index");
			String subString = doc.toString().substring(index+18);
			index = subString.indexOf("<");
			subString = subString.substring(0, index);
			pHIndex = Integer.parseInt(subString);
		}else{
			pHIndex = -1;
		}
		int index2 = doc.toString().indexOf("category");
		String subString2 = doc.toString().substring(index2);
		index2 = subString2.indexOf("\"");
		subString2 = subString2.substring(9, index2);
		
		Document doc3 = Jsoup.connect("http://www.scimagojr.com/journalrank.php?area=0&category="+subString2+"&country=all&year="+pYear+"&order=sjr&min=0&min_type=cd").userAgent("Chrome").get();
		accessLast(doc3, subString2);
	}
	
	/**
	 * Traverse through the list of ranked documents until the specific journal is found. This ranking is recorded.
	 * @param doc
	 * @param subString
	 * @throws IOException
	 */
	private void accessLast(Document doc, String subString) throws IOException{
		int titleCount = 0;
		int page = -1;
		for(int i = 0; i<10; i++){
			if(doc.toString().toLowerCase().contains("details\">"+pJournal.toLowerCase()+"</a>")){
				System.out.println(i);
				page = i-1;
				break;
			}else{
				doc = Jsoup.connect("http://www.scimagojr.com/journalrank.php?area=0&category="+subString+"&country=all&year="+pYear+"&order=sjr&min=0&min_type=cd&page="+i).userAgent("Chrome").get();
			}
			
			Document copyDoc = doc.clone();
			String[] s = copyDoc.toString().split("<td class=\"tit\">");
			for(int j = 0; j<s.length; j++){
				if(s[j].contains(pJournal)){
					break;
				}else{
					titleCount++;
				}
			}
		}
	
		pRanking = titleCount-1;
		
	}
	
	/**
	 * Return the ranking of the journal.
	 * @return Integer
	 */
	public int getRanking(){
		return pRanking;
	}
	/**
	 * Return the HIndex of the journal.
	 * @return Integer
	 */
	public int gethIndex(){
		return pHIndex;
	}
}

