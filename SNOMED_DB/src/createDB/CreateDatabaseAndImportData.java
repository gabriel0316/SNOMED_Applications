package createDB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateDatabaseAndImportData {
	
	// Driver needed to connect to the SNOMED database
	public static Connection conn = null;
	static String ReleaseFilePathCH= "C:\\Users\\Pero Grgic\\Desktop\\eHS\\SnomedCT_ManagedServiceCH_PRODUCTION_CH1000195_20230607T120000Z";
	static String ReleaseFilePath= "C:\\Users\\Pero Grgic\\Desktop\\eHS\\SnomedCT_InternationalRF2_PRODUCTION_20231001T120000Z";
	static String ReleaseDate="20231001";
	static String ReleaseDateCH="CH1000195_20230607";

	    public static void main(String[] args) {
	        // Datenbankverbindung konfigurieren
	        String dbName = "ch:sct-june23";
	        String jdbcDriver = "com.mysql.jdbc.Driver";
	    	
	    	
	    	//Credential for the connection
	    	String dbUser = "root";
	    	String dbPassword = "";
	    	String dbURL = "jdbc:mysql://localhost/";

	        try {
	            // Verbindung zur Datenbank herstellen
//	        	Class.forName(jdbcDriver);
//	            Connection connection = DriverManager.getConnection(dbURL, dbUser, dbPassword);
//	            Statement statement = connection.createStatement();

	            // Datenbank erstellen
	            String createDBQuery = "DROP DATABASE IF EXISTS `" + dbName + "`;" + "\n"
	                    + "CREATE DATABASE `" + dbName + "` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;" + "\n"
	                    + "USE `" + dbName + "`;" + "\n"
	                    + "SET GLOBAL net_write_timeout = 60;" + "\n"
	                    + "SET GLOBAL net_read_timeout = 120;" + "\n"
	                    + "SET GLOBAL sql_mode = '';" + "\n"
	                    + "SET SESSION sql_mode = '';";
	           // statement.executeUpdate(createDBQuery);
	            System.out.println(createDBQuery);

	            // Verbindung zur neuen Datenbank herstellen
//	            connection = DriverManager.getConnection(dbURL + dbName, dbUser, dbPassword);
//	            statement = connection.createStatement();

	            // Tabelle erstellen und Daten aus Dateien auf dem Desktop importieren
	            String createConceptTableQuery = "DROP TABLE IF EXISTS `full_concept`;\r\n"
	            		+ " \r\n"
	            		+ "CREATE TABLE `full_concept` (\r\n"
	            		+ "    `id` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    `effectiveTime` DATETIME NOT NULL DEFAULT  '2000-01-31 00:00:00',\r\n"
	            		+ "    `active` TINYINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    `moduleId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    `definitionStatusId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    PRIMARY KEY (`id`,`effectiveTime`))\r\n"
	            		+ "    ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;";
//	            statement.executeUpdate(createConceptTableQuery);
	            System.out.println(createConceptTableQuery);

	            
	            String createDescriptionTableQuery = "DROP TABLE IF EXISTS `full_description`;\r\n"
	            		+ " \r\n"
	            		+ "CREATE TABLE `full_description` (\r\n"
	            		+ "    `id` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    `effectiveTime` DATETIME NOT NULL DEFAULT  '2000-01-31 00:00:00',\r\n"
	            		+ "    `active` TINYINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    `moduleId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    `conceptId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    `languageCode` VARCHAR (3) NOT NULL DEFAULT '',\r\n"
	            		+ "    `typeId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    `term` TEXT NOT NULL,\r\n"
	            		+ "    `caseSignificanceId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    PRIMARY KEY (`id`,`effectiveTime`))\r\n"
	            		+ "    ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;";
//	            statement.executeUpdate(createDescriptionTableQuery);
	            System.out.println(createDescriptionTableQuery);

	            
	            String createRelationshipTableQuery = "DROP TABLE IF EXISTS `full_relationship`;\r\n"
	            		+ " \r\n"
	            		+ "CREATE TABLE `full_relationship` (\r\n"
	            		+ "    `id` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    `effectiveTime` DATETIME NOT NULL DEFAULT  '2000-01-31 00:00:00',\r\n"
	            		+ "    `active` TINYINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    `moduleId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    `sourceId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    `destinationId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    `relationshipGroup` INT NOT NULL DEFAULT 0,\r\n"
	            		+ "    `typeId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    `characteristicTypeId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    `modifierId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "    PRIMARY KEY (`id`,`effectiveTime`))\r\n"
	            		+ "    ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;";
//	            statement.executeUpdate(createRelationshipTableQuery);
	            System.out.println(createRelationshipTableQuery);

	            
	            String createSimpleRefsetTableQuery = "DROP TABLE IF EXISTS `full_refset_Simple`;\r\n"
	            		+ " \r\n"
	            		+ "CREATE TABLE `full_refset_Simple` (\r\n"
	            		+ "  `id` char(36) NOT NULL DEFAULT '',\r\n"
	            		+ "  `effectiveTime` DATETIME NOT NULL\r\n"
	            		+ "        DEFAULT  '2000-01-31 00:00:00',\r\n"
	            		+ "  `active` TINYINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  `moduleId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  `refsetId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  `referencedComponentId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  PRIMARY KEY (`id`,`effectiveTime`))\r\n"
	            		+ "  ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;";
//	            statement.executeUpdate(createSimpleRefsetTableQuery);
	            System.out.println(createSimpleRefsetTableQuery);

	            
	            String createExtendedMapRefsetTableQuery = "DROP TABLE IF EXISTS `full_refset_ExtendedMap`;\r\n"
	            		+ " \r\n"
	            		+ "CREATE TABLE `full_refset_ExtendedMap` (\r\n"
	            		+ "  `id` char(36) NOT NULL DEFAULT '',\r\n"
	            		+ "  `effectiveTime` DATETIME NOT NULL\r\n"
	            		+ "        DEFAULT  '2000-01-31 00:00:00',\r\n"
	            		+ "  `active` TINYINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  `moduleId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  `refsetId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  `referencedComponentId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  `mapGroup` INT NOT NULL DEFAULT 0,\r\n"
	            		+ "  `mapPriority` INT NOT NULL DEFAULT 0,\r\n"
	            		+ "  `mapRule` TEXT NOT NULL,\r\n"
	            		+ "  `mapAdvice` TEXT NOT NULL,\r\n"
	            		+ "  `mapTarget` VARCHAR (200) NOT NULL DEFAULT '',\r\n"
	            		+ "  `correlationId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  `mapCategoryId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  PRIMARY KEY (`id`,`effectiveTime`))\r\n"
	            		+ "  ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;";
//	            statement.executeUpdate(createExtendedMapRefsetTableQuery);
	            System.out.println(createExtendedMapRefsetTableQuery);

	            
	            String createLanguageRefsetTableQuery = "DROP TABLE IF EXISTS `full_refset_Language`;\r\n"
	            		+ " \r\n"
	            		+ "CREATE TABLE `full_refset_Language` (\r\n"
	            		+ "  `id` char(36) NOT NULL DEFAULT '',\r\n"
	            		+ "  `effectiveTime` DATETIME NOT NULL\r\n"
	            		+ "        DEFAULT  '2000-01-31 00:00:00',\r\n"
	            		+ "  `active` TINYINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  `moduleId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  `refsetId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  `referencedComponentId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  `acceptabilityId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  PRIMARY KEY (`id`,`effectiveTime`))\r\n"
	            		+ "  ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;";
//	            statement.executeUpdate(createLanguageRefsetTableQuery);
	            System.out.println(createLanguageRefsetTableQuery);

	            
	            String createModuleDependencyRefsetTableQuery = "DROP TABLE IF EXISTS `full_refset_ModuleDependency`;\r\n"
	            		+ " \r\n"
	            		+ "CREATE TABLE `full_refset_ModuleDependency` (\r\n"
	            		+ "  `id` char(36) NOT NULL DEFAULT '',\r\n"
	            		+ "  `effectiveTime` DATETIME NOT NULL\r\n"
	            		+ "        DEFAULT  '2000-01-31 00:00:00',\r\n"
	            		+ "  `active` TINYINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  `moduleId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  `refsetId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  `referencedComponentId` BIGINT NOT NULL DEFAULT  0,\r\n"
	            		+ "  `sourceEffectiveTime` DATETIME NOT NULL\r\n"
	            		+ "        DEFAULT  '2000-01-31 00:00:00',\r\n"
	            		+ "  `targetEffectiveTime` DATETIME NOT NULL\r\n"
	            		+ "        DEFAULT  '2000-01-31 00:00:00',\r\n"
	            		+ "  PRIMARY KEY (`id`,`effectiveTime`))\r\n"
	            		+ "  ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;";
//	            statement.executeUpdate(createModuleDependencyRefsetTableQuery);
	            System.out.println(createModuleDependencyRefsetTableQuery);


	            //Import of the international Edition
	            String importConceptsIntEdQuery = "LOAD DATA LOCAL INFILE '"+ReleaseFilePath+"\\Full\\Terminology\\sct2_Concept_Full_INT_"+ReleaseDate+".txt'\r\n"
	            		+ "INTO TABLE `full_concept`\r\n"
	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
	            		+ " IGNORE 1 LINES\r\n"
	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`definitionStatusId`);";
//	            statement.executeUpdate(importConceptsIntEdQuery);
	            System.out.println(importConceptsIntEdQuery);

	            
	            String importDescriptionIntEdQuery = "LOAD DATA LOCAL INFILE '"+ReleaseFilePath+"\\Full\\Terminology\\sct2_Description_Full-en_INT_"+ReleaseDate+".txt'\r\n"
	            		+ "INTO TABLE `full_description`\r\n"
	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
	            		+ " IGNORE 1 LINES\r\n"
	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`conceptId`,`languageCode`,`typeId`,`term`,`caseSignificanceId`);";
