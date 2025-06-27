package translation.check;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class creates files for new, old, identical translations and can also
 * create an overview with existing translations in the SNOMED DB.
 */
public class Compare {

	private final DB_connection dbConnection = new DB_connection();

	public static List<List<String>> newTranslation = new ArrayList<>();
	public static List<List<String>> newInactivation = new ArrayList<>();
	public static List<List<String>> oldTranslation = new ArrayList<>();
	public static List<List<String>> oldIactivation = new ArrayList<>();
	public List<List<String>> identicalTranslation = new ArrayList<>();
	public List<List<String>> noTranslation = new ArrayList<>();
	public List<List<String>> newSynonym = new ArrayList<>();
//    public List<List<String>> checkSynonym = new ArrayList<>();
	public List<List<String>> newPreferredTerm = new ArrayList<>();
	public static String language;
	public List<List<String>> descriptionChanges = new ArrayList<>();
	public List<List<String>> descriptionAddition = new ArrayList<>();
	public static List<List<String>> translationOverview = new ArrayList<>();
	public static List<List<String>> eszettList = new ArrayList<>();

	/**
	 * Sets the information to a concept
	 * 
	 * @param conceptId              ID of the concept
	 * @param fsn                    FSN of the concept
	 * @param term                   Translated term
	 * @param language_Code          ISO code for the language e.g. "de"
	 * @param case_Significance      Case significance of the translated term
	 * @param language_reference_set Reference set the translated term belongs to
	 * @param acceptabilityId        Acceptability of the translated term
	 */

	public void setNewTranslations(String conceptId, String fsn, String pt, String term, String language_Code,
			String case_Significance, String type, String language_reference_set, String acceptabilityId,
			String language_reference_set2, String acceptabilityId2, String language_reference_set3,
			String acceptabilityId3, String language_reference_set4, String acceptabilityId4,
			String language_reference_set5, String acceptabilityId5, String notes) {
		List<String> L = new ArrayList<String>();
		L.add(conceptId);
		L.add(fsn);
		L.add(pt);
		L.add(term);
		L.add(language_Code);
		L.add(case_Significance);
		L.add(type);
		L.add(language_reference_set);
		L.add(acceptabilityId);
		L.add(language_reference_set2);
		L.add(acceptabilityId2);
		L.add(language_reference_set3);
		L.add(acceptabilityId3);
		L.add(language_reference_set4);
		L.add(acceptabilityId4);
		L.add(language_reference_set5);
		L.add(acceptabilityId5);
		Compare.newTranslation.add(L);
		Compare.language = language_Code;
	}

	public void setNewTranslations(String conceptId, String fsn, String pt, String term, String language_Code,
			String case_Significance, String language_reference_set, String acceptabilityId) {
		List<String> L = new ArrayList<String>();
		L.add(conceptId);
		L.add(fsn);
		L.add(pt);
		L.add(term);
		L.add(language_Code);
		L.add(case_Significance);
		L.add("SYNONYM");
		L.add(language_reference_set);
		L.add(acceptabilityId);
		Compare.newTranslation.add(L);
		Compare.language = language_Code;
	}

	public void setNewTranslations(String conceptId, String term, String language_Code, String case_Significance,
			String acceptabilityId, String descriptionID) {
		List<String> L = new ArrayList<String>();
		L.add(conceptId);
		L.add(term);
		L.add(language_Code);
		L.add(case_Significance);
		L.add("SYNONYM"); // TODO: braucht mich das???
		L.add(acceptabilityId);
		L.add(descriptionID);
		Compare.newTranslation.add(L);
		Compare.language = language_Code;
	}

	public void setNewTranslations(String conceptId, String active, String descriptionID, String language_Code,
			String term, String case_Significance, String acceptabilityId) {
		List<String> L = new ArrayList<String>();
		L.add(conceptId);
		L.add(active);
		L.add(descriptionID);
		L.add(language_Code);
		L.add(term);
		L.add(case_Significance);
		L.add(acceptabilityId);

		Compare.newTranslation.add(L);
		Compare.language = language_Code;
	}

	// For method overloading. Allowing to just read the concept ID or description
	// ID and the translation form a file.
	public void setNewTranslations(String id, String term) {
		List<String> L = new ArrayList<String>();
		L.add(id);
		L.add(term);
		Compare.newTranslation.add(L);
	}

