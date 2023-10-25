package translation.check;

import java.io.IOException;
//import java.sql.SQLException;

import javax.swing.SwingUtilities;

/**
 * Executes the all necessary classes to compare the terms of the CSV with the SNOMED database.
 * @author Pero Grgic
 *
 */
public class Main {

	private static String CSVfilePath = "C:\\Users\\Pero Grgic\\Desktop\\eHS\\SNOMED\\translationOverview\\MikroorganismenRefset.tsv";
	
	//Path where to create the three files (identicalTranslation, newSynonym, noTranslation)
	private static String destination ="C:\\Users\\Pero Grgic\\Desktop\\eHS\\SNOMED\\Prüfen FR Terme\\Substanzen AF\\Compared\\DE";

	public static long totalTime;
	
	public static void main(String[] args) {

		try {
			ReadTranslation.readFile(CSVfilePath);	
//			DB_conncetion.connectDB();
//			DB_conncetion.searchTranslations(ReadTranslation.language);
//			DB_conncetion.closeConnectionDB();
//			Compare.compare(destination);
			Compare.createTranslationsOverview(destination);
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Vorgang abgeschlossen. Dauer:"+ totalTime);
	}	
}