//	            statement.executeUpdate(importDescriptionIntEdQuery);
	            System.out.println(importDescriptionIntEdQuery);

	            
	            String importRelationshipIntEdQuery = "LOAD DATA LOCAL INFILE '"+ReleaseFilePath+"\\Full\\Terminology\\sct2_Relationship_Full_INT_"+ReleaseDate+".txt'\r\n"
	            		+ "INTO TABLE `full_relationship`\r\n"
	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
	            		+ " IGNORE 1 LINES\r\n"
	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`sourceId`,`destinationId`,`relationshipGroup`,`typeId`,`characteristicTypeId`,`modifierId`);";
//	            statement.executeUpdate(importRelationshipIntEdQuery);
	            System.out.println(importRelationshipIntEdQuery);

//	            The international Edition does not have a simple refset
//	            String importSimpleRefsetIntEdQuery = "LOAD DATA LOCAL INFILE '"+ReleaseFilePath+"\\Full\\Content\\der2_Refset_SimpleFull_INT_"+ReleaseDate+".txt'\r\n"
//	            		+ "INTO TABLE `full_refset_simple`\r\n"
//	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
//	            		+ " IGNORE 1 LINES\r\n"
//	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`refSetId`,`referencedComponentId`);";
//	            statement.executeUpdate(importSimpleRefsetIntEdQuery);
//	            System.out.println(importSimpleRefsetIntEdQuery);

	            
	            String importLanguageRefsetsIntEdQuery = "LOAD DATA LOCAL INFILE '"+ReleaseFilePath+"\\Full\\Refset\\Language\\der2_cRefset_LanguageFull-en_INT_"+ReleaseDate+".txt'\r\n"
	            		+ "INTO TABLE `full_refset_Language`\r\n"
	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
	            		+ " IGNORE 1 LINES\r\n"
	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`refsetId`,`referencedComponentId`,`acceptabilityId`);";
