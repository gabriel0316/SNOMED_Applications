package translation.check;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Executes the all necessary classes to compare the terms of the CSV with the
 * SNOMED database.
 * 
 * @author Pero Grgic
 *
 */
public class Main {

	private static Conf conf = new Conf();
	private static CompareManager compareManager = new CompareManager();
	public static void main(String[] args) throws SQLException, ClassNotFoundException {

		try {

//			compareManager.runTranslationOverview(conf.getFilePathCurrent(), conf.getDestination());
			compareManager.runDeltaDescAdditions(conf.getFilePathCurrent(), conf.getDestination());
//			compareManager.runDeltaDescInactivations(conf.getFilePathCurrent(), conf.getDestination());
//			compareManager.runGenerateDelta(conf.getFilePathCurrent(), conf.getDestination());
//			compareManager.runCheckEszettInExtension(conf.getDestination());
//			compareManager.runDeltaNotPublishedTranslations(conf.getFilePathCurrent(), conf.getFilePathPrevious(), conf.getDestination());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}