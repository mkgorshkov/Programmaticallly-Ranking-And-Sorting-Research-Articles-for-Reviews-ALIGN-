package com.Scrape;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class ConnectWoK {
	String startURL;
	String title;
	WebClient webClient;
	int totalJournals;
	int citations;

	// DatabaseConnector db;

	public ConnectWoK(String t) {
		startURL = "";
		title = t;
		webClient = new WebClient(BrowserVersion.CHROME);
		citations = -1;
		// db = new DatabaseConnector();

		try {
			login();
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
		close();
	}

	private void login() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {

		webClient.getOptions().setThrowExceptionOnScriptError(false);

		HtmlPage home = null;

		home = webClient.getPage("http://webofknowledge.com/WOS");

		final String pageAsText = home.asText();
		accessForm(home);

	}

	private void accessForm(HtmlPage home)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {
		// Get the form that we are dealing with and within that form,
		// find the submit button and the field that we want to change.
		HtmlForm form = home.getFormByName("WOS_GeneralSearch_input_form");

		HtmlTextInput titleInput = form.getInputByName("value(input1)");
		titleInput.setText(title);
		titleInput.type(title);

		form = home.getFormByName("WOS_GeneralSearch_input_form");

		HtmlImageInput button = (HtmlImageInput) form.getByXPath(
				"//*[@id=\"WOS_GeneralSearch_input_form_sb\"]").get(0);

		// Now submit the form by clicking the button and get back the second
		// page.
		HtmlPage page2 = (HtmlPage) button.click();
		String page2XML = page2.asText();
		System.out.println(page2XML);
		parsePage(page2);
	}

	private void parsePage(HtmlPage p) {
		String fullpage = p.asText();
		fullpage = fullpage.substring(600);
		int titleIndex = -1;
		String forCitation = fullpage;
		if(fullpage.contains("+ Add Another Field | Reset Form")){
			citations = -1;
			System.out.println(citations);
		}
		else if(fullpage.contains(title)){
			titleIndex = fullpage.indexOf(title);
			forCitation = forCitation.substring(titleIndex);
			int indexCitation = forCitation.indexOf("Times Cited: ");
			forCitation = forCitation.substring(indexCitation+13);
			indexCitation = forCitation.indexOf("\n");
			citations = Integer.parseInt(forCitation.substring(0, indexCitation).trim());
			System.out.println(citations);
		}
		
	}

	private void close() {
		webClient.closeAllWindows();
	}

	public static void main(String[] args) {

		ConnectWoK j = new ConnectWoK(
				"Identifying the vulnerable patient with rupture-prone plaque");

	}
}
