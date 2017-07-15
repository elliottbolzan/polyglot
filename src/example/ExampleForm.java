package example;

import java.util.Locale;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import polyglot.Case;
import polyglot.Polyglot;
import polyglot.PolyglotException;

/**
 * @author Elliott Bolzan
 * 
 *         This class serves as an example for the polyglot utility.
 * 
 *         It displays a rudimentary form in a JavaFX stage, comprised of a
 *         Label, a ComboBox, three TextFields, and a Button. Originally, all
 *         the text values are in English (loaded from Example.properties). When
 *         the user selects a new language using the ComboBox, however, the
 *         user-facing text is automatically translated to that language.
 * 
 *         In essence, this class shows how polyglot functions as an
 *         automatically-internationalized ResourceBundle.
 * 
 *         You will notice that there is a KEY static variable. You can
 *         obtain your own for free here: https://cloud.google.com.
 * 
 */
public class ExampleForm extends Application {

	private static final String KEY = ""; // Insert your own Google Cloud API key.
	private static final String RESOURCES_PATH = "example/resources/Example";

	private Polyglot polyglot;
	private Stage stage;
	private BorderPane root;
	private ComboBox<String> languagePicker;

	/**
	 * Begins the launching process.
	 * 
	 * @param args
	 *            the arguments passed into main.
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Launches the user interface.
	 * 
	 * @param primaryStage
	 *            the application's primary stage / window.
	 */
	@Override
	public void start(Stage primaryStage) throws PolyglotException {
		this.stage = primaryStage;
		this.polyglot = new Polyglot(KEY, RESOURCES_PATH);
		setupView();
		setupStage();
	}

	/**
	 * Creates a stage. Notice how the stage's title is bound to polyglot: it
	 * will be updated on any language selection.
	 */
	private void setupStage() {
		stage.titleProperty().bind(polyglot.get("ExampleForm", Case.TITLE));
		stage.setOnCloseRequest(e -> System.exit(0));
		stage.setScene(new Scene(root, 220, 300));
		stage.setResizable(false);
		stage.show();
	}

	/**
	 * Creates the primary view for this example, a BorderPane.
	 */
	private void setupView() {

		root = new BorderPane();
		root.setPadding(new Insets(20));

		createLanguagePicker();
		Label instructions = createLabel("Language");
		TextField nameField = createField("Name");
		TextField phoneField = createField("Phone");
		TextField ageField = createField("Age");
		Button save = createButton("SaveButton");

		VBox container = new VBox(10);
		container.getChildren().addAll(instructions, languagePicker, nameField, phoneField, ageField, save);

		root.setCenter(container);

	}

	/**
	 * A helper method for creating language-updating Labels.
	 * 
	 * @param property
	 *            the desired property key.
	 * @return a Label.
	 */
	private Label createLabel(String property) {
		Label label = new Label();
		label.textProperty().bind(polyglot.get(property));
		label.setWrapText(true);
		return label;
	}

	/**
	 * A helper method for creating a language-updating TextField.
	 * 
	 * @param property
	 *            the desired property key.
	 * @return a TextField.
	 */
	private TextField createField(String property) {
		TextField field = new TextField();
		field.promptTextProperty().bind(polyglot.get(property, Case.TITLE));
		return field;
	}

	/**
	 * A helper method for creating a language-updating Button.
	 * 
	 * @param property
	 *            the desired property key.
	 * @return a Button.
	 */
	private Button createButton(String property) {
		Button button = new Button();
		button.textProperty().bind(polyglot.get(property, Case.TITLE));
		return button;
	}

	/**
	 * Initializes the language-selecting ComboBox. The ComboBox's default value
	 * is set to the language for the user's default Locale.
	 */
	private void createLanguagePicker() {
		languagePicker = new ComboBox<String>();
		try {
			languagePicker.setItems(FXCollections.observableArrayList(polyglot.languages()));
		} catch (PolyglotException e) {
			System.out.println(e.getMessage());
		}
		languagePicker.setValue(Locale.getDefault().getDisplayLanguage());
		languagePicker.setOnAction(e -> languageChanged());
	}

	/**
	 * Called when the ComboBox has a new selection. Tells polyglot to update
	 * the language.
	 */
	private void languageChanged() {
		try {
			polyglot.setLanguage(languagePicker.getValue());
		} catch (PolyglotException e) {
			System.out.println(e.getMessage());
		}
	}

}