	public void setOldTranslation(String conceptId, String fsn, String pt, String term, String languageCode,
			String caseSignificance, String type, String languageReferenceSet1, String acceptabilityId1) {
		// Create a new list to store the translation data
		List<String> L = new ArrayList<String>();

		// Add the necessary fields to the list
		L.add(conceptId); // Concept ID
		L.add(fsn); // FSN
		L.add(pt); // Preferred Term
		L.add(term); // Term (translated term)
		L.add(languageCode); // Language Code
		L.add(caseSignificance); // Case Significance
		L.add(type); // Type (e.g., SYNONYM)

		// Add language reference sets and acceptability IDs
		L.add(languageReferenceSet1);
		L.add(acceptabilityId1);

		// Add the list to the Compare.oldTranslation collection for later comparison
		Compare.oldTranslation.add(L);
	}

	public void setOldTranslation(String conceptId, String status, String fsn, String pt, String term,
			String languageCode, String caseSignificance, String type, String languageReferenceSet1,
			String acceptabilityId1) {
		// Create a new list to store the translation data
		List<String> L = new ArrayList<String>();

		// Add the necessary fields to the list
		L.add(conceptId); // Concept ID
		L.add(status); // status of the concept, active or inactive
		L.add(fsn); // FSN
		L.add(pt); // Preferred Term
		L.add(term); // Term (translated term)
		L.add(languageCode); // Language Code
		L.add(caseSignificance); // Case Significance
		L.add(type); // Type (e.g., SYNONYM)

		// Add language reference sets and acceptability IDs
		L.add(languageReferenceSet1);
		L.add(acceptabilityId1);

		// Add the list to the Compare.oldTranslation collection for later comparison
		Compare.oldTranslation.add(L);
	}

	public void setOldTranslation(String conceptId, String term, String translationId, String acceptabilityId) {
		List<String> L = new ArrayList<String>();
		L.add(conceptId);
		L.add(term);
		L.add(translationId);
		L.add(acceptabilityId);
		Compare.oldTranslation.add(L);
	}

	// For method overloading. Allowing to just read the concept ID or description
	// ID form a file.
	public void setOldTranslation(String id) {
		List<String> L = new ArrayList<String>();
		L.add(id);
		Compare.oldTranslation.add(L);
	}

	// Used to create overview file with content from the DB
	public void setTranslationOverview(String conceptId, String term, String typeId, String languageCode,
			String status) {
		List<String> L = new ArrayList<String>();
		L.add(conceptId);
		L.add(term);
		L.add(typeId);
		L.add(languageCode);
		L.add(status);
		Compare.translationOverview.add(L);
	}

	public void setEszettList(String conceptId, String descriptionID, String active, String typeId, String term,
			String CaseSig, String acceptabilityId) {
		List<String> L = new ArrayList<String>();
		L.add(descriptionID); // Description ID
		L.add(conceptId);
		L.add(active); // Status of description (1 or 0)
		L.add(typeId); // Type (SYNONYM)
		L.add(term); // Term (translated term)
		L.add(CaseSig); // Case Significance
		L.add(acceptabilityId); // Acceptability ID
		Compare.eszettList.add(L);
	}

	public void setCheckForTranslation(String id) {
		List<String> L = new ArrayList<String>();
		L.add(id);
		Compare.newTranslation.add(L);
	}

	public void setNewInactivations(String id, String term, String langageCode) {
		List<String> L = new ArrayList<String>();
		L.add(id);
		L.add(langageCode);
		L.add(""); // placeholder for PT
		L.add(term);
		Compare.newInactivation.add(L);
	}

	public void setOldInactivations(String id, String term) {
		List<String> L = new ArrayList<String>();
		L.add(id);
		L.add(term);
		Compare.oldIactivation.add(L);
	}

