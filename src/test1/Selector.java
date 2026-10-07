package test1.JAVAClasses;
import java.io.File;
import java.math.BigInteger;
//import java.util.stream.*;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import jakarta.xml.bind.JAXBContext;
//import jakarta.xml.bind.Unmarshaller;

import java.util.ArrayList;
import java.util.List;

import java.util.Scanner;

import jakarta.xml.bind.Marshaller;

//public class Selector {
//	    private final Scholar scholar;
//
//	    public Selector(File xml, File xsd) throws Exception {
//	        Unmarshaller u = JAXBContext.newInstance(Scholar.class).createUnmarshaller();
//	        // optional but good: validate while loading
//	        u.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(xsd));
//	        this.scholar = (Scholar) u.unmarshal(xml);
//	    }
//
//	    private Stream<Article> allArticles() {
//	        return scholar.getInvestigators().getInvestigator().stream()
//	        			.flatMap(inv -> inv.getArticle().stream());
//	    }
//	    
//	    
//	    
//	    
//	    public static void main(String[] args) {
//	    	
//	    	File xml = new File(".\\src\\test1\\test2.xml");
//	    	File xsd = new File(".\\src\\output2.xsd");
//	    	
//	    	try {
//				Selector selector = new Selector(xml, xsd);
//				selector.allArticles().forEach(x -> System.out.println(x.getDate().getYear()));
//
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	    	
//	    }
//
//}
//	


public class Selector {

    public static void main(String[] args) throws Exception {
        // 1. Read and validate input XML
        Scholar scholar = unmarshal();

        Scanner scanner = new Scanner(System.in);
        
        // Filter by host university
        String targetHostUni = filterByHostInstitution(scanner, scholar);
        
        // Filter by an investigators total number of citations
        BigInteger minCitations = filterByMinNumberOfCitations(scanner);
        BigInteger maxCitations = filterByMaxNumberOfCitations(scanner, minCitations);
        
        // 3. Build the filtered output
        Scholar filteredScholar = new Scholar();
        Investigators filteredInvestigators = new Investigators();

        for (Investigator inv : scholar.getInvestigators().getInvestigator()) {

        	// filter by Host Institution
            if (targetHostUni != null) {
                if (inv.getHostInstitution() == null || !inv.getHostInstitution().equalsIgnoreCase(targetHostUni)) {
                    continue;
                }
            }
            
            // filter by number of citations
            if (minCitations != null || maxCitations != null) {
            	BigInteger totalCitations = inv.getTotalCitations();
            	
            	if (totalCitations.compareTo(minCitations) < 0 ) {
            		continue;
            	}
            	if (totalCitations.compareTo(maxCitations) > 0 ) {
            		continue;
            	}
            }
            
            
            filteredInvestigators.getInvestigator().add(inv);
        }

        filteredScholar.setInvestigators(filteredInvestigators);

        // 4. Write output XML
        marshal(filteredScholar);
        System.out.println();
        System.out.println("Filtering complete. Output written to output.xml");
    }

    /**
     * Filter the investigators by host university
     * @param scanner
     * @param scholar
     * @return
     */
    private static String filterByHostInstitution(Scanner scanner, Scholar scholar) {
        System.out.println("Do you want to filter the researchers by their host university? (y/n)");
        String answer = scanner.nextLine();

        if (answer.equals("y")) {
            List<String> displayedUnis = new ArrayList<>();
            System.out.println("Available host institutions:");
            for (Investigator inv : scholar.getInvestigators().getInvestigator()) {
                String uni = inv.getHostInstitution();  
                if (uni != null && !displayedUnis.contains(uni)) {
                    System.out.println(uni);
                    displayedUnis.add(uni);
                }
            }
            System.out.println("Enter the host institution:");
            return scanner.nextLine();
        } else {
            System.out.println("No host institution filter applied");
            return null;
        }
    }
 
    /**
     * Filters the investigators by the minimum number of citations
     * @param scanner
     * @return
     */
    private static BigInteger filterByMinNumberOfCitations(Scanner scanner) {
        System.out.println("Do you want to filter the researchers by their min number of citations? (y/n)");
        String answer = scanner.nextLine();

        if (answer.equals("y")) {
            while (true) {
                System.out.println("Enter the minimum admissible number of citations:");
                try {
                    BigInteger minCitations = new BigInteger(scanner.nextLine().trim());
	                    if (minCitations.signum() >= 0) {
	                        return minCitations;
	                    }
                    System.out.println("The number can't be negative.");
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid whole number.");
                }
            }
        } else {
            System.out.println("No minimum citations filter applied");
            return null;
        }
    }
    
    /**
     * Filters the investigators by the maximum number of citations
     * @param scanner
     * @param minCitations
     * @return
     */
    private static BigInteger filterByMaxNumberOfCitations(Scanner scanner, BigInteger minCitations) {
        System.out.println("Do you want to filter the researchers by their max total number of citations? (y/n)");
        String answer = scanner.nextLine();

        if (answer.equals("y")) {
            while (true) {
                System.out.println("Enter the minimum admissible number of citations:");
                BigInteger maxCitations = new BigInteger(scanner.nextLine().trim());
                if (maxCitations.signum() < 0) {
                    System.out.println("The number can't be negative.");
                } else if (minCitations != null && maxCitations.compareTo(minCitations) < 0) {
                    System.out.println("The maximum can't be lower than the minimum (" + minCitations + ").");
                } else {
                    return maxCitations;
                }
            }
        } else {
            System.out.println("No maximum citations filter applied");
            return null;
        }
    }
    
    public static Scholar unmarshal() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Scholar.class);
        var u = context.createUnmarshaller();
        u.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(new File(".\\src\\output2.xsd")));
        return (Scholar) u.unmarshal(new File(".\\src\\test1\\test2.xml"));
    }

    public static void marshal(Scholar scholar) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Scholar.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(scholar, new File("./output.xml"));
    }
}