//	            statement.executeUpdate(importLanguageRefsetsIntEdQuery);
	            System.out.println(importLanguageRefsetsIntEdQuery);
	            
//	            TODO: beim Ausführen macht mapGroup ein Problem. 
//	            String importExtendedMapRefsetsIntEdQuery = "LOAD DATA LOCAL INFILE '"+ReleaseFilePath+"\\Full\\Refset\\Map\\der2_iisssccRefset_ExtendedMapFull_INT_"+ReleaseDate+".txt'\r\n"
//	            		+ "INTO TABLE `full_refset_Language`\r\n"
//	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
//	            		+ " IGNORE 1 LINES\r\n"
//	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`refsetId`,`referencedComponentId`,`mapGroup`,`mapPriority`,`mapRule`,`mapAdvice`,`mapTarget`,`correlationId`,`mapCategoryId`);";
//	            statement.executeUpdate(importExtendedMapRefsetsIntEdQuery);
//	            System.out.println(importExtendedMapRefsetsIntEdQuery);
	            
	            //Import of the Swiss Extension
	            String importConceptsCHdQuery = "LOAD DATA LOCAL INFILE '"+ReleaseFilePathCH+"\\Full\\Terminology\\sct2_Concept_Full_"+ReleaseDateCH+".txt'\r\n"
	            		+ "INTO TABLE `full_concept`\r\n"
	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
	            		+ " IGNORE 1 LINES\r\n"
	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`definitionStatusId`);";