	/**
	 * Generates a translations overview file named `TranslationOverview.tsv` that
	 * consolidates translation data for a given set of concept IDs. The file
	 * includes translations in English, German, French, and Italian, and is
	 * structured with a header and rows containing relevant data.
	 *
	 * ### Process: 1. Prepares a header row with columns: - "Concept ID", "GB/US
	 * FSN Term (For reference only)", "Preferred Term (For reference only)",
	 * "Translated Term DE", "Translated Term FR", "Translated Term IT". 2. Collects
	 * unique concept IDs from the `newTranslation` list. 3. Queries the database to
	 * retrieve detailed translation data for the collected concept IDs. 4.
	 * Constructs the translation overview file by: - Adding new rows for concept
	 * IDs not already in the file. - Updating existing rows with additional
	 * translation data when available. - Ensuring multiple translations for the
	 * same term are concatenated with a separator (" | "). 5. Writes the
	 * constructed data into the `TranslationOverview.tsv` file.
	 *
	 * @param destination The directory path where the overview file will be saved.
	 * @throws IOException            If an error occurs during file operations.
	 * @throws ClassNotFoundException If the database driver class cannot be found.
	 * @throws SQLException           If an error occurs during database queries.
	 */
	public void createTranslationsOverview(String CSVfilePath, String destination)
			throws IOException, ClassNotFoundException, SQLException {
		ReadTranslation.readFile(CSVfilePath);

		List<String> header = Arrays.asList("Concept ID", "GB/US FSN Term (For reference only)", "Status",
				"Preferred Term (For reference only)", "Translated Term DE", "Translated Term FR",
				"Translated Term IT");
		List<List<String>> structuredFile = new ArrayList<>();
		structuredFile.add(new ArrayList<>(header));

		long start = System.currentTimeMillis();

		// Use a Set to store unique description IDs
		Set<String> conceptID = new HashSet<>();
		for (List<String> conceptIDset : newTranslation) {
			if (!conceptIDset.isEmpty()) {
				conceptID.add(conceptIDset.get(0));
			}
		}
		long elapsedTime = System.currentTimeMillis() - start;
		dbConnection.getOverviewOfTranslationsDB(conceptID);
		System.out.println("Query executed and array populated. Duration: " + elapsedTime);
		Main.totalTime += elapsedTime;

		// Structure the file
		start = System.currentTimeMillis();

		// Using HashMap to map language codes to column indices
		Map<String, Integer> languageColumnMap = new HashMap<>();
		languageColumnMap.put("en", 3);
		languageColumnMap.put("de", 4);
		languageColumnMap.put("fr", 5);
		languageColumnMap.put("it", 6);

		for (List<String> dbTerm : translationOverview) {
			// Make a copy of the row to avoid modifying the original list (which may be the
			// header)

			List<String> entry = new ArrayList<>(Collections.nCopies(7, "TODO"));

			// Return index of the inner list of structuredFile. Now it knows on which index
			// the concept ID is.
			int indexOfSCTID = findInnerListIndex(structuredFile, dbTerm.get(0));

			// If concept ID is not found, add a new row to structuredFile
			if (indexOfSCTID == -1 && structuredFile.size() > 0) {
				entry.set(0, dbTerm.get(0));
				entry.set(2, dbTerm.get(4));
				if ("900000000000003001".equalsIgnoreCase(dbTerm.get(2))) {
					entry.set(1, dbTerm.get(1));
				} else {
					Integer langIndex = languageColumnMap.get(dbTerm.get(3).toLowerCase());
					if (langIndex != null) {
						entry.set(langIndex, dbTerm.get(1));
					}
				}
				structuredFile.add(entry);
			} else {
				// Update existing row if concept ID is found
				List<String> structuredFileElement = structuredFile.get(indexOfSCTID);
				boolean isUpdated = false;

				if ("900000000000003001".equalsIgnoreCase(dbTerm.get(2))) {
					structuredFileElement.set(1, dbTerm.get(1));
					isUpdated = true;
				} else {
					Integer langIndex = languageColumnMap.get(dbTerm.get(3).toLowerCase());
					if (langIndex != null) {
						String currentTranslation = structuredFileElement.get(langIndex);
						if ("TODO".equals(currentTranslation)) {
							structuredFileElement.set(langIndex, dbTerm.get(1));
							isUpdated = true;
						} else if (!currentTranslation.contains(dbTerm.get(1))) {
							structuredFileElement.set(langIndex, currentTranslation + " | " + dbTerm.get(1));
							isUpdated = true;
						}
					}
				}

				// If updated, set the modified element back to the list
				if (isUpdated) {
					structuredFile.set(indexOfSCTID, structuredFileElement);
				}
			}
		}
		elapsedTime = System.currentTimeMillis() - start;
		Main.totalTime += elapsedTime;
		System.out.println("File structured. Duration: " + elapsedTime);

		// Create the TranslationOverview file
		writeToFile(destination + "\\TranslationOverview.tsv", structuredFile);
	}

