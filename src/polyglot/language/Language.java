package polyglot.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * This class is a wrapper for one language's vocabulary.
 * 
 * As convenience, it provides get(String key), List<String> keys(), and
 * List<String> values() methods.
 * 
 * This design is necessary to remove clutter from LanguageManager. If it were
 * not used, LanguageManager would need to contain a Map of Maps, which would
 * not be very legible or conceptually clear.
 * 
 * @author Elliott Bolzan
 *
 */
public class Language {

	private Map<String, String> vocabulary;

	/**
	 * Creates a Language.
	 * 
	 * @param vocabulary
	 *            the key/value pairs that are part of this language's
	 *            vocabulary.
	 */
	public Language(Map<String, String> vocabulary) {
		this.vocabulary = vocabulary;
	}

	/**
	 * Get a value from a key.
	 * 
	 * @param key
	 *            the key to get the value for.
	 * @return a String representing the value for the specified key.
	 */
	protected String get(String key) {
		return vocabulary.get(key);
	}

	/**
	 * Returns the language's keys (these should be the same for all languages).
	 * 
	 * @return a List<String> of the language's keys.
	 */
	protected List<String> keys() {
		return new ArrayList<String>(vocabulary.keySet());
	}

	/**
	 * Returns the language's values (these should differ between languages).
	 * 
	 * @return a List<String> of the language's values.
	 */
	protected List<String> values() {
		return new ArrayList<String>(vocabulary.values());
	}

}
