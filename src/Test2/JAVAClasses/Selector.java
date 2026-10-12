package Test2.JAVAClasses;
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

public class Selector {
	private static final Scanner scanner = new Scanner(System.in);
	
    public static void main(String[] args) throws Exception {
    	
    	
        // Read and validate input XML
        Scholar scholar = unmarshal();

        // scholar.getInterests().getInterest().forEach(x -> System.out.println(x.getValue()));
        
        // Initialize the filter variables
        String targetHostUni = null;
        BigInteger minCitations = null;
        BigInteger maxCitations = null;
        List<String> chosenInterests = null;
        
        
        boolean finish = false;
        while(!finish) {
        	
        	int input = menu();
	        
	        if (input == 1) {
	        	// Filter by host university
		        targetHostUni = filterByHostInstitution(scholar);
	        }
	        
	        if (input == 2) {
	        	// Filter by interests
		        chosenInterests = filterByInterests(scholar);
		        
	        }
	        if (input == 3) {
	        	// Filter by an investigators total number of citations
		         minCitations = filterByMinNumberOfCitations(scanner);
		         maxCitations = filterByMaxNumberOfCitations(scanner, minCitations);
	  
	        }
	        
	        if (input == 4) {
		        finish=true;
		        //  Write output XML
		        Scholar filteredScholar = filters(scholar, targetHostUni, 
		                minCitations, maxCitations, chosenInterests);
		        marshal(filteredScholar);
		        System.out.println();
		        System.out.println("Filtering complete. Output written to selector_ output.xml");
	        }
	       
        }
    }
    
    /**
     * Menu
     * @return
     */
	private static int menu() {
    	System.out.println();
        System.out.println("Select investigators by:");
        System.out.println("1 - Host institution");
        System.out.println("2 - Interests");
        System.out.println("3 - Number of citations");
        System.out.println("4 - No more filters, generate output.xml");
        return readInt("Option:", 1, 4); 
    }
    
    /**
     * Keeps asking the user until the answer is valid
     * @param message
     * @param min
     * @param max
     * @return
     */
    protected static int readInt(String message, int min, int max) {
        while (true) {
            System.out.println(message);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            }
            
        }
    }
    
    
    /**
     * Filter the investigators by host university
     * @param scanner
     * @param scholar
     * @return
     */
    private static String filterByHostInstitution(Scholar scholar) {
       
        List<String> displayedUnis = new ArrayList<>();
        System.out.println("Available host institutions:");
         int i = 1;
        for (Investigator inv : scholar.getInvestigators().getInvestigator()) {
            String uni = inv.getHostInstitution();  
            if (uni != null && !displayedUnis.contains(uni)) {
                System.out.println(i + " - " + uni);
                displayedUnis.add(uni);
                i++;
            }
        }
        int ans = readInt("Enter the host institution:", 1, displayedUnis.size());
        
        
        return displayedUnis.get(ans - 1);
        
        
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
                System.out.println("Enter the maximum admissible number of citations:");
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

    /**
     * Filters the investigators by their interests
     * @param scholar
     * @return
     */
	private static List<String> filterByInterests(Scholar scholar) {
		List<Interest> all = scholar.getInterests().getInterest();

		System.out.println("Available interests:");
		for (int i = 0; i < all.size(); i++) {
			System.out.println((i + 1) + " - " + all.get(i).getValue());
		}

		while (true) {
			System.out.println("Select the interests by number, separated by ',':");
			String[] parts = scanner.nextLine().split(",");
			List<String> chosen = new ArrayList<>();
			boolean valid = true;

			for (String p : parts) {
				try {
					int n = Integer.parseInt(p.trim());
					if (n < 1 || n > all.size()) {
						valid = false;
						break;
					}
					chosen.add(all.get(n - 1).getValue());
				} catch (NumberFormatException e) {
					valid = false;
					break;
				}
			}

			if (valid && !chosen.isEmpty()) {
				return chosen;
			}
			System.out.println("Invalid selection, try again.");
		}
	}

	private static boolean hasAnyInterest(Investigator inv, List<String> interests) {
	    for (Object ref : inv.getInterestRefs()) {
	        if (ref instanceof Interest) {
	            String value = ((Interest) ref).getValue();
	            for (String target : interests) {
	                if (value != null && value.equalsIgnoreCase(target)) {
	                    return true;
	                }
	            }
	        }
	    }
	    return false;
	}
 
    /**
     * Filters the xml file with the filters chosen by the user
     * @param scholar
     * @param targetHostUni
     * @param minCitations
     * @param maxCitations
     * @return
     */
    private static Scholar filters(Scholar scholar, String targetHostUni, 
        BigInteger minCitations, BigInteger maxCitations, List<String> interests) {
    	// Build the filtered output
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
            	
            	
            	if (minCitations != null && totalCitations.compareTo(minCitations) < 0 ) {
            		continue;
            	}
            	if (maxCitations != null && totalCitations.compareTo(maxCitations) > 0 ) {
            		continue;
            	}
            }
            
            // filter by interests
            if (interests != null && !hasAnyInterest(inv, interests)) {
            	continue;   
            }
            
            
            filteredInvestigators.getInvestigator().add(inv);
        }

        filteredScholar.setInvestigators(filteredInvestigators);
        filteredScholar.setAuthors(scholar.getAuthors());
        filteredScholar.setInterests(scholar.getInterests());
        filteredScholar.setInvestigators(filteredInvestigators);
        return filteredScholar;
        
    }
    
    
    protected static Scholar unmarshal() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Scholar.class);
        var u = context.createUnmarshaller();
        u.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(new File(".\\src\\Test2\\output2.xsd")));
        return (Scholar) u.unmarshal(new File(".\\src\\Test2\\test2.xml"));
    }

    protected static void marshal(Scholar scholar) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Scholar.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(scholar, new File("./selector_output.xml"));
    }

}    
    
   