	// Method to compare newTranslation with oldTranslation and create a delta file
	private static final String DELTA_HEADER = "Concept ID\tGB/US FSN Term (For reference only)\tPreferred Term (For reference only)\tTranslated Term\tLanguage Code\tCase significance\tType\tLanguage reference set\tAcceptability\tLanguage reference set\tAcceptability\tLanguage reference set\tAcceptability\tLanguage reference set\tAcceptability\tLanguage reference set\tAcceptability\tNotes";

	/**
	 * Generates a delta file (`Delta.tsv`) containing terms from the
	 * `newTranslation` list that are not present in the `oldTranslation` list or
	 * the database.
	 * 
	 * The resulting file adheres to the official description addition template of
	 * SNOMED International. This method is designed to process a large number of
	 * descriptions efficiently, with a benchmark of approximately 14 minutes for
	 * 7,500 descriptions or 84,000 in 80 minutes.
	 * 
	 * Steps: 1. Fetches translations from the database using
	 * `dbConnection.searchTranslations()`. 2. Identifies the delta by comparing
	 * `newTranslation` entries against `oldTranslation` entries using hashed keys
	 * for efficiency. 3. Writes the delta entries to a file named `Delta.tsv` in
	 * the specified output path.
	 * 
	 * @param outputFilePath The path where the `Delta.tsv` file will be saved.
	 * @param languageCode   Language code used in the term addition file.
	 * @throws IOException              If an I/O error occurs during file writing.
	 * @throws SQLException             If there is an error during the database
	 *                                  query.
	 * @throws ClassNotFoundException   If the JDBC driver class is not found.
	 * @throws IllegalArgumentException If the `newTranslation` or `oldTranslation`
	 *                                  lists are null.
	 */
	public void generateDeltaDescAdditions(String CSVfilePath, String outputFilePath, String languageCode)
			throws IOException, SQLException, ClassNotFoundException {

		ReadTranslation.language = languageCode;
		ReadTranslation.readFile(CSVfilePath);

		// Check if the lists are null

		if (newTranslation == null || oldTranslation == null) {
			throw new IllegalArgumentException("Translation lists cannot be null.");
		}

		//////////////////////
		// Manipulation of list (only for DE imports) according to special requirements
		if ("DE".equalsIgnoreCase(languageCode)) {
		    String targetLanguageRefSetId = "2041000195100";
		    int languageCodeIndex = 4; // Index for the "Language Code" field
		    int termIndex = 3;         // Index for the "Term" field
		    int refSetIdIndex = 7;     // Index for the "Language Refset ID" field

		    for (List<String> row : newTranslation) {
		        // Replace "ß" with "ss" in the term
		        if (row.size() > termIndex) {
		            String term = row.get(termIndex);
		            if (term != null && term.contains("ß")) {
		                row.set(termIndex, term.replace("ß", "ss"));
		            }
		        }

		        // Ensure language code is set to "de"
		        while (row.size() <= languageCodeIndex) {
		            row.add("");
		        }
		        row.set(languageCodeIndex, "de");

		        // Ensure language reference set ID is set correctly
		        while (row.size() <= refSetIdIndex) {
		            row.add("");
		        }
		        row.set(refSetIdIndex, targetLanguageRefSetId);
		    }

		    // Final validation: check for correct language code, refset ID, and absence of "ß"
		    long total = newTranslation.size();
		    List<List<String>> invalidRows = new ArrayList<>();

		    for (List<String> row : newTranslation) {
		        boolean isValid = row.size() > refSetIdIndex
		            && "DE".equalsIgnoreCase(row.get(languageCodeIndex))
		            && targetLanguageRefSetId.equals(row.get(refSetIdIndex))
		            && (row.get(termIndex) == null || !row.get(termIndex).contains("ß"));

		        if (!isValid) {
		            invalidRows.add(row);
		        }
		    }

		    if (!invalidRows.isEmpty()) {
		        System.err.println("Warning: Some entries do not meet the requirements:");
		        for (List<String> row : invalidRows) {
		            System.err.println(row);
		        }
		        System.err.println("Invalid entries: " + invalidRows.size() + " / " + total);
		    } else {
		        System.out.println("All " + total + " entries correctly set to language code 'DE', refset ID '2041000195100' and contain no 'ß'.");
		    }
		}
		
		// Use a Set to store unique concept IDs
		Set<String> conceptID = new HashSet<>();
		for (List<String> conceptIDset : newTranslation) {
			if (!conceptIDset.isEmpty()) {
				conceptID.add(conceptIDset.get(0));
			}
		}
		/////////////////////////////////
		
		System.out.println("starting to fetch translations from the database for " + conceptID.size() + " concept IDs.");
		dbConnection.searchTranslations(conceptID); // Fetch translations from the database and populate oldTranslation
		System.out.println("Translations fetched from the database. Starting to compare new and old translations...");
		
		System.out.println("Old translations size: " + oldTranslation.size());
		// Collect all inactive conceptId|term combinations from oldTranslation
		Set<String> inactiveEntries = Compare.oldTranslation.stream()
			    .filter(entry -> "0".equals(entry.get(1))) // only inactive entries
			    .map(entry -> entry.get(0)) // conceptId|term|languageCode
			    .collect(Collectors.toSet());
		System.out.println("Inactive entries collected: " + inactiveEntries.size());
		
		// Collect all known translations as hashes to detect duplicates
		Set<String> oldTranslationsSet = oldTranslation.stream()
			    .map(entry -> generateHash(entry.get(0) + "|" + entry.get(4) + "|" + entry.get(5))) // conceptId|term|languageCode
				.collect(Collectors.toSet());
		
		// Build deltaTranslations by filtering newTranslation
		List<List<String>> deltaTranslations = newTranslation.stream()
			    .filter(entry -> {
			        String conceptId = entry.get(0);

			        // If concept is inactive, exclude it
			        if (inactiveEntries.contains(conceptId)) {
			            return false;
			        }

			        String term = entry.get(3);
			        String NewlanguageCode = entry.get(4);

			        String newKeyHash = generateHash(conceptId + "|" + term + "|" + NewlanguageCode);
			        return !oldTranslationsSet.contains(newKeyHash);
			    })
			    .collect(Collectors.toList());
		
		writeDeltaFile(outputFilePath, deltaTranslations);
	}

