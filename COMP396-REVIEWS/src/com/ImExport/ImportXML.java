package com.ImExport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parse XML data into information that is useful to ALIGN.
 * @author Maxim Gorshkov
 *
 */
public class ImportXML {
	//Input file.
	private File pFile;
	//Overall records.
	private HashMap<String, ArrayList<String>[]> pRecords;
	
	//Broken up data as lists.
	private ArrayList<String> pAuthors = new ArrayList<String>();
	private ArrayList<String> pJournals = new ArrayList<String>();
	private ArrayList<String> pKeywords = new ArrayList<String>();
	private ArrayList<String> pYear = new ArrayList<String>();
	
	/**
	 * Constructor. 
	 * @param fileName - String representing the full file path as input.
	 */
	public ImportXML(String fileName) {
		pFile = new File(fileName);
		pRecords = new HashMap<String, ArrayList<String>[]>();
		parse();
	}

	/**
	 * Connect to file and parse data into a temporary document that can later
	 * be broken into necessary categories.
	 */
	private void parse() {
		try {

			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();

			Document doc = dBuilder.parse(pFile);


			if (doc.hasChildNodes()) {

				parseNode(doc.getChildNodes());

			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}

	/**
	 * More specific parsing function where nodes are parsed into the specific categories
	 * of Authors/Journals/Keywords/Year.
	 * 
	 * @param nodeList - List of Nodes from XML input file.
	 */
	@SuppressWarnings("unchecked")
	private void parseNode(NodeList nodeList) {
		String title = "";

		for (int count = 0; count < nodeList.getLength(); count++) {

			Node tempNode = nodeList.item(count);

			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

				if(tempNode.getNodeName().equals("record")){
					@SuppressWarnings("rawtypes")
					ArrayList[] temp = {pAuthors, pJournals, pKeywords, pYear};
					pRecords.put(title, temp);
					
					title = "";
					
					pAuthors = new ArrayList<String>();
					pJournals = new ArrayList<String>();
					pKeywords = new ArrayList<String>();
					pYear = new ArrayList<String>();
				}
				if(tempNode.getNodeName().equals("author")){
					pAuthors.add(tempNode.getTextContent());
				}
				if(tempNode.getNodeName().equals("title")){
					title = tempNode.getTextContent();
				}
				if(tempNode.getNodeName().equals("secondary-title")){
					pJournals.add(tempNode.getTextContent());
				}
				if(tempNode.getNodeName().equals("alt-title")){
					pJournals.add(tempNode.getTextContent());
				}
				if(tempNode.getNodeName().equals("keyword")){
					pKeywords.add(tempNode.getTextContent());

				}
				if(tempNode.getNodeName().equals("year")){
					pYear.add(tempNode.getTextContent());
				}

				if (tempNode.hasChildNodes()) {
					// loop again if has child nodes
					parseNode(tempNode.getChildNodes());

				}

			}

		}
	}
	
	/**
	 * Access point to the dataset of the XML file.
	 * 
	 * Expected keys:
	 * 	String [Title of Paper]
	 * Expected values:
	 * 	ArrayList<String> authors
	 * 		List of authors
	 *  ArrayList<String> journals
	 *  	Short title of Journal, Full title of Journal
	 *  ArrayList<String> keywords
	 *  	List of keywords
	 *  ArrayList<String> year
	 *  	Year
	 * 
	 * @return HashMap<String, ArrayList<String>[]>
	 * 
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<String>[]> returnData(){
		return (HashMap<String, ArrayList<String>[]>) pRecords.clone();
	}

}
