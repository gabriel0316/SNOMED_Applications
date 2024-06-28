package translation.check;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class creates the files for new, old, idnetical Translations. It can also create a overview with the existing translation in the SNOMED DB. 
 */
public class Compare {
	
	//TODO in Counfluence beschreiben warum eine Liste gemacht mit welches Arraylisten enthält.
	public static List <List<String>> newTranslation = new ArrayList<List<String>>();
	public static List <List<String>> oldTranslation = new ArrayList<List<String>>();
	public static List <List<String>> identicalTranslation = new ArrayList<List<String>>();
	public static List <List<String>> noTranslation = new ArrayList<List<String>>();
	public static List <List<String>> newSynonym = new ArrayList<List<String>>();
	public static List <List<String>> checkSynonym = new ArrayList<List<String>>();
	public static List <List<String>> newPreferredTerm = new ArrayList<List<String>>();
	public static String language;	
	public static List <List<String>> DescriptionChanges = new ArrayList<List<String>>();
	public static List <List<String>> DecriptionAddition = new ArrayList<List<String>>();
	public static List <List<String>> JIRAtask = new ArrayList<List<String>>();
	public static List <List<String>> translationOverview = new ArrayList<List<String>>();
			
	/**
	 * Sets the information to a concept
	 * @param conceptId ID of the concept
	 * @param fsn FSN of the concept
	 * @param term Translated term
	 * @param language_Code ISO code for the language e.g. "de"
	 * @param case_Significance Case significance of the translated term
	 * @param language_reference_set Reference set the translated term belongs to
	 * @param acceptabilityId Acceptability of the translated term
	 */
	
