package com.csw.assignment;

import com.csw.assignment.service.ConverterFactory;
import com.csw.assignment.service.XMLJSONConverter;

public class DataConverterMain {
	public static void main(String[] args) {
		XMLJSONConverter xmlJSONConverter = new ConverterFactory().createXMLJSONConverter(); 
		xmlJSONConverter.convertJSONtoXML(args[0], args[1]);
	}

}