//	            statement.executeUpdate(importConceptsCHdQuery);
	            System.out.println(importConceptsCHdQuery);
	            
	            String importDescriptionDeCHQuery= "LOAD DATA LOCAL INFILE '"+ReleaseFilePathCH+"\\Full\\Terminology\\sct2_Description_Full-de-ch_"+ReleaseDateCH+".txt'\r\n"
	            		+ "INTO TABLE `full_description`\r\n"
	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
	            		+ " IGNORE 1 LINES\r\n"
	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`conceptId`,`languageCode`,`typeId`,`term`,`caseSignificanceId`);";
//	            statement.executeUpdate(importDescriptionDeCHQuery);
	            System.out.println(importDescriptionDeCHQuery);
	            
	            String importDescriptionFrCHQuery= "LOAD DATA LOCAL INFILE '"+ReleaseFilePathCH+"\\Full\\Terminology\\sct2_Description_Full-fr-ch_"+ReleaseDateCH+".txt'\r\n"
	            		+ "INTO TABLE `full_description`\r\n"
	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
	            		+ " IGNORE 1 LINES\r\n"
	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`conceptId`,`languageCode`,`typeId`,`term`,`caseSignificanceId`);";
//	            statement.executeUpdate(importDescriptionFrCHQuery);
	            System.out.println(importDescriptionFrCHQuery);
	            
	            String importDescriptionItCHQuery= "LOAD DATA LOCAL INFILE '"+ReleaseFilePathCH+"\\Full\\Terminology\\sct2_Description_Full-it-ch_"+ReleaseDateCH+".txt'\r\n"
	            		+ "INTO TABLE `full_description`\r\n"
	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
	            		+ " IGNORE 1 LINES\r\n"
	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`conceptId`,`languageCode`,`typeId`,`term`,`caseSignificanceId`);";
//	            statement.executeUpdate(importDescriptionItCHQuery);
	            System.out.println(importDescriptionItCHQuery);
	            
	            String importDescriptionEnCHQuery= "LOAD DATA LOCAL INFILE '"+ReleaseFilePathCH+"\\Full\\Terminology\\sct2_Description_Full-en_"+ReleaseDateCH+".txt'\r\n"
	            		+ "INTO TABLE `full_description`\r\n"
	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
	            		+ " IGNORE 1 LINES\r\n"
	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`conceptId`,`languageCode`,`typeId`,`term`,`caseSignificanceId`);";
//	            statement.executeUpdate(importDescriptionEnCHQuery);
	            System.out.println(importDescriptionEnCHQuery);
	            
	            String importRelationshipCHQuery= "LOAD DATA LOCAL INFILE '"+ReleaseFilePathCH+"\\Full\\Terminology\\sct2_Relationship_Full_"+ReleaseDateCH+".txt'\r\n"
	            		+ "INTO TABLE `full_relationship`\r\n"
	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
	            		+ " IGNORE 1 LINES\r\n"
	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`sourceId`,`destinationId`,`relationshipGroup`,`typeId`,`characteristicTypeId`,`modifierId`);";
//	            statement.executeUpdate(importRelationshipCHQuery);
	            System.out.println(importRelationshipCHQuery);
	            
	            //The swiss extension does not contain a simple refset
