1. prerequisites
   1. MariaDB
2. dependencies (may have to be configured per project in vscode)
   1. https://mariadb.com/downloads/connectors/connectors-data-access/java8-connector
   2. https://sourceforge.net/projects/opencsv/
   3. https://commons.apache.org/proper/commons-lang/download_lang.cgi
3. SNOMED Release Files (extract both)
   1. Austrian Release File
   2. Corresponding International Release File
4. TermSpace export
   1. sheet "Description_Additions": save as file ending with "Additions.tsv"
   1. sheet "Description_Inactivations": save as file ending with "Inactivations.tsv"
5. SNOMED_DB
   1. set
      1. ReleaseFilePath
      2. ReleaseFilePathCH (corresponds to AT release)
      3. ReleaseDate
      4. ReleaseDateCH (corresponds to AT release)
      5. dbPassword
      6. classpathentry (path for kind="lib")
   2. run
6. SNOMEDTranslationCheck
   1. set paths to dependencies in .vscode\settings.json
   2. DB_connection.java
      1. set PASSWORD
   3. Main.java
      1. Analyse Additions (run)
         1. set CSVFilePath to full path of "Additions.tsv"
         2. set destination to full path of output directory -> will later contain "Delta.tsv"
         3. uncomment "compare.generateDeltaDescAdditions(destination)"
      2. Analyse Inactivations (run)
         1. set CSVFilePath to full path of "Inactivations.tsv"
         2. set destination to full path of output directory -> will later contain:
            1. FoundDescriptionIDs.tsv - descriptionIDs which are part of the Austrian Edition and can be inactivated
            2. NotFoundDescriptionIDs.tsv - descriptionIDs which are NOT part of the Austrian Edition. These descriptions should still be inactivated, probably by using their corresponding term (see termspace export to extract corresponding terms)
         3. uncomment "compare.generateDeltaDescInactivation(destination);"
