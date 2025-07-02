// package src.translation.check;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Executes the all necessary classes to compare the terms of the CSV with the SNOMED database.
 * @author Pero Grgic
 *
 */
public class Main {

	// TODO action required
	private static String CSVfilePath = "C:\\Users\\someuser\\Downloads\\Austrian_terms_20250702115608264.tsv"; // Erstelle aus Termspace-export-Excel vom Arbeitsblatt "Descriptions_Additions" ein Tab separated file
	//Path where to create the three files (identicalTranslation, newSynonym, noTranslation)
	// TODO action required
	private static String destination ="C:\\Users\\someuser\\Downloads\\result_Additions_20250702"; // Verzeichnis in das geschrieben wird, muss zuvor existieren

	public static long totalTime;

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		long StartTime = System.currentTimeMillis();

		try {
			ReadTranslation.readFile(CSVfilePath);
			Compare compare = new Compare();
			//Uncomment the needed methods.
			// compare.createTranslationsOverview(destination);
			compare.generateDeltaDescAdditions(destination);
			// compare.generateDeltaDescInactivation(destination);
			// compare.checkEszettInExtension(destination);
		} catch (IOException e) {
			e.printStackTrace();
		}
		long EndTime = System.currentTimeMillis();

		System.out.println("Vorgang abgeschlossen. Dauer:" + (EndTime - StartTime));
	}
}