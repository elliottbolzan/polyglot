package polyglot;

import java.util.ArrayList;
import java.util.List;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.TranslateRequestInitializer;
import com.google.api.services.translate.model.LanguagesResource;
import com.google.api.services.translate.model.TranslationsListResponse;

/**
 * 
 * This class interacts with Google's Cloud API directly.
 * 
 * Two different types of request are made: a translation request and a possible
 * languages request. These requests respectively live in translate(List<String>
 * phrases, String code) and List<String> languages().
 * 
 * TranslateAPI throws PolyglotExceptions, letting the caller determine how to
 * handle them.
 *
 * @author Elliott Bolzan
 */
public class TranslateAPI {

	private static final int MAX_CHARACTERS_PER_REQUEST = 600;
	private static final String APPLICATION_NAME = "VoogaSalad";
	private String APIKey;
	private Translate translate;

	/**
	 * Creates a TranslateAPI.
	 * 
	 * @param APIKey
	 *            the Google Cloud API key (necessary for interacting with the
	 *            API).
	 * @throws Exception
	 *             the exception thrown when initialization is somehow
	 *             interrupted.
	 */
	public TranslateAPI(String APIKey) throws Exception {
		this.APIKey = APIKey;
		setup();
	}

	/**
	 * Initializes the translate object, through which requests are made to the
	 * Google Cloud API.
	 * 
	 * @throws PolyglotException
	 *             thrown when initialization is somehow interrupted.
	 */
	private void setup() throws Exception {
		TranslateRequestInitializer KEY_INITIALIZER = new TranslateRequestInitializer(APIKey);
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		translate = new Translate.Builder(httpTransport, jsonFactory, null).setApplicationName(APPLICATION_NAME)
				.setTranslateRequestInitializer(KEY_INITIALIZER).build();
	}

	/**
	 * Translates phrases from any given language (auto-detected by Google's
	 * API) to a destination language, specific by the parameter code.
	 * 
	 * Breaks up the requests into chunks Google can process.
	 * 
	 * @param phrases
	 *            the phrases to be translated.
	 * @param code
	 *            the language code to translate into.
	 * @return a List<String> representing translated phrases.
	 * @throws Exception
	 *             thrown when translation fails.
	 */
	protected List<String> translate(List<String> phrases, String code) throws Exception {
		List<String> translations = new ArrayList<String>();
		List<String> toTranslate = new ArrayList<String>();
		int characters = 0;
		for (String string : phrases) {
			if (characters + string.length() >= MAX_CHARACTERS_PER_REQUEST) {
				updateTranslated(translations, toTranslate, code);
				characters = 0;
				toTranslate.clear();
			}
			characters += string.length();
			toTranslate.add(string);
		}
		updateTranslated(translations, toTranslate, code);
		return translations;
	}

	/**
	 * Where the actual translation takes place. A call to Google's Cloud API is
	 * made.
	 * 
	 * @param destination
	 *            the List<String> to add translations too.
	 * @param toTranslate
	 *            the List<String> to translate.
	 * @param code
	 *            the language identifier.
	 * @return an updated List<String> of translated values.
	 * @throws Exception
	 *             throws an error when the request fails.
	 */
	private List<String> updateTranslated(List<String> destination, List<String> toTranslate, String code)
			throws Exception {
		TranslationsListResponse result = translate.translations().list(toTranslate, code).execute();
		result.getTranslations().forEach((entry) -> destination.add(entry.getTranslatedText()));
		return destination;
	}

	/**
	 * Provides the caller with a list of languages that can be translated into.
	 * This list is obtained directly from Google's Cloud API.
	 * 
	 * @return a List<String> of possible languages to translate to.
	 * @throws Exception
	 *             thrown when languages cannot be retrieved.
	 */
	protected List<String> languages() throws Exception {
		List<String> codes = new ArrayList<String>();
		List<LanguagesResource> response = translate.languages().list().execute().getLanguages();
		response.forEach((resource) -> codes.add(resource.getLanguage()));
		return codes;
	}

}
