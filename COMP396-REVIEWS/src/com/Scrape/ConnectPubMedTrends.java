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
/**
 * Scrape the PubMed website for specific keywords, reconstructing the data related to published papers with a specific keyword.
 * @author Maxim Gorshkov
 *
 */
public class ConnectPubMedTrends {
	private String pKeyword;
	private HashMap<Integer, Integer> pValuesByMonth;
	private HashMap<Integer, Integer> pValuesByYear;

	/**
	 * Constructor. Takes in the  list of keywords as a comma seperated String.
	 * @param keywordInput
	 */
	public ConnectPubMedTrends(String keywordInput) {
		pKeyword = keywordInput;
		pValuesByMonth = new HashMap<Integer, Integer>();
		pValuesByYear = new HashMap<Integer, Integer>();
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
	 * Retrieves the main webpage with details regarding the specific keywords.
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void search() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		
		pKeyword = pKeyword.replace(" ", "+");

		Document doc = Jsoup.connect("http://www.ncbi.nlm.nih.gov/pubmed/?term="+pKeyword).userAgent("Chrome").get();
		process(doc);

	}

	/**
	 * Find the JavaScript entry for the reconstruction of the data.
	 * @param doc
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void process(Document doc)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {

		Element e = doc.getElementById("disc_col");
		e = e.getElementsByAttribute("href").get(0);
		Attributes a = e.attributes();
		open(a.get("href"));
	}
	
	/**
	 * Opens the timeline with the specific values for each month since the first paper until the most current with the keyword.
	 * Generates the month-to-month values for each keyword.
	 * @param specificKeyword
	 * @throws IOException
	 */
	private void open(String specificKeyword) throws IOException{
		Document doc = Jsoup.connect("http://www.ncbi.nlm.nih.gov"+specificKeyword).userAgent("Chrome").get();
		Elements el1 = doc.getElementsByClass("timelineData");
		if(el1.isEmpty()){
			
		}
		else{
			Elements yearLinks = el1.get(0).getElementsByAttribute("value");
			
			for(Element e : yearLinks){
				pValuesByMonth.put(Integer.parseInt(e.attr("value")), Integer.parseInt(e.text()));
			}
			
			generateValuesByYear();
		}
		
	}
	
	/**
	 * Generates the year-to-year values from the month-to-month values.
	 */
	private void generateValuesByYear(){
		Iterator i = pValuesByMonth.keySet().iterator();
		int currentYear = 0;
		int totalValue = 0;
		while(i.hasNext()){
			String tempStr = ""+i.next();
			int temp = Integer.parseInt(tempStr.substring(0, 4));
			System.out.println(temp);
			int tempCount = pValuesByMonth.get(Integer.parseInt(tempStr));
			System.out.println(tempCount);

			if(pValuesByYear.containsKey(temp)){
				int tempValue = pValuesByYear.get(temp);
				tempValue += tempCount;
				pValuesByYear.remove(temp);
				pValuesByYear.put(temp, tempValue);
			}else{
				pValuesByYear.put(temp, tempCount);
			}
		}
	}

	/**
	 * Retrieve the month-to-month dataset.
	 * @return HashMap<Integer, Integer> for MONTHYEAR, VALUE
	 */
	public HashMap<Integer, Integer> getMonthMap() {
		return pValuesByMonth;
	}
	
	/**
	 * Retrieve the year-to-year dataset.
	 * @return HashMap<Integer, Integer> for YEAR, YEAR
	 */
	public HashMap<Integer, Integer> getYearMap(){
		return pValuesByYear;
	}
}
