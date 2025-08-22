package translation.check;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegexValidator {
	private static final Pattern UPPERCASE_PATTERN = Pattern.compile("^\\p{Lu}");
	private static final Pattern EMPTY_STRING_PATTERN = Pattern.compile("^\\s*$");              // nur Leerzeichen oder leer
	private static final Pattern SPACE_AROUND_SLASH_PATTERN = Pattern.compile("\\s*/|/\\s+");   // z. B. " /" oder "/ "

	
    public static List<String> validateTerm(String languageCode, String term) {
        List<String> results = new ArrayList<>();
        
        //General checks for all languages
        boolean isEmpty = term == null || EMPTY_STRING_PATTERN.matcher(term).matches();
        boolean hasSpaceAround = term != null && SPACE_AROUND_SLASH_PATTERN.matcher(term).find();
        
        results.add(isEmpty ? "ja" : "nein");
        results.add(hasSpaceAround ? "ja" : "nein");
        
        if (languageCode.equals("de")) {
			//Specific checks for German language
        	boolean startsUpper = term != null && UPPERCASE_PATTERN.matcher(term).find();
        	results.add(startsUpper ? "ja" : "nein");
		} else if (languageCode == "fr") {
			results.add("nein"); // French does not require uppercase at the start
			
		} else if (languageCode == "it") {
			results.add("nein"); // Italian does not require uppercase at the start
		}  else {
			results.add("language code not recognized and therefore not checked"); // Default case for other languages
		}
        return results;
    }
}
