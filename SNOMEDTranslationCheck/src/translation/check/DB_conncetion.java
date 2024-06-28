package translation.check;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * This class connects to the SNOMED database and retrieves the translation in the corresponding language.
 * @author Pero Grgic
 *
 */
public class DB_conncetion {

	// Driver needed to connect to the SNOMED database
    static String jdbcDriver = "com.mysql.jdbc.Driver";
	public static Connection conn = null;
	
	//Credential for the connection
	static String username = "root";
	static String password = "";
	static String serverUrl = "jdbc:mysql://localhost/ch:sct-june23";
	static Compare concept = new Compare();

	/**
	 * Connects to the SNOMED data base  with the given driver.
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void connectDB () throws SQLException, ClassNotFoundException{
		Class.forName(jdbcDriver);
		conn = DriverManager.getConnection(serverUrl,username,password);
	}
	
	/**¨
	 * Closes off the connection to the SNOMED database.
	 * @throws SQLException
	 */
	public static void closeConnectionDB() throws SQLException{
		conn.close();
	}
	
	
	/**
	 * This method searches concepts which have a translation in the given language and fills the lists them in the "translated"-list of the Compare class.
	 * @param Language code of the language in which to retrieve the terms. E.g. de, fr or it.
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */
	public static void searchTranslations(String language) throws SQLException, UnsupportedEncodingException {
		//Queries to execute according to the give language code
		String queryGetDETerms = "SELECT * FROM `full_description` WHERE languageCode = 'de'";
		String queryGetFRTerms = "SELECT * FROM `full_description` WHERE languageCode = 'fr'";
		String queryGetITTerms = "SELECT * FROM `full_description` WHERE languageCode = 'it'";
		
		if(language.equalsIgnoreCase("de")){
		    Statement stmt = conn.createStatement();
		    ResultSet rs = stmt.executeQuery(queryGetDETerms);   		    
            while (rs.next()) {
            	
            	String queryGetAcceptability = "SELECT `acceptabilityId` FROM `full_refset_Language` WHERE `referencedComponentId` ="+rs.getString("id");
        		Statement AccStmt = conn.createStatement();
        		ResultSet AccRs = AccStmt.executeQuery(queryGetAcceptability); 
        		AccRs.next();
            	//The encoding form MySQL is in ISO-8859-1. Before we can compare the two lists we have to make sure the ISO-8859-1 gets converted to UTF-8
            	String encodedWithISO88591 = rs.getString("term");
            	String decodedToUTF8 = new String(encodedWithISO88591.getBytes("ISO-8859-1"), "UTF-8");
            	concept.setOldTranslation(rs.getString("conceptId"), decodedToUTF8, rs.getString("id"), AccRs.getString("acceptabilityId"));
				}
            }
			
		if(language == "fr"){	
	        Statement stmt = conn.createStatement();
	        ResultSet rs = stmt.executeQuery(queryGetFRTerms);
	        while (rs.next()) {
	        	
	        	String queryGetAcceptability = "SELECT `acceptabilityId` FROM `full_refset_Language` WHERE `referencedComponentId` ="+rs.getString("id");
        		Statement AccStmt = conn.createStatement();
        		ResultSet AccRs = AccStmt.executeQuery(queryGetAcceptability);
	        	
	        	//The encoding form MySQL is in ISO-8859-1. Before we can compare the two lists we have to make sure the ISO-8859-1 gets converted to UTF-8
	        	String encodedWithISO88591 = rs.getString("term");
            	String decodedToUTF8 = new String(encodedWithISO88591.getBytes("ISO-8859-1"), "UTF-8");
            	concept.setOldTranslation(rs.getString("conceptId"), decodedToUTF8, rs.getString("id"), AccRs.getString("acceptabilityId"));
			}        
		}
		
		if(language == "it"){
			Statement stmt = conn.createStatement();
	        ResultSet rs = stmt.executeQuery(queryGetITTerms);
	        while (rs.next()) {
	        	
	        	String queryGetAcceptability = "SELECT `acceptabilityId` FROM `full_refset_Language` WHERE `referencedComponentId` ="+rs.getString("id");
        		Statement AccStmt = conn.createStatement();
        		ResultSet AccRs = AccStmt.executeQuery(queryGetAcceptability);
	        	
	        	//The encoding form MySQL is in ISO-8859-1. Before we can compare the two lists we have to make sure the ISO-8859-1 gets converted to UTF-8
	        	String encodedWithISO88591 = rs.getString("term");
            	String decodedToUTF8 = new String(encodedWithISO88591.getBytes("ISO-8859-1"), "UTF-8");
            	concept.setOldTranslation(rs.getString("conceptId"), decodedToUTF8, rs.getString("id"), AccRs.getString("acceptabilityId"));	
			}
		}	
	}
	
	public static void getOverviewOfTranslationsDB(String conceptID) throws SQLException, UnsupportedEncodingException {
		long start = System.currentTimeMillis();    
		String queryGetInformation = "SELECT `conceptId`, `typeId`, `term`, `languageCode` FROM `full_description` WHERE active= 1 AND conceptID IN ("+conceptID+");";
		
		    Statement stmt = conn.createStatement();
		    ResultSet rs = stmt.executeQuery(queryGetInformation);
			long elapsedTime = System.currentTimeMillis() - start;
			System.out.println("Query durchgeführt. Dauer: "+ elapsedTime);
			Main.totalTime = Main.totalTime + elapsedTime;
		    
			
			start = System.currentTimeMillis();
			//Puts the result set in the arrayList
            while (rs.next()) { 	
            	//The encoding form MySQL is in ISO-8859-1. Before we can compare the two lists we have to make sure the ISO-8859-1 gets converted to UTF-8
            	String encodedWithISO88591 = rs.getString("term");
            	String decodedToUTF8 = new String(encodedWithISO88591.getBytes("ISO-8859-1"), "UTF-8");
            	concept.setTranslationOverview(rs.getString("conceptId"), decodedToUTF8, rs.getString("typeId"), rs.getString("languageCode"));
				}
            elapsedTime = System.currentTimeMillis() - start;
            System.out.println("Resultset in Array TranslationOverview gefüllt. Dauer: "+elapsedTime);
            Main.totalTime = Main.totalTime + elapsedTime;
            }
	}