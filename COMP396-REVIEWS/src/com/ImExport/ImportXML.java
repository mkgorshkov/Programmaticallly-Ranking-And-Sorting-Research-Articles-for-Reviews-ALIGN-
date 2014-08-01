package com.ImExport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImportXML {
	File f;
	HashMap<String, ArrayList<String>[]> records;
	
	String title = "";
	ArrayList<String> authors = new ArrayList<String>();
	ArrayList<String> journals = new ArrayList<String>();
	ArrayList<String> keywords = new ArrayList<String>();
	ArrayList<String> year = new ArrayList<String>();
	
	public ImportXML(String fileName) {
		f = new File(fileName);
		records = new HashMap<String, ArrayList<String>[]>();
		parse();
	}

	private void parse() {
		try {

			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();

			Document doc = dBuilder.parse(f);


			if (doc.hasChildNodes()) {

				printNote(doc.getChildNodes());

			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}

	private void printNote(NodeList nodeList) {
		

		for (int count = 0; count < nodeList.getLength(); count++) {

			Node tempNode = nodeList.item(count);

			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

				if(tempNode.getNodeName().equals("record")){
					ArrayList[] temp = {authors, journals, keywords, year};
					records.put(title, temp);
					
					title = "";
					
					authors = new ArrayList<String>();
					journals = new ArrayList<String>();
					keywords = new ArrayList<String>();
					year = new ArrayList<String>();
				}
				if(tempNode.getNodeName().equals("author")){
					authors.add(tempNode.getTextContent());
				}
				if(tempNode.getNodeName().equals("title")){
					title = tempNode.getTextContent();
				}
				if(tempNode.getNodeName().equals("secondary-title")){
					journals.add(tempNode.getTextContent());
				}
				if(tempNode.getNodeName().equals("alt-title")){
					journals.add(tempNode.getTextContent());
				}
				if(tempNode.getNodeName().equals("keyword")){
					keywords.add(tempNode.getTextContent());

				}
				if(tempNode.getNodeName().equals("year")){
					year.add(tempNode.getTextContent());
				}

				if (tempNode.hasChildNodes()) {
					// loop again if has child nodes
					printNote(tempNode.getChildNodes());

				}

			}

		}
	}
	
	public HashMap<String, ArrayList<String>[]> returnData(){
		return (HashMap<String, ArrayList<String>[]>) records.clone();
	}

//	public static void main(String[] args) {
//		ImportXML x = new ImportXML("./res/TestLibrary.xml");
//		
//	}
}
