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
					break;
				case "Check_inactivation.tsv":
					System.out.println("Processing `Check_inactivation.tsv` file...");
					processInactivationFile2(csvReader);
					break;
				case ".simpleOverview.tsv":
					System.out.println("Processing `.simpleOverview.tsv` file...");
					processInactivationFile(csvReader);
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
			concept.setNewInactivations(row[0], row[2], language); // Only description ID is needed
		}
	}

	private static void processInactivationFile2(CSVReader csvReader) throws IOException { // to handle TS check
																							// inactivation
		csvReader.skip(1); // Skip the first row (headers)

		for (String[] row : csvReader) {
			concept.setNewInactivations(row[0], row[2], language); // Only concept ID and term are needed
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
					String[] values = new String[18];

					for (int i1 = 0; i1 < 18; i1++) {
						Cell cell = row.getCell(i1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						switch (cell.getCellType()) {
						case STRING:
							values[i1] = cell.getStringCellValue();
							break;
						case NUMERIC:
							values[i1] = String.valueOf(cell.getNumericCellValue());
							break;
						case BOOLEAN:
							values[i1] = String.valueOf(cell.getBooleanCellValue());
							break;
						case FORMULA:
							values[i1] = cell.getCellFormula(); // You can evaluate it if needed
							break;
						default:
							values[i1] = "";
							break;
						}
					}

					concept.setNewTranslations(values[0], values[1], values[2], values[3], values[4], values[5],
							values[6], values[7], values[8], values[9], values[10], values[11], values[12], values[13],
							values[14], values[15], values[16], values[17]);
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
					String[] values = new String[3];

					for (int i2 = 0; i2 < 3; i2++) {
						Cell cell = row.getCell(i2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						switch (cell.getCellType()) {
						case STRING:
							values[i2] = cell.getStringCellValue();
							break;
						case NUMERIC:
							values[i2] = String.valueOf(cell.getNumericCellValue());
							break;
						case BOOLEAN:
							values[i2] = String.valueOf(cell.getBooleanCellValue());
							break;
						case FORMULA:
							values[i2] = cell.getCellFormula(); // You can evaluate it if needed
							break;
						default:
							values[i2] = "";
							break;
						}
					}

					if (language == null) {
						language = values[4];
					}
					concept.setNewInactivations(values[0], values[2], language);
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

}