package Test3.JAVAClasses;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
public class XMLtoHTML {
	
	/**
	 * Main
	 * @param args
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception {

        // Load the XSL stylesheet
        TransformerFactory factory =
                TransformerFactory.newInstance();

        StreamSource xslSource =
                new StreamSource(new File("./src//Test3/toHTML.xsl"));

        Transformer transformer =
                factory.newTransformer(xslSource);

        // Load the XML input
        StreamSource xmlSource =
                new StreamSource(new File("./processor_output.xml"));

        // Define the HTML output file
        StreamResult output =
                new StreamResult(new File("final_output.html"));

        // Perform the transformation
        transformer.transform(xmlSource, output);

        System.out.println("HTML file generated. Output written to final_output.html");
    }
}

	

