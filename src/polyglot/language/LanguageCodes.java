package polyglot.language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 
 * This class provides a two-way mapping between Google's language codes and
 * these language's display names. Because this functionality is used several
 * times in polyglot, it has been abstracted into its own class.
 * 
 * The data is originally loaded from a Properties file. Then, a reverse index
 * is created, to simulate a two-way Map. This is possible, because there is a
 * bijection between the language codes and the language display names.
 * 
 * @author Elliott Bolzan
 *
 */
public class LanguageCodes {

	private static final String LANGUAGE_CODES_PATH = "polyglot/resources/LanguageCodes";
	private ResourceBundle resources;
	private Map<String, String> reverseIndex;

	/**
	 * Creates an instance of LanguageCodes. Loads the ResourceBundle.
	 */
	public LanguageCodes() {
		resources = ResourceBundle.getBundle(LANGUAGE_CODES_PATH);
		createReverseIndex();
	}

	/**
	 * Creates a reverse index from the data in the ResourceBundle. Allows for
	 * value/key lookup, in other terms.
	 */
	private void createReverseIndex() {
		reverseIndex = new HashMap<>();
		Enumeration<String> keys = resources.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			reverseIndex.put(resources.getString(key), key);
		}
	}

	/**
	 * Returns a language code for a given language display name.
	 * 
	 * @param language
	 *            the String representing a given language's display name.
	 * @return the language's code in Google's Translate API.
	 */
	protected String codeForLanguage(String language) {
		return reverseIndex.get(language);
	}

	/**
	 * Returns language display names from language codes.
	 * 
	 * @param codes
	 *            the codes for which to find language names.
	 * @return a List<String> of language display names.
	 */
	protected List<String> languagesFromCodes(List<String> codes) {
		List<String> languages = new ArrayList<>();
		for (String code : codes) {
			if (resources.containsKey(code)) {
				languages.add(resources.getString(code));
			}
		}
		Collections.sort(languages);
		return languages;
	}

}
