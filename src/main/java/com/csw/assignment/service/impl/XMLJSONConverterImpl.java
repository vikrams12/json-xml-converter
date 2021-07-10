package com.csw.assignment.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.Branch;
import org.dom4j.Document;

import com.csw.assignment.service.XMLJSONConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author vseshadri
 * This class provides a method to convert a given JSON file into a XML file based on specific rules.
 * Note that this class uses DOM parser to construct the XML document which is not memory efficient for large objects
 */
public class XMLJSONConverterImpl implements XMLJSONConverter {
	private static final Logger LOGGER = LogManager.getLogger(XMLJSONConverterImpl.class);

	/**
	 * Converts JSON data from the given input file to an XML And writes to the
	 * given output file
	 */
	public void convertJSONtoXML(String jsonFileIn, String xmlFileOut) {
		try {
			File file = new File(jsonFileIn);
			StringBuilder jsonStringBuilder = new StringBuilder();
			Files.lines(file.toPath()).forEach(content -> jsonStringBuilder.append(content));
			String jsonString = jsonStringBuilder.toString().trim();
			if (StringUtils.isBlank(jsonString)) {
				LOGGER.error("The input file is empty");
				return;
			}
			Document rootElement = DocumentHelper.createDocument();

			// Parsing the root elements
			// Top level could be a JSON Object or JSON Array
			if (jsonString.startsWith("{")) {
				LOGGER.debug("Root element is a JSON Object");
				JSONObject jsonFileObject = new JSONObject(jsonString.toString());
				Branch parentElement = rootElement.addElement("object");
				for (int i = 0; i < jsonFileObject.names().length(); i++) {
					parseJsonObject(jsonFileObject.get(jsonFileObject.names().getString(i)), parentElement,
							jsonFileObject.names().getString(i));
				}
			} else if (jsonString.startsWith("[")) {
				LOGGER.debug("Root element is a JSON Array");
				JSONArray jsonFileArray = new JSONArray(jsonString.toString());
				Branch parentElement = rootElement.addElement("array");
				for (int i = 0; i < jsonFileArray.length(); i++) {
					parseJsonObject(jsonFileArray.get(i), parentElement, null);
				}
			} else {
				LOGGER.error("Invalid JSON File.");
				return;
			}
			LOGGER.info("Successfully parsed the JSON File. Writing the XML Document into output file");

			writeXMLDocument(rootElement, xmlFileOut);
			LOGGER.info("Completed writing the result XML into output file");
		} catch (FileNotFoundException fileNotFoundException) {
			LOGGER.error("Given input / output file was not found. Please check and correct the path.", fileNotFoundException);
		} catch (IOException ioException) {
			LOGGER.error("Unexcpected IOException converting the input JSON to XML", ioException);
		} catch (JSONException jsonException) {
			LOGGER.error("Unexcpected JSONException converting the input JSON to XML", jsonException);
		}
	}

	private void parseJsonObject(Object object, Branch parentElement, String key) throws JSONException {
		if (object instanceof JSONObject) {
			JSONObject currentJsonObject = (JSONObject) object;
			parentElement = addXMLElementForObject(parentElement, key);
			// Recursive method call to parse all the child elements
			for (int i = 0; i < currentJsonObject.names().length(); i++) {
				parseJsonObject(currentJsonObject.get(currentJsonObject.names().getString(i)), parentElement,
						currentJsonObject.names().getString(i));
			}
		} else if (object instanceof JSONArray) {
			JSONArray currentJsonArray = (JSONArray) object;
			parentElement = addXMLElementForArray(parentElement, key);
			// Recursive method call to parse all the child elements
			for (int i = 0; i < currentJsonArray.length(); i++) {
				parseJsonObject(currentJsonArray.get(i), parentElement, null);
			}
		} else if (object instanceof String) {
			parentElement = addXMLElementForString(parentElement, object.toString(), key);
		} else if (object instanceof Number) {
			parentElement = addXMLElementForNumber(parentElement, object.toString(), key);
		} else if (object instanceof Boolean) {
			parentElement = addXMLElementForBoolean(parentElement, object.toString(), key);
		} else if (object == JSONObject.NULL || object == JSONObject.EXPLICIT_NULL) {
			parentElement = addXMLElementForNull(parentElement, key);
		} else {
			LOGGER.warn("Unknown object type: " + object.getClass().getName() + " Value: " + object);
		}
	}

	private void writeXMLDocument(Document document, String outFile) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(new FileWriter(new File(outFile)), format);
		writer.write(document);
		writer.close();
	}

	private Element addXMLElementForObject(Branch parentDocument, String attributeName) {
		Element element = parentDocument.addElement("object");
		if (StringUtils.isNotBlank(attributeName)) {
			element.addAttribute("name", attributeName);
		}
		return element;
	}

	private Element addXMLElementForArray(Branch parentDocument, String attributeName) {
		Element element = parentDocument.addElement("array");
		if (StringUtils.isNotBlank(attributeName)) {
			element.addAttribute("name", attributeName);
		}
		return element;
	}

	private Element addXMLElementForString(Branch parentDocument, String value, String key) {
		Element element = parentDocument.addElement("string");
		if (StringUtils.isNotBlank(key)) {
			element.addAttribute("name", key);
		}
		if (StringUtils.isNotBlank(value)) {
			element.addText(value);
		}
		return element;
	}

	private Element addXMLElementForNumber(Branch parentDocument, String value, String key) {
		Element element = parentDocument.addElement("number");
		if (StringUtils.isNotBlank(key)) {
			element.addAttribute("name", key);
		}
		if (StringUtils.isNotBlank(value)) {
			element.addText(value);
		}
		return element;
	}
	
	private Element addXMLElementForBoolean(Branch parentDocument, String value, String key) {
		Element element = parentDocument.addElement("boolean");
		if (StringUtils.isNotBlank(key)) {
			element.addAttribute("name", key);
		}
		if (StringUtils.isNotBlank(value)) {
			element.addText(value);
		}
		return element;
	}

	private Element addXMLElementForNull(Branch parentDocument, String key) {
		Element element = parentDocument.addElement("null");
		if (StringUtils.isNotBlank(key)) {
			element.addAttribute("name", key);
		}
		return element;
	}
}