	private void writeDeltaFile(String outputFilePath, List<List<String>> deltaTranslations) throws IOException {
		Path deltaFilePath = Paths.get(outputFilePath, "Delta_DescAddition.tsv");

		// Use a larger buffer size for better performance with large files
		try (BufferedWriter writer = Files.newBufferedWriter(deltaFilePath, StandardCharsets.UTF_8,
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

			// Write header once
			writer.write(DELTA_HEADER);
			writer.newLine();

			// Write delta entries in batches
			int batchSize = 1000; // You can adjust this batch size based on your system's capacity
			StringBuilder batch = new StringBuilder();

			for (int i = 0; i < deltaTranslations.size(); i++) {
				List<String> deltaEntry = deltaTranslations.get(i);
				batch.append(String.join("\t", deltaEntry)).append("\n");

				// Write in batches
				if ((i + 1) % batchSize == 0 || i == deltaTranslations.size() - 1) {
					writer.write(batch.toString());
					batch.setLength(0); // Clear the batch
				}
			}
		}
	}

	/**
	 * Generates two TSV files ('NotFoundDescriptionIDs.tsv' and
	 * 'FoundDescriptionIDs.tsv') based on description IDs found or not found in the
	 * database.
	 * 
	 * The input data is expected to follow the SNOMED International template for
	 * inactivating descriptions. The method processes the provided translations,
	 * checks for their presence in the database, and creates the respective files
	 * in the specified output directory.
	 * 
	 * Output Files: - **NotFoundDescriptionIDs.tsv**: Contains descriptions that
	 * are not found in the database. - **FoundDescriptionIDs.tsv**: Contains
	 * descriptions that are found in the database.
	 * 
	 * @param outputDirectoryPath The directory where the output files will be
	 *                            created. The directory will be created if it does
	 *                            not already exist.
	 * @throws IOException            If an I/O error occurs while creating or
	 *                                writing the output files.
	 * @throws ClassNotFoundException If the JDBC driver is not found.
	 * @throws SQLException           If a database error occurs during the query
	 *                                execution.
	 */
	public void generateDeltaDescInactivation(String CSVfilePath, String outputDirectoryPath, String languageCode)
			throws IOException, ClassNotFoundException, SQLException {

		ReadTranslation.language = languageCode;
		ReadTranslation.readFile(CSVfilePath);

		List<List<String>> newIactivation2 = new ArrayList<>();
		for (List<String> row : Compare.newInactivation) {
			newIactivation2.add(new ArrayList<>(row));
		}
		Compare.newInactivation.clear();

		dbConnection.searchDescriptions(newIactivation2);

		Set<List<String>> uniqueOldTranslations = new HashSet<>(oldIactivation);
		Set<List<String>> uniqueNewTranslations = new HashSet<>(newInactivation);
		Set<List<String>> checkInTermspace = new HashSet<>(newIactivation2);

		File oldInactivationFile = new File(outputDirectoryPath + "\\NotFoundDescriptionIDs.tsv");
		File newInactivationFile = new File(outputDirectoryPath + "\\FoundDescriptionIDs.tsv");
		File checkInTermspaceFile = new File(outputDirectoryPath + "\\CheckDescriptionInTermspace.tsv");

		oldInactivationFile.getParentFile().mkdirs();

		writeTranslationInactivationFile(oldInactivationFile, uniqueOldTranslations);
		writeTranslationInactivationFile(newInactivationFile, uniqueNewTranslations);
		writeTranslationInactivationFile(checkInTermspaceFile, checkInTermspace);
	}

	/**
	 * Checks for entries containing the Eszett (ß) in the extension, processes
	 * these entries for inactivation and addition, and generates two output files:
	 * - `Eszett_Inactivation.tsv`: Contains entries to be inactivated due to the
	 * presence of Eszett. - `SS_Addition.tsv`: Contains entries where the Eszett
	 * has been replaced with "ss" for addition purposes.
	 *
	 * The method retrieves relevant entries from the database, processes them into
	 * inactivation and addition sets, and writes these sets into the respective
	 * files.
	 *
	 * @param outputDirectoryPath The directory path where the output files will be
	 *                            saved.
	 * @throws ClassNotFoundException If the database driver class cannot be found.
	 * @throws SQLException           If an error occurs while querying the
	 *                                database.
	 * @throws IOException            If an error occurs during file operations.
	 */
	public void checkEszettInExtension(String outputDirectoryPath)
			throws ClassNotFoundException, SQLException, IOException {

		// Retrieve entries with Eszett from the database
		dbConnection.searchEszett();

		// Prepare inactivation set
		Set<List<String>> esszettInactivationSet = eszettList.stream().map(entry -> {
			List<String> firstElementList = new ArrayList<>();
			firstElementList.add(entry.get(0)); // Only use the first element (description ID)
			return firstElementList;
		}).collect(Collectors.toSet());

		// Write inactivation file
		File eszettInactivationFile = new File(outputDirectoryPath + "\\Eszett_Inactivation.tsv");
		writeTranslationInactivationFile(eszettInactivationFile, esszettInactivationSet);

		System.out.println("Eszett check: Inactivation file successfully created.");

		// Update entries by replacing Eszett with "ss"
		eszettList.forEach(entry -> {
			List<String> updatedEntry = updateEszettEntry(entry);
			entry.clear();
			entry.addAll(updatedEntry);
		});

		// Prepare addition set
		File ssAdditionFile = new File(outputDirectoryPath + "\\SS_Addition.tsv");
		Set<List<String>> ssAdditionSet = new HashSet<>(eszettList);

		// Write addition file
		writeTranslationAdditionFile(ssAdditionFile, ssAdditionSet);
	}

	/**
	 * Generates a file containing the found translation terms for inactivation. The
	 * file is named `Found_TS_inactivation.tsv` and is created in the specified
	 * output directory. - Benchmark: approximately 1.5 minutes for 1,300
	 * descriptions to check.
	 * 
	 * @param outputDirectoryPath The directory where the output file will be
	 *                            created.
	 * @param languageCode        The language code used for the translations.
	 * @throws ClassNotFoundException If the database driver class cannot be found.
	 * @throws SQLException           If an error occurs during database operations.
	 * @throws IOException            If an error occurs while writing to the file.
	 */
	public void generateDeltaTScheckInactivation(String CSVfilePath, String outputDirectoryPath, String languageCode)
			throws ClassNotFoundException, SQLException, IOException {
		ReadTranslation.language = languageCode;
		ReadTranslation.readFile(CSVfilePath);

		List<List<String>> newIactivation2 = new ArrayList<>();
		for (List<String> row : Compare.newInactivation) {
			newIactivation2.add(new ArrayList<>(row));
		}
		Compare.newInactivation.clear();

		dbConnection.searchDescriptionsForInactivationTS(newIactivation2);
		Set<List<String>> foundInactivationTS = new HashSet<>(newInactivation);

		File foundTSinactivationFile = new File(outputDirectoryPath + "\\Found_TS_inactivation.tsv");

		foundTSinactivationFile.getParentFile().mkdirs();

		writeTranslationInactivationFile(foundTSinactivationFile, foundInactivationTS);
	}

	public void createDelta(String outputDirectoryPath) throws ClassNotFoundException, IOException, SQLException {
		// TODO:Method is intended to be used for the delta creation but is not finished yet
//    	generateDeltaDescAdditions(outputDirectoryPath, true);
//    	generateDeltaDescInactivation(outputDirectoryPath, true);

	}

	// Helper methods
	private void writeToFile(String filePath, List<List<String>> data) throws IOException {
		File file = new File(filePath);
		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
			for (List<String> line : data) {
				writer.write(String.join("\t", line));
				writer.newLine();
			}
		}
	}

