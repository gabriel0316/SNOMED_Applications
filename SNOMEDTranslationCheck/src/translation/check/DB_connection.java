package translation.check;

import java.sql.*;
import java.util.*;
import java.io.UnsupportedEncodingException;

/**
 * The DB_connection class facilitates interactions with the SNOMED CT database,
 * enabling retrieval and processing of translations in various languages. It
 * supports querying concepts, descriptions, and translations while managing
 * database connections efficiently.
 * 
 * <p>
 * This class provides methods to:
 * <ul>
 * <li>Connect and disconnect from the database.</li>
 * <li>Search translations by language.</li>
 * <li>Handle specific linguistic features, such as terms containing "ß".</li>
 * <li>Batch-process large sets of IDs for efficient querying.</li>
 * </ul>
 * 
 * <p>
 * Author: Pero Grgic
 * </p>
 */
public class DB_connection {

	// JDBC driver and connection settings
	static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static String SERVER_URL = "jdbc:mysql://localhost/sct:ch_beta_juni25?useUnicode=true&characterEncoding=UTF-8";
	static String USERNAME = "root";
	static String PASSWORD = "";
	private Connection connection;
	static Compare CONCEPT = new Compare();

	/**
	 * Opens a new database connection.
	 *
	 * @throws SQLException           If a database access error occurs.
	 * @throws ClassNotFoundException If the JDBC driver class is not found.
	 */
	public void connect() throws SQLException, ClassNotFoundException {
		Class.forName(JDBC_DRIVER);
		try {
			connection = DriverManager.getConnection(SERVER_URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			System.err.println("Failed to establish connection: " + e.getMessage());
		}
	}

	/**
	 * Closes the active database connection.
	 */
	public void disconnect() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				System.err.println("Failed to close connection: " + e.getMessage());
			}
		}
	}

	/**
	 * Searches for translations in the specified language, processes the results,
	 * and populates the `translated` list in the Compare class.
	 * 
	 * @param language the language code for translations (e.g., "de", "fr", "it")
	 * @throws SQLException                 if a database access error occurs
	 * @throws UnsupportedEncodingException if character encoding issues occur
	 * @throws ClassNotFoundException       if the JDBC driver is not found
	 * @throws IllegalArgumentException     if an unsupported language code is
	 *                                      provided
	 */
	public void searchTranslations() throws SQLException, UnsupportedEncodingException, ClassNotFoundException {
		connect();

		String query = """
				  	SELECT
				    d.id,
				    d.conceptId,
				    d.term,
				    d.languageCode,
				    d.typeId,
				    d.caseSignificanceId,
				    d.effectiveTime,
				    d.active AS descriptionActive,
				    c.active AS conceptActive,
				    l.acceptabilityId
				FROM
				    full_description d
				LEFT JOIN
				    full_concept c ON d.conceptId = c.id
				LEFT JOIN
				    full_refset_Language l ON d.id = l.referencedComponentId
				WHERE
				    d.languageCode = ?
				      """;

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, ReadTranslation.language);
			try (ResultSet rs = stmt.executeQuery()) {
				processTranslationResultSet(rs);
			}
		}

		disconnect();
		System.out.println("Translation search completed for language: " + ReadTranslation.language);
	}

	/**
	 * Retrieves an overview of translations for a set of concept IDs, processing
	 * the results into the TranslationOverview list.
	 * 
	 * @param conceptIDs a set of concept IDs to retrieve translations for
	 * @throws SQLException                 if a database access error occurs
	 * @throws UnsupportedEncodingException if character encoding issues occur
	 * @throws ClassNotFoundException
	 */
	public void getOverviewOfTranslationsDB(Set<String> conceptIDs)
			throws SQLException, ClassNotFoundException, UnsupportedEncodingException {
		long start = System.currentTimeMillis();
		connect();
		// Base query for retrieving translation information
		String baseQuery = "SELECT fc.active, fd.conceptId, fd.typeId, fd.term, fd.languageCode\r\n"
				+ "FROM full_concept fc\r\n" + "JOIN (\r\n" + "    SELECT id, MAX(effectiveTime) AS max_time\r\n"
				+ "    FROM full_concept\r\n" + "    GROUP BY id\r\n"
				+ ") latest ON fc.id = latest.id AND fc.effectiveTime = latest.max_time\r\n"
				+ "JOIN full_description fd ON fc.id = fd.conceptId\r\n" + "WHERE fd.active = 1 \r\n" + "AND fc.id";

		// Generate batched queries for concept IDs
		List<String> batchedQueries = buildBatchedQueries(conceptIDs, baseQuery);

		for (String query : batchedQueries) {
			try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

				long elapsedTime = System.currentTimeMillis() - start;
				Main.totalTime += elapsedTime;

				// Process the result set and add entries to TranslationOverview
				while (rs.next()) {
					String term = rs.getString("term");
					CONCEPT.setTranslationOverview(rs.getString("conceptId"), term, rs.getString("typeId"),
							rs.getString("languageCode"), rs.getString("active"));
				}
			}
		}
		disconnect();
	}

	/**
	 * Searches for descriptions by ID, populating the Compare class with
	 * information about newly found and missing descriptions.
	 * 
	 * @param descriptionIds a set of description IDs and terms to search for
	 * @throws SQLException           if a database access error occurs
	 * @throws ClassNotFoundException if the JDBC driver is not found
	 */
	public void searchDescriptions(List<List<String>> newInactivation) throws SQLException, ClassNotFoundException {

		Set<String> descriptionIDs = new HashSet<>();
		Set<String> descriptionTerms = new HashSet<>();

		for (List<String> descriptionIDSet : newInactivation) {
			if (!descriptionIDSet.isEmpty()) {
				descriptionIDs.add(descriptionIDSet.get(0));
			}
		}

		connect();

		// Prepare the base query, selecting only the description ID
		String baseQueryDescID = "SELECT DISTINCT id, term, active FROM full_description WHERE id";

		// Initialize a Set to keep track of IDs found in the database to avoid
		// duplicates
		Set<String> notFoundIds = new HashSet<>(descriptionIDs); // Initially assume all IDs are not found

		// Build and execute batch queries
		List<String> queriesDescID = buildBatchedQueries(descriptionIDs, baseQueryDescID);

		for (String query : queriesDescID) {
			System.out.println("Executing query: " + query);
			try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
				while (rs.next()) {
					String descriptionID = rs.getString("id");
					String descriptionTerm = rs.getString("term");
					if (rs.getString("active").equals("1")) {
						CONCEPT.setNewInactivations(descriptionID, descriptionTerm, ReadTranslation.language); // Add to newTranslation list
						notFoundIds.remove(descriptionID); // Remove from notFoundIds if found
						newInactivation.removeIf(entry -> entry.size() > 0 && entry.get(0).equals(descriptionID)); // Remove from newInactivation if found
					} else if (rs.getString("active") == "0") {
						notFoundIds.remove(descriptionID);
						newInactivation.removeIf(entry -> entry.size() > 0 && entry.get(0).equals(descriptionID)); 
					}

				}
			}
		}

		for (String missingID : notFoundIds) {
			for (List<String> descriptionTermSet : newInactivation) {
				if (missingID.equals(descriptionTermSet.get(0))) {
					descriptionTerms.add(descriptionTermSet.get(1));
				}
			}
		}
		System.out.println("Startung to query with terms for not found description IDs");
		String baseQueryDescTerm = "SELECT DISTINCT id, term, active FROM full_description WHERE term";
		List<String> queriesDescTerm = buildBatchedQueries(descriptionTerms, baseQueryDescTerm);
		for (String query : queriesDescTerm) {
			System.out.println("Executing query: " + query);
			try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
				while (rs.next()) {
					String descriptionID = rs.getString("id");
					String descriptionTerm = rs.getString("term");
					if (rs.getString("active").equals("1")) {
						System.out.println("Matching and active description with term comparison found");
						System.out.println(descriptionID + " | " + descriptionTerm);
						CONCEPT.setOldInactivations(descriptionID, descriptionTerm); // Add to newTranslation list
						notFoundIds.remove(descriptionID); // Remove from notFoundIds if found
					}
				}
			}
		}

		System.out.println(notFoundIds.size());
		// Close the database connection
		disconnect();
	}

	public void searchDescriptionsForInactivationTS(List<List<String>> newInactivation)
			throws SQLException, ClassNotFoundException {
		connect();

		String query = """
				SELECT fd.conceptId, fd.term, fd.languageCode, fd.id, fd.active
				FROM full_description fd
				INNER JOIN (
				    SELECT conceptId, term, languageCode, MAX(CAST(effectiveTime AS UNSIGNED)) AS max_effectiveTime
				    FROM full_description
				    WHERE languageCode = ?
				    GROUP BY conceptId, term, languageCode
				) latest
				ON fd.conceptId = latest.conceptId
				AND fd.term = latest.term
				AND fd.languageCode = latest.languageCode
				AND CAST(fd.effectiveTime AS UNSIGNED) = latest.max_effectiveTime
				WHERE fd.active = 1""";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, ReadTranslation.language);
			System.out.println("Executing query: " + stmt.toString());

			try (ResultSet rs = stmt.executeQuery()) {
				// Build a set of the combinations you are looking for for quick reference
				Set<String> searchSet = new HashSet<>();
				for (List<String> entry : newInactivation) {
					String key = entry.get(0) + "||" + entry.get(3); // conceptId || term
					searchSet.add(key);
				}

				while (rs.next()) {
					String conceptId = rs.getString("conceptId");
					String term = rs.getString("term");
					String id = rs.getString("id");
					String key = conceptId + "||" + term;

					if (searchSet.contains(key)) {
						CONCEPT.setNewInactivations(id, term, ReadTranslation.language);
					}
				}
			}
		}

		disconnect();
	}

	/**
	 * Retrieves and processes concepts containing the character "ß" in their German
	 * translations, adding them to the `EszettList` in the Compare class.
	 * 
	 * @throws SQLException                 if a database access error occurs
	 * @throws ClassNotFoundException       if the JDBC driver is not found
	 * @throws UnsupportedEncodingException if character encoding issues occur
	 */
	public void searchEszett() throws SQLException, ClassNotFoundException, UnsupportedEncodingException {

		connect();
		String query = "SELECT fd.id, fd.active, fd.conceptId, fd.typeId, fd.term, fd.caseSignificanceId, fr.acceptabilityId FROM full_description fd INNER JOIN full_refset_Language fr ON fd.id = fr.referencedComponentId WHERE fd.languageCode = 'de' AND fd.term REGEXP 'ß'";
		System.out.println("Eszett check: Starting with query...");
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			System.out.println("Eszett check: All translations retrieved from DB - proceeding with filling list...");
			while (rs.next()) {
				String descId = rs.getString("id");
				String conceptId = rs.getString("conceptId");
				String active = rs.getString("active");
				String type = rs.getString("typeId");
				String term = new String(rs.getString("term"));
				String caseSignificance = rs.getString("caseSignificanceId");
				String acceptabilityId = rs.getString("acceptabilityId");

				// Call setEszettList with retrieved data
				CONCEPT.setEszettList(conceptId, descId, active, type, term, caseSignificance, acceptabilityId);
			}
			System.out.println("Eszett check: All translations in list - proceeding with conversion...");
		}

		disconnect();
	}

	/**
	 * Constructs batched SQL queries for a set of IDs, optimizing query execution
	 * by dividing large sets into manageable chunks.
	 * 
	 * @param ids       a set of IDs to query (e.g., concept IDs or description IDs)
	 * @param baseQuery the base SQL query without filtering (e.g., "SELECT ...
	 *                  WHERE")
	 * @param type      the type of ID to query ("conceptId" or "descriptionId")
	 * @return a list of SQL query strings, each containing up to `batchSize` IDs
	 * @throws IllegalArgumentException if an invalid `idType` is provided
	 */
	public static List<String> buildBatchedQueries(Set<String> ids, String baseQuery) {
		List<String> queries = new ArrayList<>();
		int batchSize = 500; // Adjust based on DB limitations
		List<String> batch = new ArrayList<>(batchSize);

		// Create query batches based on the ID type
		for (String id : ids) {
			batch.add("'" + id + "'");
			if (batch.size() == batchSize) {
				// Use the idType (conceptId or descriptionId) in the IN clause
				queries.add(baseQuery + " IN (" + String.join(",", batch) + ")");
				batch.clear();
			}
		}

		// Add remaining IDs in the last batch if any
		if (!batch.isEmpty()) {
			queries.add(baseQuery + " IN (" + String.join(",", batch) + ")");
		}

		return queries;
	}

	/**
	 * Processes the given `ResultSet` to extract translation data and populates the
	 * old translation data for concepts.
	 * 
	 * This method iterates through the rows of the provided `ResultSet` and
	 * retrieves relevant fields such as `conceptId`, `term`, `languageCode`,
	 * `caseSignificanceId`, and `acceptabilityId`. The extracted data is passed to
	 * the `CONCEPT.setOldTranslation` method to populate the old translation
	 * entries for a concept.
	 * 
	 * @param rs The `ResultSet` object containing translation data retrieved from
	 *           the database. Expected fields in the result set include: -
	 *           `conceptId` (String): The unique identifier for the concept. -
	 *           `term` (String): The term associated with the concept, which will
	 *           be decoded before processing. - `languageCode` (String): The
	 *           language code of the term. - `caseSignificanceId` (String):
	 *           Specifies whether the term is case-sensitive. - `acceptabilityId`
	 *           (String): Indicates the acceptability of the term in the given
	 *           language.
	 * 
	 * @throws SQLException If an error occurs while accessing the `ResultSet`
	 *                      object.
	 */
	private void processTranslationResultSet(ResultSet rs) throws SQLException {
		while (rs.next()) {
			String conceptId = rs.getString("conceptId");
			String conceptStatus = rs.getString("conceptActive");
			String term = rs.getString("term");
			String languageCode = rs.getString("languageCode");
			String caseSignificance = rs.getString("caseSignificanceId");
			String acceptabilityId = rs.getString("acceptabilityId");
			CONCEPT.setOldTranslation(conceptId, conceptStatus, "", // FSN placeholder
					"", // Preferred Term placeholder
					term, languageCode, caseSignificance, "SYNONYM", "Language Reference Set 1", acceptabilityId);
		}
	}

}