package translation.check;

import java.io.IOException;
import java.sql.SQLException;

import javax.swing.SwingUtilities;


/**
 * Executes the all necessary classes to compare the terms of the CSV with the SNOMED database.
 * @author Pero Grgic
 *
 */
public class Main {

	private static String CSVfilePath = "C:\\Users\\Pero Grgic\\Desktop\\eHS\\SNOMED\\translationOverview\\MikroorganismenRefset.tsv";
	//PATH_TO_THE_CSV
	//Path where to create the three files (identicalTranslation, newSynonym, noTranslation)
	//PATH_TO_THE_DESTINATION_WHERE_TO_CREATE_THE_FILES
	private static String destination ="C:\\Users\\Pero Grgic\\Desktop\\eHS\\SNOMED\\Prüfen FR Terme\\Substanzen AF\\Compared\\DE";

	public static long totalTime;
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {

		try {
			ReadTranslation.readFile(CSVfilePath);	
			
			//Comment of uncomment the needed methods. TODO Make a GUI for choosing which method and also setting all needed parameters.
//			DB_conncetion.connectDB();
//			DB_conncetion.searchTranslations(ReadTranslation.language);
//			DB_conncetion.closeConnectionDB();
//			Compare.compare(destination);
			Compare.createTranslationsOverview(destination);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Vorgang abgeschlossen. Dauer:"+ totalTime);
	}	
}