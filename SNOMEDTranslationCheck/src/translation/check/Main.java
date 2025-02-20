package translation.check;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Executes the all necessary classes to compare the terms of the CSV with the SNOMED database.
 * @author Pero Grgic
 *
 */
public class Main {

	private static String CSVfilePath = "C:\\Users\\Pero Grgic\\Downloads\\Unbenannte Tabelle - Tabellenblatt1.Inactivations.tsv";
	//Path where to create the three files (identicalTranslation, newSynonym, noTranslation)
	private static String destination ="C:\\Users\\Pero Grgic\\Downloads";

	public static long totalTime;

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		long StartTime = System.currentTimeMillis();

		try {
			ReadTranslation.readFile(CSVfilePath);	
			Compare compare = new Compare();
			//Uncomment the needed methods.
			compare.createTranslationsOverview(destination);
//			compare.generateDeltaDescAdditions(destination);
//			compare.generateDeltaDescInactivation(destination);
//			compare.checkEszettInExtension(destination);
		} catch (IOException e) {
			e.printStackTrace();
		}
		long EndTime = System.currentTimeMillis();

		System.out.println("Vorgang abgeschlossen. Dauer:" + (EndTime - StartTime));
	}	
}