package polyglot;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringEscapeUtils;

import javafx.beans.binding.StringBinding;
import polyglot.language.Language;
import polyglot.language.LanguageManager;

/**
 * 
 * The highest-level class in the polyglot utility.
 * 
 * The class holds references to translateAPI, which processes requests to
 * Google's Cloud API, and to manager, which caches the languages that have been
 * translated into.
 * 
 * Polyglot should be called with an APIKey for Google's Cloud API, and a path
 * to a ResourceBundle. The latter is done for maximal flexibility and easier
 * integration: the user can keep using a Properties file, and does not have to
 * know what takes place behind the scenes.
 * 
 * In reality, the data from the ResourceBundle is cached, as are subsequent
 * translations.
 * 
 * @author Elliott Bolzan
 *
 */
public class Polyglot {

	private static final String PATH_TO_EXCEPTION_MESSAGES = "polyglot/resources/PolyglotException";

	private TranslateAPI translateAPI;
	private LanguageManager manager;
	private ResourceBundle resources;

	/**
	 * Creates a Polyglot object.
	 * 
	 * @param APIKey
	 *            the Google Cloud API key.
	 * @param pathToResourceBundle
	 *            a path to a ResourceBundle.
	 * @param locale
	 *            the user's default Locale.
	 * @throws PolyglotException
	 *             an exception thrown if the provided parameters are incorrect.
	 */
	public Polyglot(String APIKey, String pathToResourceBundle, Locale locale) throws PolyglotException {
		resources = ResourceBundle.getBundle(PATH_TO_EXCEPTION_MESSAGES);
		try {
			translateAPI = new TranslateAPI(APIKey);
		} catch (Exception e) {
			throw new PolyglotException(resources.getString("SetupFailure"), e);
		}
		manager = new LanguageManager(pathToResourceBundle, locale);
	}

	/**
	 * Creates a Polyglot object.
	 * 
	 * @param APIKey
	 *            the Google Cloud API key.
	 * @param pathToResourceBundle
	 *            a path to a ResourceBundle.
	 * @throws PolyglotException
	 *             an exception thrown if the provided parameters are incorrect.
	 */
	public Polyglot(String APIKey, String pathToResourceBundle) throws PolyglotException {
		this(APIKey, pathToResourceBundle, new Locale("en"));
	}

	/**
	 * Set the language to a new language.
	 * 
	 * Note: the language parameter must be included in the List<String>
	 * returned by languages(). Naturally, Polyglot can only translate into the
	 * languages supported by Google's API, and they must be spelled correctly.
	 * 
	 * @param language
	 *            the language to translate into.
	 * @throws PolyglotException
	 *             throws an Exception, specifically when the provided language
	 *             is incorrect.
	 */
	public void setLanguage(String language) throws PolyglotException {
		try {
			String code = manager.codeForLanguage(language);
			if (!manager.hasTranslated((new Locale(code)))) {
				List<String> original = manager.toTranslate();
				List<String> translated = translateAPI.translate(original, code);
				save(translated, code);
			} else {
				manager.switchTo(new Locale(code), true);
			}
		} catch (Exception e) {
			throw new PolyglotException(String.format(resources.getString("LanguageChangeFailure"), language), e);
		}
	}

	/**
	 * Called when a new translation has been made. Caches the translation using
	 * the LanguageManager.
	 * 
	 * @param phrases
	 *            the translated phrases.
	 * @param code
	 *            the code representing the language these phrases are in.
	 */
	private void save(List<String> phrases, String code) {
		Map<String, String> vocabulary = new HashMap<String, String>();
		List<String> keys = manager.keys();
		for (int i = 0; i < keys.size(); i++) {
			vocabulary.put(keys.get(i), StringEscapeUtils.unescapeHtml4(phrases.get(i)));
		}
		manager.switchTo(new Locale(code), new Language(vocabulary), true);
	}

	/**
	 * Returns a list of available languages in Google's Translate API. The
	 * actual implementation is delegated to the LanguageManager.
	 * 
	 * @return a List<String> of languages that can be translated into.
	 * @throws PolyglotException
	 *             thrown when languages cannot be retrieved properly.
	 */
	public List<String> languages() throws PolyglotException {
		try {
			return manager.languagesFromCodes(translateAPI.languages());
		} catch (Exception e) {
			throw new PolyglotException(resources.getString("LanguageRetrievalFailure"), e);
		}
	}

	/**
	 * Returns a StringBinding for a Properties file key. Designed to simulate
	 * the ResourceBundle experience using the get(String key) signature.
	 * 
	 * This method calls get(String key, Case textCase) with an UNCHANGED Case,
	 * meaning that the text provided by Google's API will be unprocessed in
	 * terms of capitalization.
	 * 
	 * @param key
	 *            the key corresponding to the entry in the Properties file.
	 * @return a StringBinding that updates as the language is modified.
	 */
	public StringBinding get(String key) {
		return get(key, Case.UNCHANGED);
	}

	/**
	 * Returns a StringBinding for a Properties file key. Designed to simulate
	 * the ResourceBundle experience using the get(String key) signature.
	 * 
	 * A Case must be provided: the value returned can this way be set to use
	 * upper case, lower case, or title case, depending on the caller's needs.
	 * 
	 * @param key
	 *            the key corresponding to the entry in the Properties file.
	 * @param textCase
	 *            the case that values will be converted to.
	 * @return a StringBinding that updates as the language is modified.
	 */
	public StringBinding get(String key, Case textCase) {
		return manager.createStringBinding(key, textCase);
	}

	/**
	 * Provides the caller with the original version of the value for the key
	 * parameter.
	 * 
	 * @param key
	 *            the key from the Properties file.
	 * @return a String representing the original version of the value for the
	 *         key.
	 */
	public String getOriginal(String key) {
		return manager.getOriginal(key);
	}

}