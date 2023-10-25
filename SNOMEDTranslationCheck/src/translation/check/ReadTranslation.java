package translation.check;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class reads the prepared CSV file and fills the lists of the compare class.
 * @author Pero Grgic
 *
 */
public class ReadTranslation {
	
	static Compare concept = new Compare();
	public static String language;


	// TODO: make splitBy dynamic
	static String csvSplitBy = "	";
	
	/**
	 * This function reads the CSV-File and fills the Lists of the Compare Class.
	 * @param csvFile path to the CSV-file
	 * @param language which language should be compared
	 * @throws IOException
	 */
	public static void readFile(String csvFile) throws IOException{
		long start = System.currentTimeMillis();    
		String acceptabilityID = null;
		
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		String line;
		while ((line = br.readLine()) != null) {
			String[] values = line.split(csvSplitBy);
			
				//Checks acceptability of the new translation and sets the ID
				if (values[8].equalsIgnoreCase("preferred") ||values[8].equalsIgnoreCase("pt")){
				acceptabilityID = "900000000000548007";
				}else{
					acceptabilityID = "900000000000549004";
				}
				concept.setNewTranslations(values[0], values [1], values [2], values [3],values [4],values [5],values [7], acceptabilityID);
				ReadTranslation.language=values[4];
			}
			br.close();
			long elapsedTime = System.currentTimeMillis() - start;
			System.out.println("CSV file eingelesen und array NewTranslation befüllt. Dauer: "+ elapsedTime);
			Main.totalTime = Main.totalTime + elapsedTime;
	}
}