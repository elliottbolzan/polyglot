package polyglot.language;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import polyglot.Case;

/**
 * This class manages polyglot's state concerning languages.
 * 
 * It holds references to: a Map of languages that have been translated into;
 * the default Locale; the original Language supplied by the user; and a tool
 * for converting between language codes and their display names.
 * 
 * @author Elliott Bolzan
 *
 */
public class LanguageManager {

	private Map<Locale, Language> languages;
	private Locale defaultLocale;
	private ObjectProperty<Locale> locale;
	private Language originalLanguage;
	private LanguageCodes languageCodes;

	/**
	 * Create a LanguageManager.
	 * 
	 * A listener is added to the locale: whenever the locale is changed, bound
	 * Strings will automatically be updated to the new language.
	 * 
	 * @param propertiesPath
	 *            the path to the caller's default Properties file.
	 * @param defaultLocale
	 *            the caller's default Locale.
	 */
	public LanguageManager(String propertiesPath, Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
		languages = new HashMap<Locale, Language>();
		languageCodes = new LanguageCodes();
		locale = new SimpleObjectProperty<>();
		locale.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
		languageFromBundle(propertiesPath);
	}

	/**
	 * Creates a Language from the user-supplied ResourceBundle. This allows the
	 * LanguageManager to ignore ResourceBundles once and for all.
	 * 
	 * @param propertiesPath
	 *            the path to the caller's default ResourceBundle.
	 */
	private void languageFromBundle(String propertiesPath) {
		Map<String, String> vocabulary = new HashMap<String, String>();
		ResourceBundle resources = ResourceBundle.getBundle(propertiesPath);
		Enumeration<String> keys = resources.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			vocabulary.put(key, resources.getString(key));
		}
		originalLanguage = new Language(vocabulary);
		switchTo(defaultLocale, originalLanguage, false);
	}

	/**
	 * Switches the Locale to a new value, triggering a language change for
	 * values that are bound to polyglot.
	 * 
	 * @param newLocale
	 *            the Locale to switch to.
	 */
	public void switchTo(Locale newLocale, boolean threaded) {
		if (threaded) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					locale.set(newLocale);
				}
			});
		} else {
			locale.set(newLocale);
		}
	}

	/**
	 * Switches the Locale to a new value, triggering a language change for
	 * values that are bound to polyglot.
	 * 
	 * In addition, caches the new Language so it does not have to be translated
	 * again in the future, if the user chooses to switch back to it.
	 * 
	 * @param newLocale
	 * @param language
	 */
	public void switchTo(Locale newLocale, Language language, boolean threaded) {
		languages.put(newLocale, language);
		switchTo(newLocale, threaded);
	}

	/**
	 * Provides the caller with a localized version of the value for the key
	 * parameter.
	 * 
	 * @param key
	 *            the key from the Properties file.
	 * @param textCase
	 *            the case the text should be returned in.
	 * @return a String representing a localized version of the value for the
	 *         key.
	 */
	public String get(String key, Case textCase) {
		Language language = languages.get(locale.get());
		String value = language.get(key);
		return textCase.convert(value);
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
		return originalLanguage.get(key);
	}

	/**
	 * Creates a StringBinding that will be updated when the Locale is changed.
	 * Any user-facing text-property can bind to the returned value, and be
	 * updated when the language is changed.
	 * 
	 * @param key
	 *            the key to bind to.
	 * @param textCase
	 *            the case of the value to be returned.
	 * @return a StringBinding that updates as the language does.
	 */
	public StringBinding createStringBinding(String key, Case textCase) {
		return Bindings.createStringBinding(() -> get(key, textCase), locale);
	}

	/**
	 * @return a List<String> representing the keys of the user's default
	 *         Properties file.
	 */
	public List<String> keys() {
		return originalLanguage.keys();
	}

	/**
	 * @return a List<String> representing the values of the user's default
	 *         Properties file. These are the values that are to be translated.
	 */
	public List<String> toTranslate() {
		return originalLanguage.values();
	}

	/**
	 * @param locale
	 *            the specified Locale.
	 * @return whether polyglot has already translated into that Locale or not.
	 */
	public boolean hasTranslated(Locale locale) {
		return languages.containsKey(locale);
	}

	/**
	 * Returns a language code for a given language display name.
	 * 
	 * @param language
	 *            the String representing a given language's display name.
	 * @return the language's code in Google's Translate API.
	 */
	public String codeForLanguage(String language) {
		return languageCodes.codeForLanguage(language);
	}

	/**
	 * Returns language display names from language codes.
	 * 
	 * @param codes
	 *            the codes for which to find language names.
	 * @return a List<String> of language display names.
	 */
	public List<String> languagesFromCodes(List<String> codes) {
		return languageCodes.languagesFromCodes(codes);
	}

}