	private void writeTranslationInactivationFile(File file, Set<List<String>> translations) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(
					"Description ID Or Term\tLanguage Code (require if the term is specified)\tPreferred Term (For reference only)\tTerm (For reference only)\tInactivation Reason\tAssociation Target ID1\tAssociation Target ID2\tAssociation Target ID3\tAssociation Target ID4\tNotes\n");
			for (List<String> entry : translations) {
				writer.write(String.join("\t", entry) + "\n");
			}
		}
	}

	private void writeTranslationAdditionFile(File file, Set<List<String>> translations) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(
					"Concept ID\tGB/US FSN Term (For reference only)\tPreferred Term (For reference only)\tTranslated Term\tLanguage Code\tCase significance\tType\tLanguage reference set\tAcceptability\tLanguage reference set\tAcceptability\tLanguage reference set\tAcceptability\tLanguage reference set\tAcceptability\tNotes\n");
			for (List<String> entry : translations) {
				writer.write(String.join("\t", entry) + "\n");
			}
		}
	}

	private List<String> updateEszettEntry(List<String> entry) {
		List<String> updatedEntry = new ArrayList<>(Collections.nCopies(9, ""));
		updatedEntry.set(0, entry.get(1)); // Concept ID
		updatedEntry.set(3, entry.get(4).replace("ß", "ss")); // Replace ß with ss
		updatedEntry.set(4, "de"); // Language Code
		updatedEntry.set(5, entry.get(5)); // Case significance
		updatedEntry.set(6, entry.get(3)); // Type
		updatedEntry.set(7, "2041000195100"); // Language reference set
		updatedEntry.set(8, entry.get(6)); // Acceptability
		return updatedEntry;
	}

	private String generateHash(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

			StringBuilder hexString = new StringBuilder(2 * hashBytes.length);
			for (byte b : hashBytes) {
				hexString.append(String.format("%02x", b));
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Hashing algorithm not found", e);
		}
	}

	// Used to find the index of the concept that is already in the structuredList.
	private static int findInnerListIndex(List<List<String>> outerList, String element) {
		for (int i = 0; i < outerList.size(); i++) {
			List<String> innerList = outerList.get(i);
			if (innerList.contains(element)) {
				return i; // Return the index of the inner list
			}
		}
		return -1; // Return -1 if the element is not found in any inner list
	}
}
