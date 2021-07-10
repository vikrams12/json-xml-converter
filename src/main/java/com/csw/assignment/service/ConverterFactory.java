package com.csw.assignment.service;

import com.csw.assignment.service.impl.XMLJSONConverterImpl;

public class ConverterFactory {
	public XMLJSONConverter createXMLJSONConverter() {
		return new XMLJSONConverterImpl();
	}

}
