package test1;
import test1.JAVAClasses.*;

import java.io.File;
import java.util.stream.*;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

public class Selector {
	    private final Scholar scholar;

	    public Selector(File xml, File xsd) throws Exception {
	        Unmarshaller u = JAXBContext.newInstance(Scholar.class).createUnmarshaller();
	        // optional but good: validate while loading
	        u.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(xsd));
	        this.scholar = (Scholar) u.unmarshal(xml);
	    }

	    private Stream<Article> allArticles() {
	        return scholar.getInvestigators().getInvestigator().stream()
	        			.flatMap(inv -> inv.getArticle().stream());
	    }
	    
	    private {}
	    
	    
	    
	    public static void main(String[] args) {
	    	
	    	File xml = new File(".\\src\\test1\\test2.xml");
	    	File xsd = new File(".\\src\\output2.xsd");
	    	
	    	try {
				Selector selector = new Selector(xml, xsd);
				selector.allArticles().forEach(x -> System.out.println(x.getDate().getYear()));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }

}
	

