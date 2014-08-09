package com.Scrape;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLDivElement;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHeading3;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class ConnectScholarCited {
	String title;
	int totalJournals;
	int citations;

	// DatabaseConnector db;

	public ConnectScholarCited(String t) {
		title = t;
		citations = -1;
		// db = new DatabaseConnector();

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
		
		System.out.println(citations);
	}

	private void search() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {

		HtmlPage home = null;

		String titleBroken = title;
		titleBroken = titleBroken.replace(" ", "+");
		
		Document doc = Jsoup.connect("http://scholar.google.ca/scholar?q="
				+ titleBroken + "&as_sdt=1").userAgent("Chrome").get();
		// System.out.println(home.asText());

		// final String pageAsText = home.asText();
		accessForm(doc);

	}

	private void accessForm(Document home)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {

		Elements elements = home.getElementsByClass("gs_ri");
		
		for(Element e : elements){
			process(e.getElementsByClass("gs_rt").get(0).text().toUpperCase(), e.getElementsByClass("gs_fl").get(0).text().toUpperCase());
		}
		
	}
	
	private void process (String a, String b){
		if(a.contains(title.toUpperCase())){
			if(b.contains("CITED BY ")){
			int index = b.indexOf("CITED BY ") + 9;
			int spaceIndex = b.indexOf(" ", index);
			citations = Integer.parseInt(b.substring(index,spaceIndex));
			}else{
				citations = 0;
			}
		}
		
	}
	
	public int getCitations() {
		return citations;
	}

//	public static void main(String[] args) {
//		long start = System.currentTimeMillis();
//		ConnectScholarCited j = new ConnectScholarCited(
//				"Identifying the vulnerable patient with rupture-prone plaque");
//		System.out.println(System.currentTimeMillis() - start);
//	}
}
