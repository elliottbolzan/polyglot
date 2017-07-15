polyglot
========
A self-localizing `ResourceBundle` that makes use of Google's Cloud API and integrates into JavaFX.

Provide one `.properties` file (presumably in English, but `polyglot` has language-detection enabled) and allow your application to be translated into around 100 languages at runtime. With very little to no extra work! 

Check out `ExampleForm.java` for more instructions and tips. If you're interested in using `polyglot` in your application, get your own Google Cloud API key at [https://cloud.google.com](). It's free for one year!

Written by Elliott Bolzan for Duke's CS 308 (Design and Implementation).

## Use

Using `polyglot` is simple, and ideally, the experience should be very similar to that of using a `ResourceBundle`.

1. Instantiate a `polyglot` object with a Google Cloud API Key and a path to a traditional `.properties` file.
2. Associate keys from that `.properties` file to your user-facing text: `label.textProperty().bind(polyglot.get("TitleKey"));`.
3. To change the language, call `polyglot.setLanguage("Language");`. Your user-facing text is automatically updated.

**Notes:** 

1. Obviously, `polyglot` can only translate into languages supported by Google's Cloud API. A list of such languages is available using the `languages()` command.
2. We recommend that your `.properties` file only contain user-facing `String` elements. Paths and numeric values probably won't be translated accurately. Additionally, formatted `String` elements (`%s`) probably run a risk in translation.

## Advantages

Here are some reasons why you should use `polyglot` in your application:

1. **Instant internationalization.** You might be able to localize your project into 5 or 6 languages, based on the origins of the members of your group. That takes time and effort, however. `polyglot` will give you around 100 languages with very little to no work.
2. **Caching.** Your user switches the language to Spanish, then to French, and then, back to Spanish. To make the latter switch happen faster (and to prevent unecessary Internet calls), `polyglot` caches translation results. 
3. **Case management.** Google Translate, in certain languages specifically, can be notoriously bad at returning text in the case it was provided. `polyglot` allows you to return text in four different cases: `UNCHANGED` (Google's case and the default for `polyglot`), `UPPER` (upper case), `LOWER` (lower case), and `TITLE` (great for buttons, titles, and many other user interface items).
4. **Ease of use.** `polyglot` lets you retain your old structure: you can keep using a `.properties` file in English and the workflow associated with it. We take care of the rest!