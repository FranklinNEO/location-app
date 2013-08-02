package com.neo.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLContentHandler extends DefaultHandler {
	private List<xml_result> xml_results;
	private xml_result xml_result;
	private String preTag;

	public List<xml_result> getxml_result() {
		return xml_results;
	}

	/*
	 * 接收文档的开始的通知。
	 */
	@Override
	public void startDocument() throws SAXException {
		xml_results = new ArrayList<xml_result>();
	}

	/*
	 * 接收字符数据的通知。
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (xml_result != null) {
			String data = new String(ch, start, length);

			if ("name".equals(preTag)) {
				xml_result.setName(data);
			} else if ("address".equals(preTag)) {
				xml_result.setAddress(data);
			}
		}

	}

	/*
	 * 接收元素开始的通知。
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if ("result".equals(localName)) {
			xml_result = new xml_result();
		}
		preTag = localName;
	}

	/*
	 * 接收文档的结尾的通知。
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if ("result".equals(localName) && xml_result != null) {
			xml_results.add(xml_result);
			xml_result = null;
		}
		preTag = null;
	}

}
