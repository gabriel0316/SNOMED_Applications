package translation.check;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.opencsv.CSVReader;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;

/**
 * A utility class to read CSV files and populate the translation data in the
 * `Compare` class. This class supports various file types and formats for
 * processing linguistic data.
 * 
 * Supported file types: - `.propcsv.csv` - `.termspace.csv` - `Additions.tsv` -
 * `Inactivations.tsv`
 * 
 * It also handles SNOMED International Template files by default.
 * 
 * @author Pero Grgic
 */
public class ReadTranslation {

	// Reference to the Compare class for storing translations
	static Compare concept = new Compare();

	// Language used for translations
	public static String language;

	// Acceptability ID based on translation preferences
	static String acceptabilityID = null;

	// Custom CSV parser configuration
	static CSVParser parser = new CSVParserBuilder().withSeparator('\t') // Use tab as a separator
			.withQuoteChar('"').withEscapeChar('\\').withStrictQuotes(false).build();

	/**
	 * Reads a CSV file and populates the `Compare` class with the extracted data.
	 *
	 * @param csvFile The file path to the CSV file.
	 * @throws IOException If an error occurs during file reading.
	 */
	public static void readFile(String csvFile) throws IOException {
		long start = System.currentTimeMillis();

		try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(csvFile)).withCSVParser(parser).build()) {

			// Determine the file type and process accordingly
			String fileType = checkFilePathExtension(csvFile);
			switch (fileType) {
			case ".porpcsv.csv":
				processPropCsv(csvReader);
				break;
			case ".termspace.csv":
				processTermspaceCsv(csvReader);
				break;
			case "Additions.tsv":
				processAdditionFile(csvReader);
				break;
			case "Inactivations.tsv":
				processInactivationFile(csvReader);
				break;
			case ".txt":
				processRF2File(csvReader);
				break;
			default:
				System.out.println(fileType + "... Resuming with SNOMED International Template");
				processSnomedTemplate(csvReader);
				break;
			}

			// Log the time taken to process the file
			long elapsedTime = System.currentTimeMillis() - start;
			System.out.println("CSV file processed successfully. Duration: " + elapsedTime + "ms");
			Main.totalTime += elapsedTime;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			System.err.println("The CSV is malformed or has syntax issues.");
			System.err.println("Please delete all columns after 'Target acceptable' in the CSV and try again.");
			System.exit(1);
		}
	}

	/**
	 * Processes `.propcsv.csv` files, extracting translations and their language
	 * details.
	 */
	private static void processPropCsv(CSVReader csvReader) throws IOException {
		System.out.println("Processing `.propcsv.csv` file...");
		csvReader.skip(4); // Skip the first 4 rows (headers or metadata)

		for (String[] row : csvReader) {
			// Determine the language based on cell content
			for (String cell : row) {
				if (cell.contains("de-CH")) {
					language = "de-CH";
				} else if (cell.contains("fr-CH")) {
					language = "fr-CH";
				} else {
					language = "it-CH";
				}
			}
			concept.setNewTranslations(row[1], row[2], "", row[3], language, "", "", "");
		}
	}

	/**
	 * Processes `.termspace.csv` files, extracting minimal translation data.
	 */
	private static void processTermspaceCsv(CSVReader csvReader) throws IOException {
		System.out.println("Processing `.termspace.csv` file...");
		csvReader.skip(1); // Skip the first row (headers)

		for (String[] row : csvReader) {
			concept.setNewTranslations(row[1]);
		}
	}

	/**
	 * Processes `Additions.tsv` files, extracting extended translation details.
	 */
	private static void processAdditionFile(CSVReader csvReader) throws IOException {
		System.out.println("Processing `Additions.tsv` file...");
		csvReader.skip(1); // Skip the first row (headers)
		
		

		for (String[] row : csvReader) {
			if (language == null){
			language= row[4];
			}
			concept.setNewTranslations(row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8], row[9],
					row[10], row[11], row[12], row[13], row[14], row[15], row[16], row[17]);
		}
	}

	/**
	 * Processes `Inactivations.tsv` files, extracting basic inactivation details.
	 */
	private static void processInactivationFile(CSVReader csvReader) throws IOException {
		System.out.println("Processing `Inactivations.tsv` file...");
		csvReader.skip(1); // Skip the first row (headers)

		for (String[] row : csvReader) {
			concept.setNewTranslations(row[0]); // Only description ID is needed
		}
	}

	/**
	 * Processes SNOMED International Template files, applying acceptability rules.
	 */
	private static void processSnomedTemplate(CSVReader csvReader) throws IOException {
		csvReader.skip(1); // Skip the first row (headers)

		for (String[] row : csvReader) {
			acceptabilityID = row[8].equalsIgnoreCase("preferred") || row[8].equalsIgnoreCase("pt")
					? "900000000000548007" // Preferred acceptability ID
					: "900000000000549004"; // Alternative acceptability ID

			concept.setNewTranslations(row[0], row[1], row[2], row[3], row[4], row[5], row[7], acceptabilityID);
			language = row[4];
		}
	}

	/**
	 * Processes SNOMED International Template files, applying acceptability rules.
	 */
	private static void processRF2File(CSVReader csvReader) throws IOException {
		System.out.println("Processing `.txt` file...");
		csvReader.skip(1); // Skip the first row (headers)

		for (String[] row : csvReader) {
			concept.setNewTranslations(row[4]);	
		}
	}
	
	
	
	/**
	 * Identifies the file type based on its extension.
	 *
	 * @param filePath The path of the file.
	 * @return The identified file type or "Unknown file type" if no match is found.
	 */
	public static String checkFilePathExtension(String filePath) {
		Map<String, String> fileExtensions = new HashMap<>();
		fileExtensions.put(".porpcsv.csv", ".porpcsv.csv");
		fileExtensions.put(".termspace.csv", ".termspace.csv");
		fileExtensions.put("Additions.tsv", "Additions.tsv");
		fileExtensions.put("Inactivations.tsv", "Inactivations.tsv");
		fileExtensions.put(".txt", ".txt");

		return fileExtensions.keySet().stream().filter(filePath::endsWith).findFirst().map(fileExtensions::get)
				.orElse("Unknown file type");
	}
}
