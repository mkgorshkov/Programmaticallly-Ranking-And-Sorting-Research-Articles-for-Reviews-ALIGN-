package com.FillDB;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.SQL.DatabaseConnector;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

/**
 * Fill the internal database of ALIGN with all impact factors available from the 
 * Web of Knowledge online database.
 * 
 * @author Maxim Gorshkov
 *
 */
public class FillimpactFactor {

	private String pStartURL;
	private int pPubYear;
	private WebClient pWebClient;
	private int pTotalJournals;
	private DatabaseConnector pDatabaseConnection;

	/**
	 * Constructor taking in the year of the concerned Impact Factors.
	 * @param url
	 * @param year - Integer
	 */
	public FillimpactFactor(String url, int year) {
		pStartURL = url;
		pPubYear = year;
		pWebClient = new WebClient();
		pDatabaseConnection = new DatabaseConnector();

		try {
			login();
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		close();
	}

	/**
	 * Connect to the online datasource to start to retrieve data.
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void login() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		HtmlPage home = null;

		home = pWebClient
				.getPage("http://admin-router.webofknowledge.com/?DestApp=JCR");

		final String pageAsText = home.asText();
		accessForm(home);
		
	}
	
	/**
	 * Access the home page in order to select the specific parameters needed.
	 * @param home
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void accessForm(HtmlPage home) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		 // Get the form that we are dealing with and within that form, 
	    // find the submit button and the field that we want to change.
	    final HtmlForm form = home.getFormByName("limits");
	    
	    final HtmlSelect year = form.getSelectByName("science_year");
	    final List<HtmlRadioButtonInput> select = form.getRadioButtonsByName("RQ");

	    // Change the value of the text field
	    year.setSelectedAttribute(""+pPubYear, true);
	    for(HtmlRadioButtonInput h : select){
	    	if(h.getValueAttribute().equals("SELECT_ALL")){
	    		h.click();
	    	}
	    }
	    	    
	    // Now submit the form by clicking the button and get back the second page.
	    HtmlPage page2 = pWebClient.getPage("javascript:check_limits();");
	    String page2XML = page2.asXml();
	    totalNumberJournals(page2XML);
	    
  
	    int recordValue = 1;
	    boolean end = false;
	    while(!end){
	 	    if(page2.asText().contains(pTotalJournals+" (of "+pTotalJournals)) end = true;
	 	    parsePage(page2);
	 	    recordValue += 20;
	 	    page2 = pWebClient.getPage("http://admin-apps.webofknowledge.com/JCR/JCR?RQ=SELECT_ALL&cursor="+recordValue);
	    }
	}
	
	/**
	 * Calculate the total number of journals to figure out how many pages are needed
	 * to traverse the whole database.
	 * 
	 * @param crtPage - String
	 */
	private void totalNumberJournals(String crtPage){
		int journalValueIndex = crtPage.indexOf(" (of ");
		String sub = crtPage.substring(journalValueIndex+5);
		
		int endBracket = sub.indexOf(")");
		String journals = sub.substring(0, endBracket);
		pTotalJournals = Integer.parseInt(journals);
	}
	
	/**
	 * Parse each specific page in order to retrieve the specific impact factors per page.
	 * @param crtPage
	 */
	private void parsePage(HtmlPage crtPage){
		HtmlTable table = (HtmlTable) crtPage.getByXPath("/html/body/form/center/p/table[1]").get(0);
		
		List<HtmlTableRow> rows = table.getRows();
		for(int r = 2; r<rows.size(); r++){
		
			List<HtmlTableCell> cells = rows.get(r).getCells();
			
				if(cells.get(5).asText().contains(" ")){
					pDatabaseConnection.addImpactFactors(cells.get(2).asText(), String.valueOf(pPubYear), 0.0);
				}else{
					pDatabaseConnection.addImpactFactors(cells.get(2).asText(), String.valueOf(pPubYear), Double.parseDouble(cells.get(5).asText()));
				}
			}
		}

	/**
	 * Close all windows of the open client.
	 */
	private void close() {
		pWebClient.closeAllWindows();
	}

	/**
	 * To fill the database, the following can be uncommented and changed as necessary.
	 */
//	public static void main(String[] args) {
//		long Overallstart = System.currentTimeMillis();
//		System.out.println("FULL DUMP");
//		
//		for(int i = 1997; i<2013; i++){
//		System.out.println("Year: "+i);
//		
//		long start = System.currentTimeMillis();
//		
//		FillimpactFactor j = new FillimpactFactor(
//				"http://admin-router.webofknowledge.com/?DestApp=JCR", i);
//		
//		
//		System.out.println("Running Time: "+(System.currentTimeMillis() - start) + " ms");
//		}
//		
//		System.out.println("END FULL DUMP");
//		System.out.println("Total dump time: "+(System.currentTimeMillis() - Overallstart)+ " ms");
//	}

}
