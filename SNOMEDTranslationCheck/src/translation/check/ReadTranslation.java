package translation.check;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;

import com.opencsv.CSVReader;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;


/**
 * This class reads the prepared CSV file and fills the lists of the compare class.
 * @author Pero Grgic
 *
 */
public class ReadTranslation {
	
	static Compare concept = new Compare();
	public static String language;
	static String acceptabilityID = null;
	
	// Create a CSVParser with tab as the separator
	// TODO: make splitBy dynamic
    static CSVParser parser = new CSVParserBuilder().withSeparator('\t').withQuoteChar('"').withEscapeChar('\\').withStrictQuotes(false).build();
	

	/**
	 * This function reads the CSV-File and fills the Lists of the Compare Class.
	 * @param csvFile path to the CSV-file
	 * @param language which language should be compared
	 * @throws IOException
	 */
	public static void readFile(String csvFile) throws IOException{
		long start = System.currentTimeMillis();

		try {
			// Create a CSVReader with the custom parser
		    CSVReader csvReader = new CSVReaderBuilder(new FileReader(csvFile)).withCSVParser(parser).build();
			
			//checks if the CSV-File is a prop.csv file from termionoloGit
			if (checkFilePathExtension(csvFile).equals("propcsv file detected")) { 
	            System.out.println("The file path ends with .propcsv.csv");

	            // Skip the first 4 rows
	            csvReader.skip(4);
	                
	            //TODO: im propcsv notwendige cells: 1 code, 2 display,3 des de, 4 des fr, 5 des it, 6 des rm, 7 des en

	            // Loop through each row of data
	            for (String[] row : csvReader) {
	            	String lang = null;
	                
	            	// Process the current row
	                for (int i = 0; i < row.length; i++) {
	                    String cell = row[i];
	                    if (cell.contains("de-CH")) {
	                    	language = "de-CH";
	                    }else if (cell.contains("fr-CH")){
	                    	language = "fr-CH";
	                    }else {
	                    	language = "it-CH";
	                    }
	                }    
	               System.out.println(); // Add a newline after processing each row
	               concept.setNewTranslations(row[1], row[2], "", row[3], lang, "", "", "");           
	            }
	          
	        } else if (checkFilePathExtension(csvFile).equals(".termspace.csv")) {
	        	System.out.println("termspace file detected");
	        	csvReader.skip(1);
				for (String[] row : csvReader) {
	                concept.setNewTranslations(row[1]);
	            }	
	        } else {
	        	System.out.println(checkFilePathExtension(csvFile) + "... resuming with SNOMED International Template");
	        	csvReader.skip(1);
				for (String[] row : csvReader) {

					//Checks acceptability of the new translation and sets the ID
	                if (row[8].equalsIgnoreCase("preferred") || row[8].equalsIgnoreCase("pt")) {
	                    acceptabilityID = "900000000000548007";
	                } else {
	                    acceptabilityID = "900000000000549004";
	                }

	                concept.setNewTranslations(row[0], row[1], row[2], row[3], row[4], row[5], row[7], acceptabilityID);
	                ReadTranslation.language = row[4];
	            }	
	        }

			long elapsedTime = System.currentTimeMillis() - start;
			System.out.println("CSV file eingelesen und array NewTranslation befüllt. Dauer: "+ elapsedTime);
			Main.totalTime = Main.totalTime + elapsedTime;
		 } catch (IOException e) {
	         e.printStackTrace();
	     } catch (NoSuchElementException e) {
	         // Handle CsvValidationException
	         System.out.println("The CSV is malformed and has syntax issues.");
	         System.out.println("Please delete all columns after 'Target acceptable' in the CSV and try again.");
	         System.exit(1);
	     }
		
	}
	
	/**
	 * Method to detect the file extension. The file extension determines how the file is red.
	 * @param FilePath Path to the file
	 * @return Returns a sting with the file extension 
	 */
	public static String checkFilePathExtension(String filePath) {
        File file = new File(filePath);
        String fileName = file.getName();
        if (fileName.endsWith(".porpcsv.csv")) {
            return ".porpcsv.csv";
        } else if (fileName.endsWith(".termspace.csv")) {
            return ".termspace.csv";
        } else {
            return "Unknown file type";
        }
    }
	
}