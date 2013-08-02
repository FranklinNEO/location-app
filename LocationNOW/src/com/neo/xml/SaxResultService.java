package com.neo.xml;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SaxResultService {
	public static List<xml_result> readXml(InputStream inStream)
			throws Exception {

		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser saxParser = spf.newSAXParser(); // 创建解析器
		XMLContentHandler handler = new XMLContentHandler();
		saxParser.parse(inStream, handler);
		inStream.close();
		return handler.getxml_result();
	}

}
