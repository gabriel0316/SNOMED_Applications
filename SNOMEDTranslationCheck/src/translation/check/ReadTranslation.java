package translation.check;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import com.opencsv.CSVReader;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

	/**
	 * Reads a CSV file and populates the `Compare` class with the extracted data.
	 *
	 * @param csvFile The file path to the CSV file.
	 * @throws IOException If an error occurs during file reading.
	 */
	public static void readFile(String filePath) throws IOException {
		long start = System.currentTimeMillis();

		// Determine file type and delimiter
		Object[] fileInfo = checkFilePathExtension(filePath);
		String fileType = (String) fileInfo[0];
		Character separator = (Character) fileInfo[1];

		if ("Excel".equals(fileType)) {
			// Read Excel file using Apache POI
			try (FileInputStream fis = new FileInputStream(filePath);
					Workbook workbook = filePath.endsWith(".xlsx") ? new XSSFWorkbook(fis) : new HSSFWorkbook(fis)) {

				System.out.println("Processing Excel file: " + filePath);
				processTermspaceExcelForDelta(workbook);

			} catch (IOException e) {
				System.err.println("Error reading Excel file.");
				e.printStackTrace();
			}

		} else {
			// Read CSV/TSV file using OpenCSV
			CSVParser parser = new CSVParserBuilder().withSeparator(separator).withQuoteChar('"').withEscapeChar('\\')
					.withStrictQuotes(false).build();

			try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath)).withCSVParser(parser).build()) {

				switch (fileType) {
				case ".propcsv.csv":
					processPropCsv(csvReader);
					break;
				case ".termspace.csv":
					processTermspaceCsv(csvReader);
					break;
				case "Additions.tsv":
					processAdditionFile(csvReader);
					break;
				case "Inactivations.tsv":
					System.out.println("Processing `Inactivations.tsv` file...");
					processInactivationFile(csvReader);
//					break;
//				case "Check_inactivation.tsv":
//					System.out.println("Processing `Check_inactivation.tsv` file...");
//					processInactivationFile2(csvReader);
//					break;
				case ".simpleOverview.tsv":
					System.out.println("Processing `.simpleOverview.tsv` file...");
					simpleOverviewFile(csvReader);
					break;
				case ".txt":
					System.out.println("Processing RF2 file...");
					processDescriptionRF2File(csvReader);
					break;
 				default:
					System.out.println(fileType + "... Resuming with SNOMED International Template");
					processSnomedTemplate(csvReader);
					break;
				}

				long elapsedTime = System.currentTimeMillis() - start;
				System.out.println("CSV/TSV file processed successfully. Duration: " + elapsedTime + "ms");
				Main.totalTime += elapsedTime;

			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSuchElementException e) {
				System.err.println("The CSV is malformed or has syntax issues.");
				System.err.println("Please delete all columns after 'Target acceptable' in the CSV and try again.");
			}
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
			concept.setCheckForTranslation(row[1]);
		}
	}

	/**
	 * Processes `Additions.tsv` files, extracting extended translation details.
	 */
	private static void processAdditionFile(CSVReader csvReader) throws IOException {
		System.out.println("Processing `Additions.tsv` file...");
		csvReader.skip(1); // Skip the first row (headers)

		for (String[] row : csvReader) {
			concept.setNewTranslations(row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8], row[9],
					row[10], row[11], row[12], row[13], row[14], row[15], row[16], row[17]);
		}
	}

	/**
	 * Processes `Inactivations.tsv` files, extracting basic inactivation details.
	 */
	private static void processInactivationFile(CSVReader csvReader) throws IOException {
		csvReader.skip(1); // Skip the first row (headers)

		for (String[] row : csvReader) {
			System.out.println("row[0]: " + row[0]);
			concept.setNewInactivations(row[0], row[4], language, row[2]); // Only description ID is needed
		}
	}

