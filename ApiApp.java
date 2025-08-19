package cs1302.api;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;
import com.google.gson.Gson;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import com.google.gson.Gson;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;




/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {
    Stage stage;
    Scene scene;
    VBox root;
    TextField searchBar;

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox();
    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Label appNameLabel = new Label("UGA Dictionary/Library");
        appNameLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Button searchButton = new Button("Search");
        TextArea wordDetailsArea = new TextArea();
        wordDetailsArea.setEditable(false);
        wordDetailsArea.setPrefSize(600, 720);
        searchBar = new TextField();
        searchBar.setPrefWidth(520);
        searchButton.setOnAction(event -> {
            String word = searchBar.getText().trim();
            if (!word.isEmpty()) {
                WordAndDefinition wordAndDefinition = fetchWordDetails(word, wordDetailsArea);
                String definition = wordAndDefinition.getDefinition();
            } else {
                wordDetailsArea.setText("please enter a word to search");
            }
        });
        HBox topBar = new HBox();
        topBar.getChildren().addAll(searchBar, searchButton);
        VBox contentBox = new VBox();
        contentBox.setPrefSize(600,620);
        contentBox.getChildren().add(wordDetailsArea);
        Label messageLabel = new Label("America's most trusted Dictionary. Welcome! ");
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
        Button termsButton = new Button("Terms and Conditions");
        termsButton.setOnAction(event ->  displayTermsAndConditions());
        HBox bottomBar = new HBox();
        bottomBar.getChildren().addAll(messageLabel, termsButton);
        // Button getImagesButton = new Button("Get images");
        // contentBox.getChildren().add(getImagesButton);
        Button searchBooksButton = new Button("Search books");
        searchBooksButton.setOnAction(event -> searchBooks());
        contentBox.getChildren().add(searchBooksButton);
        root.getChildren().addAll(appNameLabel, topBar, contentBox, bottomBar);
        scene = new Scene(root);
        stage.setTitle("ApiApp!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();
    } // start

    public static void main(String [] args) {
        launch(args);
    }

    /**This method fetches word details from the first api.
     *and also receivees images from the second api.
     *
     *@return definitions
     *@param word
     *@param wordDetailsArea
     */
    private WordAndDefinition fetchWordDetails(String word, TextArea wordDetailsArea) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.dictionaryapi.dev/api/v2/entries/en/" + word))
                .GET()
                .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new Gson();
            WordDefinition[] definitions = gson.fromJson(response.body(), WordDefinition[].class);
            if (definitions != null && definitions.length > 0) {
                String definition = "Word: " + definitions[0].word + "\n";

                for (Meaning meaning : definitions[0].meanings) {
                    definition += "Part of Speech: " + meaning.partOfSpeech + "\n";
                    for (Definition def : meaning.definitions) {
                        definition += "- " + def.definition + "\n";
                    }
                }

                wordDetailsArea.setText(definition);

                //VBox contentBox = (VBox) wordDetailsArea.getParent();
                //contentBox.getChildren().removeIf(node -> node instanceof VBox);

                return new WordAndDefinition(word, definition);
            } else {
                return new WordAndDefinition(word, "No definition found for the word: " + word);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new WordAndDefinition(word, "Error fetching word details");
        }
    }

    /**This method displays the word details that were fetched from the API.
     *onto the text box and also presents an alert for terms and conditions.
     *
     *@param word presnets a word
     *@param definition presents a definition
     *
     */
    private void displayWordDetails(String word, String definition) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Word Details");
        alert.setHeaderText("Word: " + word);
        alert.setContentText(definition);
        alert.showAndWait();
    }

    /**this method method solely displays the terms and conditions that i had created.
     *
     */
    private void displayTermsAndConditions() {
        Stage termsStage = new Stage();
        VBox termsRoot = new VBox();
        Scene termsScene = new Scene(termsRoot, 400, 300);

        Label termsLabel = new Label("Welcome to UGA Dictionary \n"
            + "By using any UGA Property, you agree to\n be bound by these "
            + "terms and conditions. ");
        termsRoot.getChildren().add(termsLabel);

        termsStage.setTitle("Terms and Conditions");
        termsStage.setScene(termsScene);
        termsStage.show();
    }

    /**This method takes the API and searches through it using the definiton of the word given.
     *by the user.
     */
    private void searchBooks() {
        String searchQuery = searchBar.getText().trim();
        if (!searchQuery.isEmpty()) {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://openlibrary.org/search.json?q=" + searchQuery))
                    .GET()
                    .build();

                HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());



                Gson gson = new Gson();
                BookSearchResult result = gson.fromJson(
                    response.body(), BookSearchResult.class);
                if (result.getDocs() == null || result.getDocs().isEmpty()) {
                    showAlert("No Books Found", "No books found for the query: " + searchQuery, "");
                    return;
                }
                StringBuilder bookTitles = new StringBuilder();
                int count = 0;
                //   List<Book> books = result.getDocs();
                //if (books.isEmpty()) {
                //  showAlert(
                //      "No Books Found", "No books found containing the searched word: ", "");
                // } else {
                //  StringBuilder bookTitles = new StringBuilder();
                //  int count = 0;
                for (Book book : result.getDocs()) {
                    if (count >= 30) {
                        break;
                    }
                    bookTitles.append(book.getTitle()).append("\n");
                    if (book.getAuthorName() != null && !book.getAuthorName().isEmpty()) {
                        bookTitles.append
                            ("Author(s): ").append
                            (String.join(", ", book.getAuthorName())).append("\n");
                    }
                    bookTitles.append("\n");
                    count++;
                }

                showAlert("Books Found", "Books containing the definition of the searched word:"
                              , bookTitles.toString());

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to fetch book details"
                          , "An error occurred while fetching book details.");
            }
        } else {
            showAlert("No Search Query", "Please enter a search query for books"
                      , "Please enter a word to search for books.");
        }
    }

    /**this method displays book titles after getting the word out.
     *
     *@param result prints results out through def.
     *
     */
    private void displayBookTitles(BookSearchResult result) {
        List<Book> books = result.getDocs();
        if (books.isEmpty()) {
            showAlert("No Books Found", "No books found containing the searched word", "");
        } else {
            StringBuilder bookTitles = new StringBuilder();
            for (Book book : books) {
                bookTitles.append(book.getTitle()).append("\n");
            }
            showAlert("Books Found", "Books containing the searched word:", bookTitles.toString());
        }
    }

    /**This method searches the book and puts an alert out.
     *@param definition
     */
    private void searchBooks(String definition) {
        if (!definition.isEmpty()) {
            // Use the definition to search for books
            System.out.println("Searching books related to definition: " + definition);
            // Your book search logic goes here
        } else {
            showAlert("No Definition", "Please search for a word first"
                      , "Please enter a word to search for books.");
        }
    }

    /**this method creates an alert for the books to be listed.
     *
     *@param title
     *@param header
     *@param content
     */
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**This method just instances of variables.
     *
     */
    public class WordDefinition {
        private String word;
        private String phonetic;
        private List<Phonetic> phonetics;
        private String origin;
        private List<Meaning> meanings;
    }

    /**this method just holds instances of phonetics.
     *
     */
    public class Phonetic {
        private String text;
        private String audio;
    }

    /**this method just holds instances of variables for meaning.
     *
     */
    public class Meaning {
        private String partOfSpeech;
        private List<Definition> definitions;
    }

    /**this method just holds instances of variables for definition.
     *
     *
     */
    public class Definition {
        private String definition;
        private String example;
        private List<String> synonyms;
        private List<String> antonyms;
    }

    /**this method only allows 30 count of books.
     *
     *
     */
    public class BookSearchResult {
        private int start;
        private int numFound;
        private List<Book> docs;
        /**returns the num found.
         *
         *@return numFound
         */

        public int getNumFound() {
            return numFound;
        }
        /**returns the start method.
         *
         *@return start
         */

        public int getStart() {
            return start;
        }

        /** this method returns the documents.
         *
         *@return docs
         *
         */
        public List<Book> getDocs() {
            return docs;
        }
    }

    /**this method initialize the title of the book.
     *
     *
     */
    public class Book {
        private String title;
        private List <String> authorname;
        // private Integer first_publish_year;
        /**this method gets the titles of the books.
         *
         *@return title
         */

        public String getTitle() {
            return title;
        }
        /**returns tghe authro name.
         *
         *@return authorname
         */

        public List<String> getAuthorName() {
            return authorname;
        }
    }

    /**this method intinalizes the word and definition variables.
     *
     */
    public class WordAndDefinition {
        private String word;
        private String definition;

        /**this method returns the word and the definition.
         *
         *
         *@param word
         *@param definition
         */
        public WordAndDefinition(String word, String definition) {
            this.word = word;
            this.definition = definition;
        }

        /**this method gets the word and returns it.
         *
         *
         *@return word
         */
        public String getWord() {
            return word;
        }

        /**this method returns the definition of the word.
         *
         *@return definition
         *
         */
        public String getDefinition() {
            return definition;
        }
    }
} // ApiApp