//	            String importSimpleRefsetCHQuery= "LOAD DATA LOCAL INFILE '"+ReleaseFilePathCH+"\\Full\\Content\\der2_Refset_SimpleFull_INT_"+ReleaseDateCH+".txt'\r\n"
//	            		+ "INTO TABLE `full_refset_simple`\r\n"
//	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
//	            		+ " IGNORE 1 LINES\r\n"
//	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`refSetId`,`referencedComponentId`);";
//	            statement.executeUpdate(importSimpleRefsetCHQuery);
	            
	            String importLanguageRefsetsDeCHQuery= "LOAD DATA LOCAL INFILE '"+ReleaseFilePathCH+"\\Full\\Refset\\Language\\der2_cRefset_LanguageFull-de-ch_"+ReleaseDateCH+".txt'\r\n"
	            		+ "INTO TABLE `full_refset_Language`\r\n"
	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
	            		+ " IGNORE 1 LINES\r\n"
	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`refsetId`,`referencedComponentId`,`acceptabilityId`);";
//	            statement.executeUpdate(importLanguageRefsetsDeCHQuery);
	            System.out.println(importLanguageRefsetsDeCHQuery);
	            
	            String importLanguageRefsetsFrCHQuery= "LOAD DATA LOCAL INFILE '"+ReleaseFilePathCH+"\\Full\\Refset\\Language\\der2_cRefset_LanguageFull-fr-ch_"+ReleaseDateCH+".txt'\r\n"
	            		+ "INTO TABLE `full_refset_Language`\r\n"
	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
	            		+ " IGNORE 1 LINES\r\n"
	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`refsetId`,`referencedComponentId`,`acceptabilityId`);";
//	            statement.executeUpdate(importLanguageRefsetsFrCHQuery);
	            System.out.println(importLanguageRefsetsFrCHQuery);
	            
	            String importLanguageRefsetsItCHQuery= "LOAD DATA LOCAL INFILE '"+ReleaseFilePathCH+"\\Full\\Refset\\Language\\der2_cRefset_LanguageFull-it-ch_"+ReleaseDateCH+".txt'\r\n"
	            		+ "INTO TABLE `full_refset_Language`\r\n"
	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
	            		+ " IGNORE 1 LINES\r\n"
	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`refsetId`,`referencedComponentId`,`acceptabilityId`);";
//	            statement.executeUpdate(importLanguageRefsetsItCHQuery);
	            System.out.println(importLanguageRefsetsItCHQuery);
	            
	            String importLanguageRefsetsEnCHQuery= "LOAD DATA LOCAL INFILE '"+ReleaseFilePathCH+"\\Full\\Refset\\Language\\der2_cRefset_LanguageFull-en_"+ReleaseDateCH+".txt'\r\n"
	            		+ "INTO TABLE `full_refset_Language`\r\n"
	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
	            		+ " IGNORE 1 LINES\r\n"
	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`refsetId`,`referencedComponentId`,`acceptabilityId`);";
//	            statement.executeUpdate(importLanguageRefsetsEnCHQuery);
	            System.out.println(importLanguageRefsetsEnCHQuery);
	            
	            //The swiss extension does not contain a map
//	            String importExtendedMapRefsetsIntEdQueryCH = "LOAD DATA LOCAL INFILE '"+ReleaseFilePathCH+"\\Full\\Refset\\Map\\der2_iisssccRefset_ExtendedMapFull_INT_"+ReleaseDateCH+".txt'\r\n"
//	            		+ "INTO TABLE `full_refset_Language`\r\n"
//	            		+ "LINES TERMINATED BY '\\r\\n'\r\n"
//	            		+ " IGNORE 1 LINES\r\n"
//	            		+ "(`id`,`effectiveTime`,`active`,`moduleId`,`refsetId`,`referencedComponentId`,`mapGroup`,`mapPriority`,`mapRule`,`mapAdvice`,`mapTarget`,`correlationId`,`mapCategoryId`);";
//	            statement.executeUpdate(importExtendedMapRefsetsIntEdQueryCH);

	            // Verbindung schließen
//	            statement.close();
//	            connection.close();

	            System.out.println("Datenbank und Datenimport erfolgreich abgeschlossen.");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}