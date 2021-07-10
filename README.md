# JSON - XML Converter Tool
## Description:
This is a simple Java based tool to convert a JSON input file into an XML output file based on predefined criteria.

## Pre-requisites:
1. Windows/Linux based operating system
2. Java 1.8 or higher 
3. Maven must be installed in the system to build and run the application

## Usage:
0. Run the following command if Java 1.8 is not the version in use
	
	`export JAVA_HOME=<java 1.8 JDK Location>`
1. Run the following from the base project folder 
	
	`mvn clean install`
2. Run the tool to convert a given JSON input file into an XML and write it into an output file
	
	`java -jar target\json-xml-converter-0.0.1-SNAPSHOT-jar-with-dependencies.jar "<INPUT_JSON_FILE_PATH>" "<OUTPUT_XML_FILE_PATH>"`
Where, 
	"INPUT_JSON_FILE_PATH" is the relative or absolute path of the input JSON file (including the file name).
	"OUTPUT_XML_FILE_PATH" is the relative or absolute path where the output XML file will be written (including the file name).
