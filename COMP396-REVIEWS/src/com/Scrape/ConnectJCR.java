package com.Scrape;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class ConnectJCR {

	String startURL;
	int pubYear;
	WebClient webClient;
	int totalJournals;

	public ConnectJCR(String url, int year) {
		startURL = url;
		pubYear = year;
		webClient = new WebClient();

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

	private void login() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		HtmlPage home = null;

		home = webClient
				.getPage("http://admin-router.webofknowledge.com/?DestApp=JCR");

		final String pageAsText = home.asText();
		accessForm(home);
		
		
		
	}
	
	private void accessForm(HtmlPage home) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		 // Get the form that we are dealing with and within that form, 
	    // find the submit button and the field that we want to change.
	    final HtmlForm form = home.getFormByName("limits");
	    
	    final HtmlSelect year = form.getSelectByName("science_year");
	    final List<HtmlRadioButtonInput> select = form.getRadioButtonsByName("RQ");

	    // Change the value of the text field
	    year.setSelectedAttribute(""+pubYear, true);
	    for(HtmlRadioButtonInput h : select){
	    	if(h.getValueAttribute().equals("SELECT_ALL")){
	    		h.click();
	    	}
	    }
	    	    
	    // Now submit the form by clicking the button and get back the second page.
	    HtmlPage page2 = webClient.getPage("javascript:check_limits();");
	    String page2XML = page2.asXml();
	    totalNumberJournals(page2XML);
	    
  
	    int recordValue = 1;
	    boolean end = false;
	    while(!end){
	 	    if(page2.asText().contains(totalJournals+" (of "+totalJournals)) end = true;
	 	    parsePage(page2);
	 	    recordValue += 20;
	 	    page2 = webClient.getPage("http://admin-apps.webofknowledge.com/JCR/JCR?RQ=SELECT_ALL&cursor="+recordValue);
	    }
	}
	
	private void totalNumberJournals(String s){
		int journalValueIndex = s.indexOf(" (of ");
		String sub = s.substring(journalValueIndex+5);
		
		int endBracket = sub.indexOf(")");
		String journals = sub.substring(0, endBracket);
		totalJournals = Integer.parseInt(journals);
	}
	
	private void parsePage(HtmlPage p){
		HtmlTable table = (HtmlTable) p.getByXPath("/html/body/form/center/p/table[1]").get(0);
		
		List<HtmlTableRow> rows = table.getRows();
		for(int r = 2; r<rows.size(); r++){
		
			List<HtmlTableCell> cells = rows.get(r).getCells();
			
			System.out.print("[");
			for(int c = 2; c<cells.size(); c++){
				if(c == cells.size() - 1){
					System.out.print(cells.get(c).asText());
				}else{
					System.out.print(cells.get(c).asText()+",");
				}
			}
			System.out.print("]\n");
		}
	}

	private void close() {
		webClient.closeAllWindows();
	}

	public static void main(String[] args) {
		long Overallstart = System.currentTimeMillis();
		System.out.println("FULL DUMP");
		
		for(int i = 1997; i<2013; i++){
		System.out.println("Year: "+i);
		
		long start = System.currentTimeMillis();
		
		ConnectJCR j = new ConnectJCR(
				"http://admin-router.webofknowledge.com/?DestApp=JCR", i);
		
		
		System.out.println("Running Time: "+(System.currentTimeMillis() - start) + " ms");
		}
		
		System.out.println("END FULL DUMP");
		System.out.println("Total dump time: "+(System.currentTimeMillis() - Overallstart)+ " ms");
	}

}
