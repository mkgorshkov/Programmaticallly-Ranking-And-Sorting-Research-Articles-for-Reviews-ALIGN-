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

public class ConnectHIndex {
	String author;
	String affiliation;
	WebClient client;
	int hIndex;
	

	// DatabaseConnector db;

	public ConnectHIndex(String auth) {
		author = auth;
		client = new WebClient();
		
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

		client.getOptions().setThrowExceptionOnScriptError(false);
		HtmlPage home = client.getPage("http://www.scopus.com/search/form/authorFreeLookup.url");
		HtmlTextInput text1 = (HtmlTextInput) home.getByXPath("//*[@id=\"lastname\"]").get(0);
		HtmlTextInput text2 = (HtmlTextInput) home.getByXPath("//*[@id=\"firstname\"]").get(0);
//		HtmlTextInput text3 = (HtmlTextInput) home.getByXPath("//*[@id=\"institute\"]").get(0);
		
		int index = author.indexOf(", ");
		
		text1.type(author.substring(0, index));
		text2.type(author.substring(index+2, index+3));
		
		if(home.getByXPath("//*[@id=\"authorFreeLookSearh\"]/div[4]/div/input[2]").get(0).equals("")){
			hIndex = -1;
		}else{
		HtmlPage newPage = ((HtmlInput) home.getByXPath("//*[@id=\"authorFreeLookSearh\"]/div[4]/div/input[2]").get(0)).click();
		process(newPage);
		}
		
	}
	
	private void process(HtmlPage page) throws IOException{
		if(page.getByXPath("//*[@id=\"resultDataRow1\"]/div[2]/div[1]/a").size() == 0){
			hIndex = -1;
		}else{
		HtmlAnchor select = (HtmlAnchor) page.getByXPath("//*[@id=\"resultDataRow1\"]/div[2]/div[1]/a").get(0);
		HtmlPage newPage = select.click();
		HtmlDivision div = (HtmlDivision) newPage.getByXPath("//*[@id=\"ulLeftList\"]/li[3]/div[2]/div").get(0);
			if(div.getTextContent().substring(0, div.getTextContent().indexOf("\n")).equals("")){
				hIndex = -1;
			}else{
			hIndex = Integer.parseInt(div.getTextContent().substring(0, div.getTextContent().indexOf("\n")));
			}
		}
	}

	public int hIndex(){
		return hIndex;
	}
//	public static void main(String[] args) {
//		long start = System.currentTimeMillis();
//		ConnectHIndex j = new ConnectHIndex(
//				"Daskalopoulou, S.");
//		System.out.println(System.currentTimeMillis() - start);
//	}
}
