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
 * Retrieves the H-Index of a specific researcher based on the first and last name.
 * @author Maxim Gorshkov
 *
 */
public class ConnectHIndex {
	private String pAuthor;
	private WebClient pClient;
	private int pHIndex;

	/**
	 * Constructor. Takes as input the full name of an author.
	 * @param auth - String.
	 */
	public ConnectHIndex(String auth) {
		pAuthor = auth;
		pClient = new WebClient();
		
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
	 * Open the main webpage and fill out the form, inputting the full author name.
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void search() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {

		pClient.getOptions().setThrowExceptionOnScriptError(false);
		HtmlPage home = pClient.getPage("http://www.scopus.com/search/form/authorFreeLookup.url");
		HtmlTextInput text1 = (HtmlTextInput) home.getByXPath("//*[@id=\"lastname\"]").get(0);
		HtmlTextInput text2 = (HtmlTextInput) home.getByXPath("//*[@id=\"firstname\"]").get(0);
		
		int index = pAuthor.indexOf(", ");
		
		text1.type(pAuthor.substring(0, index));
		text2.type(pAuthor.substring(index+2, index+3));
		
		if(home.getByXPath("//*[@id=\"authorFreeLookSearh\"]/div[4]/div/input[2]").get(0).equals("")){
			pHIndex = -1;
		}else{
		HtmlPage newPage = ((HtmlInput) home.getByXPath("//*[@id=\"authorFreeLookSearh\"]/div[4]/div/input[2]").get(0)).click();
		process(newPage);
		}
		
	}
	
	/**
	 * As long as the author exists, continue to the personal profile. Retrieve the  H-Index.
	 * @param page - HtmlPage
	 * @throws IOException
	 */
	private void process(HtmlPage page) throws IOException{
		if(page.getByXPath("//*[@id=\"resultDataRow1\"]/div[2]/div[1]/a").size() == 0){
			pHIndex = -1;
		}else{
		HtmlAnchor select = (HtmlAnchor) page.getByXPath("//*[@id=\"resultDataRow1\"]/div[2]/div[1]/a").get(0);
		HtmlPage newPage = select.click();
		HtmlDivision div = (HtmlDivision) newPage.getByXPath("//*[@id=\"ulLeftList\"]/li[3]/div[2]/div").get(0);
			if(div.getTextContent().substring(0, div.getTextContent().indexOf("\n")).equals("")){
				pHIndex = -1;
			}else{
			pHIndex = Integer.parseInt(div.getTextContent().substring(0, div.getTextContent().indexOf("\n")));
			}
		}
	}

	/**
	 * Returns the H-Index of the author.
	 * @return Integer.
	 */
	public int getHIndex(){
		return pHIndex;
	}
}