//	private static void processInactivationFile2(CSVReader csvReader) throws IOException { // to handle TS check
//																							// inactivation
//		csvReader.skip(1); // Skip the first row (headers)
//
//		for (String[] row : csvReader) {
//			concept.setNewInactivations(row[0], row[2], language); // Only concept ID and term are needed
//		}
//	}
	
	private static void simpleOverviewFile(CSVReader csvReader) throws IOException { // to handle TS check
		csvReader.skip(1); // Skip the first row (headers)

		for (String[] row : csvReader) {
			concept.setCheckForTranslation(row[0]); // Only concept ID and term are needed
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
	private static void processDescriptionRF2File(CSVReader csvReader) throws IOException {

		System.out.println("Processing `Description_RF2` file...");
		csvReader.skip(1); // Skip the first row (headers)

		for (String[] row : csvReader) {
			concept.setNewTranslations(row[4], row[3], row[0], row[5], row[6], row[7], "TODO");
		}
	}

	private static void processTermspaceExcelForDelta(Workbook workbook) {

		int numberOfSheets = workbook.getNumberOfSheets();
		for (int i = 0; i < numberOfSheets; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			String sheetName = sheet.getSheetName();
			System.out.println("Processing sheet: " + sheet.getSheetName());
			// Example: process the sheet (you can replace this with your own logic)
			switch (sheetName) {
			case "Description Additions":
				Iterator<Row> rowIterator = sheet.iterator();

				// Skip header row
				if (rowIterator.hasNext()) {
					rowIterator.next();
				}

				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();
					String conceptId = getCellAsString(row.getCell(0));
					String fsn = getCellAsString(row.getCell(1));
					String pt = getCellAsString(row.getCell(2));
					String term = getCellAsString(row.getCell(3));
					String language_Code = getCellAsString(row.getCell(4));
					String case_Significance = getCellAsString(row.getCell(5));
					String type = getCellAsString(row.getCell(6));
					String language_reference_set = getCellAsString(row.getCell(7));
					String acceptabilityId = getCellAsString(row.getCell(8));
					String language_reference_set2 = getCellAsString(row.getCell(9));
					String acceptabilityId2 = getCellAsString(row.getCell(10));
					String language_reference_set3 = getCellAsString(row.getCell(11));
					String acceptabilityId3 = getCellAsString(row.getCell(12));
					String language_reference_set4 = getCellAsString(row.getCell(13));
					String acceptabilityId4 = getCellAsString(row.getCell(14));
					String language_reference_set5 = getCellAsString(row.getCell(15));
					String acceptabilityId5 = getCellAsString(row.getCell(16));
					String notes = getCellAsString(row.getCell(17));				

					concept.setNewTranslations(conceptId, fsn, pt, term, language_Code, case_Significance, type,
							language_reference_set, acceptabilityId, language_reference_set2, acceptabilityId2,
							language_reference_set3, acceptabilityId3, language_reference_set4, acceptabilityId4,
							language_reference_set5, acceptabilityId5, notes);
				}
				break;

			case "Description Inactivations":
				System.out.println("Starting processing description inactivations");
				Iterator<Row> rowIterator1 = sheet.iterator();

				// Skip header row
				if (rowIterator1.hasNext()) {
					rowIterator1.next();
				}

				while (rowIterator1.hasNext()) {

					 Row row = rowIterator1.next();
					 String descriptionId = getCellAsString(row.getCell(0));
					 String term = getCellAsString(row.getCell(2));
					 String conceptId = getCellAsString(row.getCell(9));

					    concept.setNewInactivations(descriptionId, term, language, conceptId); //Language is not used here, but can be added if needed
				}
				break;

			default:
				// Do nothing for unrecognized sheets
				break;
			}

		}

	}

	/**
	 * Identifies the file type based on its extension.
	 *
	 * @param filePath The path of the file.
	 * @return The identified file type or "Unknown file type" if no match is found.
	 */
	public static Object[] checkFilePathExtension(String filePath) {
		Map<String, Character> fileExtensions = new HashMap<>();

		// Define known file extensions and their corresponding delimiters
		fileExtensions.put(".propcsv.csv", ';');
		fileExtensions.put(".termspace.csv", '\t');
		fileExtensions.put("Additions.tsv", '\t');
		fileExtensions.put("Inactivations.tsv", '\t');
		fileExtensions.put("Check_inactivation.tsv", '\t');
		fileExtensions.put(".txt", '\t');
		fileExtensions.put(".simpleOverview.tsv", '\t');

		// Check if the file path ends with a known extension
		for (Map.Entry<String, Character> entry : fileExtensions.entrySet()) {
			if (filePath.endsWith(entry.getKey())) {
				return new Object[] { entry.getKey(), entry.getValue() };
			}
		}

		// Check for Excel files (no delimiter needed)
		if (filePath.endsWith(".xlsx") || filePath.endsWith(".xls")) {
			return new Object[] { "Excel", null };
		}

		// Return default for unknown file types
		return new Object[] { "Unknown file type", '\t' };
	}
	
	
	private static String getCellAsString(Cell cell) {
	    if (cell == null) {
	        return "";
	    }
	    switch (cell.getCellType()) {
	        case STRING:
	            return cell.getStringCellValue();
	        case NUMERIC:
	            return String.valueOf(cell.getNumericCellValue());
	        case BOOLEAN:
	            return String.valueOf(cell.getBooleanCellValue());
	        case FORMULA:
	            return cell.getCellFormula();
	        default:
	            return "";
	    }
	}
	

}