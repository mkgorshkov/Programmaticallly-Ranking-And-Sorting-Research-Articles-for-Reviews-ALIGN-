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

/**
 * Retrieve the number of citations for a specific paper by retrieving data from Google Scholar.
 * @author Maxim Gorshkov
 *
 */
public class ConnectScholarCited {
	private String pTitle;
	private int pCitations;

	/**
	 * Constructor. As input, the title of the paper is taken.
	 * @param titleInput
	 */
	public ConnectScholarCited(String titleInput) {
		pTitle = titleInput;
		pCitations = -1;

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
	 * Search for entry on Google Scholar.
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void search() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {


		String titleBroken = pTitle;
		titleBroken = titleBroken.replace(" ", "+");
		
		Document doc = Jsoup.connect("http://scholar.google.ca/scholar?q="
				+ titleBroken + "&as_sdt=1").userAgent("Chrome").get();

		accessForm(doc);

	}

	/**
	 * As long as there is a match, continue to parse the returned rows.
	 * @param home - Document
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void accessForm(Document home)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {

		Elements elements = home.getElementsByClass("gs_ri");
		
		for(Element e : elements){
			process(e.getElementsByClass("gs_rt").get(0).text().toUpperCase(), e.getElementsByClass("gs_fl").get(0).text().toUpperCase());
		}
		
	}
	
	/**
	 * If the title is a perfect match, parse how many citations the paper has.
	 * @param stringInput String
	 * @param titleToCheck String
	 */
	private void process (String stringInput, String titleToCheck){
		if(stringInput.contains(pTitle.toUpperCase())){
			if(titleToCheck.contains("CITED BY ")){
			int index = titleToCheck.indexOf("CITED BY ") + 9;
			int spaceIndex = titleToCheck.indexOf(" ", index);
			pCitations = Integer.parseInt(titleToCheck.substring(index,spaceIndex));
			}else{
				pCitations = 0;
			}
		}
		
	}
	
	/**
	 * Returns the number of citations for current paper.
	 * @return Integer.
	 */
	public int getCitations() {
		return pCitations;
	}

}
