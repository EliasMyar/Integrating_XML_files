package Test2.JAVAClasses;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

public class Processor {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {

        // Read and validate the Selector output
        Scholar scholar = unmarshal();

        Statistics stats = new Statistics();

        // Total number of researchers processed
        int totalResearchers = scholar.getInvestigators().getInvestigator().size();
        System.out.println("Total number of researchers: " + totalResearchers);
        stats.setTotalResearchers(BigInteger.valueOf(totalResearchers));

        // Total number of conferences/journals
        int totalJournals = numberOfJournals(scholar);
        System.out.println("Total number of journals: " + totalJournals);
        stats.setTotalVenues(BigInteger.valueOf(totalJournals));

        // Total number of citations (all researchers)
        BigInteger totalCitations = totalNumberOfCitations(scholar);
        System.out.println("Total number of citations: " + totalCitations);
        stats.setTotalCitations(totalCitations);

        // Investigator with the most citations in 2026 
        String bestInvestigator = mostCitationsCurrentYear(scholar);
        System.out.println("The Investigator with the most citations: " + bestInvestigator);
        stats.setMostCitedInvestigatorCurrentYear(bestInvestigator);
        
        // Total number of publications/articles        
        int totalArticles = totalArticles(scholar);
        System.out.println("Total number of publications: " + totalArticles);
        stats.setTotalArticles(BigInteger.valueOf(totalArticles));
        
        // Top N publications with the highest citation count
        int n = readInt("Select the value of N for the top N publications:", 1, totalArticles);

        List<Article> topN = topNPublications(scholar, n);

        System.out.println("Top " + topN.size() + " publications by citations:");
        for (Article a : topN) {
            String title = a.getTitle().replaceAll("\\s+", " ").trim();
            System.out.println(" " + a.getArticleCitations() + " - " + title);

            TopPublication p = new TopPublication();
            p.setTitle(title);
            p.setArticleCitations(a.getArticleCitations());
            stats.getTopPublication().add(p);
        }

        // Output = Selector data + statistics
        scholar.setStatistics(stats);
        marshal(scholar);

        System.out.println();
        System.out.println("Processing complete. Output written to processed_output.xml");
    }

    
    /**
     * Returns the number of distinct journals
     * @param scholar
     * @return
     */
    private static int numberOfJournals(Scholar scholar) {
        List<String> journals = new ArrayList<>();

        for (Investigator inv : scholar.getInvestigators().getInvestigator()) {
            for (Article article : inv.getArticle()) {
                String journal = article.getJournal();

                // some articles do not have a journal name available
                if (journal != null && !journal.trim().isEmpty()) {
                    journal = journal.replaceAll("\\s+", " ").trim();

                    if (!journals.contains(journal)) {
                        journals.add(journal);
                    }
                }
            }
        }
        //System.out.println(journals);
        return journals.size();
    }
   
    /**
     * Returns the sum of the total number of citations from the investigators combined 
     * @param scholar
     * @return
     */
    private static BigInteger totalNumberOfCitations(Scholar scholar) {
    		BigInteger citations = BigInteger.ZERO;
    		
    		for (Investigator inv : scholar.getInvestigators().getInvestigator()) {
    			citations = citations.add(inv.getTotalCitations());
    			
    		}
    		return citations;
    }

    
    /**
     * Returns the name of the investigator with the most citations in the current year
     * @param scholar
     * @return the name, or null if there are no investigators
     */
    private static String mostCitationsCurrentYear(Scholar scholar) {
        Investigator best = null;

        for (Investigator inv : scholar.getInvestigators().getInvestigator()) {
            if (best == null
                    || inv.getCitationsCurrentYear().compareTo(best.getCitationsCurrentYear()) > 0) {
                best = inv;
            }
        }

        if (best == null) {
            return null;
        }

        // authorRef is an IDREF, so JAXB resolves it to the Author object
        Object ref = best.getAuthorRef();
        if (ref instanceof Author) {
            return ((Author) ref).getName();
        }
        return null;
    }

    // the N articles with the most citations, highest first
    private static List<Article> topNPublications(Scholar scholar, int n) {
        List<Article> all = new ArrayList<>();
        for (Investigator inv : scholar.getInvestigators().getInvestigator()) {
            all.addAll(inv.getArticle());
        }
        all.sort((a, b) -> b.getArticleCitations().compareTo(a.getArticleCitations()));
        return new ArrayList<>(all.subList(0, Math.min(n, all.size())));
    }
 
    private static int totalArticles(Scholar scholar) {
    	List<Article> all = new ArrayList<>();
        for (Investigator inv : scholar.getInvestigators().getInvestigator()) {
            all.addAll(inv.getArticle());
        }
        return all.size();
    	
    }
    
    
    private static int readInt(String message, int min, int max) {
        while (true) {
            System.out.println(message);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // handled below
            }
            System.out.println("Please enter a whole number >= " + min + " and <= " + max + ".");
        }
    }

    
    public static Scholar unmarshal() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Scholar.class);
        var u = context.createUnmarshaller();
        u.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(new File(".\\src\\Test2\\output2.xsd")));
        return (Scholar) u.unmarshal(new File("./selector_output.xml"));
    }

    public static void marshal(Scholar scholar) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Scholar.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(scholar, new File("./processor_output.xml"));
    }
}