	public void setNewTranslations (String conceptId, String fsn, String pt, String term, String language_Code, String case_Significance, String language_reference_set, String acceptabilityId){
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
	
	//For method overloading. Allowing to just read the SCT ID form a file.
	public void setNewTranslations (String conceptId){
		List<String> L = new ArrayList<String>();
		L.add(conceptId);
		Compare.newTranslation.add(L);
	}
	
	public void setOldTranslation (String conceptId, String term, String translationId, String acceptabilityId){
		List<String> L = new ArrayList<String>();
		L.add(conceptId);
		L.add(term);
		L.add(translationId);
		L.add(acceptabilityId);
		Compare.oldTranslation.add(L);
	}
	
	//Used to create overview file with content from the DB
	public void setTranslationOverview (String conceptId, String term, String typeId, String languageCode) {
		List<String> L = new ArrayList<String>();
		L.add(conceptId);
		L.add(term);
		L.add(typeId);
		L.add(languageCode);
		Compare.translationOverview.add(L);
	}
	
	/**
	 * This function compares the both lists. It checks if there are duplicates (same in the CSV as in the SNOMED database), if the concept has already an translation (add as a synonym or new translation) 
	 * @param path where to create the three text files.
	 * @throws IOException
	 */
	public static void compare (String path) throws IOException{
		List<String> header = new ArrayList<String>();
		List<String> ChangeEntry= new ArrayList<String>();
		List<String> JIRAtodo = new ArrayList<String>();
		
		header.add("Description ID");
		header.add("Preferred Term (For reference only)");
		header.add("Term (For reference only)");
		header.add("Inactivation Reason");
		DescriptionChanges.add(new ArrayList<String>(header));
		header.clear();
		
		header.add("Concept ID");
		header.add("GB/US FSN Term (For reference only)");
		header.add("Preferred Term (For reference only)");
		header.add("Translated Term");
		header.add("Language Code");
		header.add("Case significance");
		header.add("Type");
		header.add("Language reference set");
		header.add("Acceptability");
		header.add("Language reference set");
		header.add("Acceptability");
		header.add("Language reference set");
		header.add("Acceptability");
		header.add("Notes");
		DecriptionAddition.add(new ArrayList<String>(header));
		header.clear();
		
		
		
		header.add("|SCT ID|");
		header.add("|FSN|");
		header.add("|TODO|");
		JIRAtask.add(new ArrayList<String>(header));
		header.clear();
		boolean NoDuplicate = true;

		for (List <String> compare : newTranslation){
			boolean newTranslation=true;
		
				for (List <String> compareTO : oldTranslation){
					
					//checks if the translation in the CSV is identical with the one in the SNOMED DB
					if (compareTO.get(0).equals(compare.get(0)) && compareTO.get(1).equals(compare.get(3)) && compareTO.get(3).equals(compare.get(8))) {
							newTranslation=false;
							identicalTranslation.add(compare);
					} else{				
							//searches the conceptID and adds the the term in the newTranslation-List. If no matching conceptID is found, it is a new translation.
							if (compare.get(0).equals(compareTO.get(0))){
								for(List <String> SCTID : JIRAtask){								
									if (SCTID.get(0).equals("|"+compare.get(0)+"|")){								
										NoDuplicate = false;
										if (!NoDuplicate){
											System.out.println(SCTID.get(0)+"=="+"|"+compare.get(0)+"|"+": Duplikat = false");
										}
										}
								}
							if (NoDuplicate){
//								JIRAtodo.add("|");
//								JIRAtask.add(new ArrayList<String>(JIRAtodo));
//								JIRAtodo.clear();
								JIRAtodo.add("|"+compare.get(0)+"|"); //Adds SCT ID
								JIRAtodo.add(compare.get(1)+"|"); //Adds
							}
							if(compare.get(8).equals("900000000000548007")){
								newPreferredTerm.add(compare);
								ChangeEntry.add(compareTO.get(2));
								ChangeEntry.add("PT");
								ChangeEntry.add("Term");
								ChangeEntry.add("Non-conformance to editorial policy");
								DescriptionChanges.add(new ArrayList<String>(ChangeEntry));
								JIRAtodo.add("* PT "+compareTO.get(1)+" inaktivieren \n");
								JIRAtodo.add("* Neuer PT "+compare.get(3));
							}else{
								newSynonym.add(compare);
								checkSynonym.add(compareTO);
								DecriptionAddition.add(compare);
								JIRAtodo.add("\n * Neues SYN "+compare.get(3));
								}
							JIRAtodo.add("|");
							JIRAtask.add(new ArrayList<String>(JIRAtodo));
							NoDuplicate=true;                                                                                                                                                                                                                                                                                                            
							ChangeEntry.clear();
							JIRAtodo.clear();
					}
						//NoDuplicate=true;
						newTranslation=false;
				}
			}
			
			//if no matching translation is found the new term will be added in the noTranslation-List.
			if(newTranslation){
				noTranslation.add(compare);
			}
		}

		//Creates the identicalTranslation file.
		File identical2 = new File(path+"\\identicalTranslation.txt");
		identical2.createNewFile();			
		FileWriter writer = new FileWriter(identical2);
		
		for (List<String> str : removeDuplicates(identicalTranslation)) {
			writer.write(str.get(0)+"	"+str.get(2) + System.lineSeparator());
		}
		writer.close();
		
		//Creates the noTranslation file.
		File noTranslation2 = new File(path+"\\noTranslations.txt");
		noTranslation2.createNewFile();		
		FileWriter writer2 = new FileWriter(noTranslation2);
		
		for (List<String> str : removeDuplicates(noTranslation)) {
			writer2.write(str.get(0)+"	"+str.get(2) + System.lineSeparator());
		}
		writer2.close();
		
		//Creates the newSynonym file.
		File newSynonym2 = new File(path+"\\newSynonym.txt");
		newSynonym2.createNewFile();			
		FileWriter writer3 = new FileWriter(newSynonym2);
		
		for (List<String> str : removeDuplicates(newSynonym)) {
			writer3.write(str.get(0)+"	"+str.get(2) + System.lineSeparator());
		}
		writer3.close();
		
		//Creates the CheckSynoynm file.
		File checkSynonym2 = new File(path+"\\CheckSynonym.txt");
		checkSynonym2.createNewFile();			
		FileWriter writer4 = new FileWriter(checkSynonym2);
		
		for (List<String> str : removeDuplicates(checkSynonym)) {
			writer4.write(str.get(0)+"	"+str.get(2) + System.lineSeparator());
		}
		writer4.close();
		
		//Creates the newPreferredTerm file.
		File newPreferredTerm2 = new File(path+"\\newPreferredTerm.txt");
		newPreferredTerm2.createNewFile();			
		FileWriter writer5 = new FileWriter(newPreferredTerm2);
		
		for (List<String> str : removeDuplicates(newPreferredTerm)) {
			writer5.write(str.get(0)+"	"+str.get(2) + System.lineSeparator());
		}
		writer5.close();
		
		//Creates the newPreferredTerm file.
		File descriptionInaktivation = new File(path+"\\descriptionInactivations.tsv");
		descriptionInaktivation.createNewFile();			
		FileWriter writer6 = new FileWriter(descriptionInaktivation);
				
		for (List<String> str : removeDuplicates(DescriptionChanges)) {
			writer6.write(str.get(0)+"	"+str.get(1)+"	"+str.get(2)+"	"+str.get(3) + System.lineSeparator());
		}
		writer6.close();
		
		//Creates the descriptionAddition file.
		File descriptionAddition = new File(path+"\\descriptionAddition.tsv");
		descriptionAddition.createNewFile();			
		FileWriter writer7 = new FileWriter(descriptionAddition);
				
		for (List<String> str : removeDuplicates(DecriptionAddition)) {
			writer7.write(str.get(0)+"	"+str.get(1)+"	"+str.get(2)+"	"+str.get(3) + "	"+str.get(4)+ "	"+str.get(5)+"	"+str.get(6)+"	"+str.get(7)+"	"+str.get(8)+System.lineSeparator());
		}
		writer7.close();
		
		//Creates the JIRAtask file.
				File JIRAtask2 = new File(path+"\\JIRAtask.tsv");
				JIRAtask2.createNewFile();			
				FileWriter writer8 = new FileWriter(JIRAtask2);
				for (List<String> str : removeDuplicates(JIRAtask)) {
					writer8.write(Arrays.toString(str.toArray())+ System.lineSeparator());
				}
				writer8.close();
	}
	
	/**
	 * This function removes duplicates from a given list.
	 * @param list List which has duplicates.
	 * @return Returns a cleaned list.
	 */
	public static List <List<String>> removeDuplicates(List <List<String>> list) {
        // Create a new ArrayList
		List <List<String>> newList = new ArrayList<List<String>>();
		Set<List<String>> set = new HashSet<>();
		    for (List<String> i : list) {
		        if (!set.contains(i)) {
		        	 set.add(i);
			         newList.add(i);
		        }
		    }
        return newList;
    }

	/**
	 * This function creates a overview with the existing translations in the SNOMED DB. 
	 * @param destination Destination path where to create the file.
	 * @throws IOException
	 */
	public static void createTranslationsOverview(String destination) throws IOException {
		List<String> header = new ArrayList<String>();
		List <List<String>> structuredFile = new ArrayList<List<String>> ();
		header.add("Concept ID");
		header.add("GB/US FSN Term (For reference only)");
		header.add("Preferred Term (For reference only)");
		header.add("Translated Term DE");
		header.add("Translated Term FR");
		header.add("Translated Term IT");
		structuredFile.add(new ArrayList<String>(header));
		header.clear();
		
		String conceptIDs = null;
		
		//removes duplicates from the given CSV. This reduces the times the SQL-query needs to be executed.
		List <List<String>> newTranslationCleaned = removeDuplicates(newTranslation);
//		newTranslationCleaned.remove(0);
		
		
		long start = System.currentTimeMillis();    
		for (List <String> conceptOverview : newTranslationCleaned){
			if (conceptIDs == null) {
				conceptIDs = conceptOverview.get(0);
			}else {
			conceptIDs = conceptIDs + ", " + conceptOverview.get(0);
			}
		}
		
		//connects to the DB and retrieves the conceptId, typeId, term, languageCode for each concept of the CSV file.
		try {
			DB_conncetion.connectDB();
			DB_conncetion.getOverviewOfTranslationsDB(conceptIDs);						
			DB_conncetion.closeConnectionDB();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		long elapsedTime = System.currentTimeMillis() - start;
		System.out.println("Query erstellt, druchgeführt und array befüllt. Dauer: "+elapsedTime);
		Main.totalTime = Main.totalTime + elapsedTime;
		
		//Structures the file to the desired schema
		start = System.currentTimeMillis();
		for (List<String> str : translationOverview) {
			boolean containsNoTransltion = true; 
			
			for(List<String> check : structuredFile) {
				//checks if the FSN are Identical
				if(str.get(0).equals(check.get(0))) {
					containsNoTransltion = false;
					
					//TODO Fügt bei der IT Spalte machmal den DE Begriff ein. Muss korrigiert werden.
					//If the SCT-Id is identical it checks if the term is a FSN
					if(str.get(2).equalsIgnoreCase("900000000000003001")) {						
						check.add(1, str.get(1));
					}else {
						//If the term is not an FSN it checks the languageCode and places the term at desired position
						if(str.get(3).equalsIgnoreCase("en")) {
							check.add(2, str.get(1));
						}else if(str.get(3).equalsIgnoreCase("de")) {
							check.add(3, str.get(1));
						}else if(str.get(3).equalsIgnoreCase("fr")){
							check.add(4, str.get(1));
						} else if (str.get(3).equalsIgnoreCase("it")){
							check.add(5, str.get(1));
						}
					}	
				}
			}
				
			//If the previous loop is done and no existing SCT ID is found in the structuredFile, it will be added.
			if (containsNoTransltion) {
					List<String> L = new ArrayList<String>();
					L.add(0, str.get(0));
					L.add("TODO");
					L.add("TODO");
					L.add("TODO");
					L.add("TODO");
					L.add("TODO");
					if(str.get(2).equalsIgnoreCase("900000000000003001")) {						
						L.add(1, str.get(1));
					}else {
						//If the term is not an FSN it checks the languageCode and places the term at desired position
						if(str.get(3).equalsIgnoreCase("en")) {						
							L.add(2, str.get(1));
						}else if(str.get(3).equalsIgnoreCase("de")) {
							L.add(3, str.get(1));
						}else if(str.get(3).equalsIgnoreCase("fr")){
							L.add(4, str.get(1));
						} else if (str.get(3).equalsIgnoreCase("it")){
							L.add(5, str.get(1));
						}
					}
					structuredFile.add(L);
				}
			}
		elapsedTime = System.currentTimeMillis() - start;
		Main.totalTime = Main.totalTime + elapsedTime;
		System.out.println("File nach gewünschter Struktur strukturiert. Dauer: "+elapsedTime);
		
		//Creates the TranslationOverview file.
		File translationOverviewFile = new File(destination+"\\TranslationOverview.tsv");
		translationOverviewFile.createNewFile();			
		FileWriter writer = new FileWriter(translationOverviewFile);
		System.out.println("File in erstellt und starte mit Befüllung.");
		start = System.currentTimeMillis();
		for (List<String> str : structuredFile) {
			writer.write(str.get(0)+"	"+str.get(1)+"	"+str.get(2) +"	"+str.get(3)+"	"+str.get(4)+"	"+str.get(5) + System.lineSeparator());
			}
		writer.close();
		elapsedTime = System.currentTimeMillis() - start;
		System.out.println("File erstellt. Dauer: "+elapsedTime);
		Main.totalTime = Main.totalTime + elapsedTime;
	}
}