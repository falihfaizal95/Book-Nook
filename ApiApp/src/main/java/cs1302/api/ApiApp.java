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
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import java.util.List;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Optional;




/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {
    Stage stage;
    Scene scene;
    VBox root;
    TextField searchBar;
    CacheService cache;
    TranslationService translationService;
    ThemeManager themeManager;
    ContentEnrichmentService enrichmentService;
    ExportService exportService;
    AIAssistService aiAssistService;
    TextArea wordDetailsArea;
    VBox booksList;
    Button playAudioButton;
    String currentAudioUrl;
    ObservableList<FlashcardDeck> decks;
    ObservableList<Book> filteredBooks;
    ComboBox<String> languageCombo;
    ComboBox<String> sortCombo;
    CheckBox offlineModeCheck;
    TabPane mainTabPane;
    ListView<FlashcardDeck> deckListView;
    ProgressIndicator loadingIndicator;
    TextArea enrichmentArea;
    TextArea aiAssistArea;

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox();
        cache = new CacheService(System.getProperty("user.home") + "/.apiapp/cache.db");
        translationService = new TranslationService();
        themeManager = new ThemeManager();
        enrichmentService = new ContentEnrichmentService();
        exportService = new ExportService();
        aiAssistService = new AIAssistService();
        decks = FXCollections.observableArrayList();
        filteredBooks = FXCollections.observableArrayList();
    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        setupMainUI();
        scene = new Scene(root, 1200, 800);
        setupTheme();
        stage.setTitle("Enhanced Dictionary & Library App");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.show();
    } // start

    private void setupMainUI() {
        BorderPane mainLayout = new BorderPane();
        
        // Top toolbar
        HBox topToolbar = createTopToolbar();
        mainLayout.setTop(topToolbar);
        
        // Main content with tabs
        mainTabPane = new TabPane();
        mainTabPane.getTabs().addAll(
            createDictionaryTab(),
            createBooksTab(),
            createFlashcardsTab(),
            createEnrichmentTab(),
            createExportTab(),
            createSettingsTab()
        );
        mainLayout.setCenter(mainTabPane);
        
        root.getChildren().add(mainLayout);
    }

    private HBox createTopToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10));
        toolbar.setAlignment(Pos.CENTER_LEFT);
        
        Label appNameLabel = new Label("Enhanced Dictionary & Library");
        appNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        searchBar = new TextField();
        searchBar.setPromptText("Search for words or books...");
        searchBar.setPrefWidth(300);
        
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> performSearch());
        
        languageCombo = new ComboBox<>();
        languageCombo.getItems().addAll("English", "Spanish", "French", "German", "Italian");
        languageCombo.setValue("English");
        
        offlineModeCheck = new CheckBox("Offline Mode");
        offlineModeCheck.setOnAction(e -> toggleOfflineMode());
        
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);
        
        toolbar.getChildren().addAll(
            appNameLabel, new Separator(), searchBar, searchButton,
            new Separator(), languageCombo, offlineModeCheck, loadingIndicator
        );
        
        return toolbar;
    }

    private Tab createDictionaryTab() {
        Tab tab = new Tab("Dictionary");
        tab.setClosable(false);
        
        SplitPane splitPane = new SplitPane();
        
        // Left: Dictionary
        VBox dictPane = new VBox(10);
        dictPane.setPadding(new Insets(10));
        
        wordDetailsArea = new TextArea();
        wordDetailsArea.setEditable(false);
        wordDetailsArea.setPrefRowCount(20);
        
        HBox dictControls = new HBox(10);
        playAudioButton = new Button("🔊 Play");
        playAudioButton.setDisable(true);
        playAudioButton.setOnAction(e -> playCurrentAudio());
        
        Button favoriteWordButton = new Button("⭐ Favorite");
        favoriteWordButton.setOnAction(e -> addToFavorites());
        
        Button translateButton = new Button("🌐 Translate");
        translateButton.setOnAction(e -> translateCurrentWord());
        
        Button addToDeckButton = new Button("📚 Add to Deck");
        addToDeckButton.setOnAction(e -> addToDeck());
        
        Button aiAssistButton = new Button("🤖 AI Assist");
        aiAssistButton.setOnAction(e -> showAIAssist());
        
        dictControls.getChildren().addAll(playAudioButton, favoriteWordButton, translateButton, addToDeckButton, aiAssistButton);
        
        dictPane.getChildren().addAll(wordDetailsArea, dictControls);
        VBox.setVgrow(wordDetailsArea, Priority.ALWAYS);
        
        // Right: Translation results
        VBox translationPane = new VBox(10);
        translationPane.setPadding(new Insets(10));
        Label translationLabel = new Label("Translation");
        translationLabel.setStyle("-fx-font-weight: bold;");
        TextArea translationArea = new TextArea();
        translationArea.setEditable(false);
        translationArea.setPrefRowCount(20);
        translationPane.getChildren().addAll(translationLabel, translationArea);
        VBox.setVgrow(translationArea, Priority.ALWAYS);
        
        splitPane.getItems().addAll(dictPane, translationPane);
        splitPane.setDividerPositions(0.6);
        
        tab.setContent(splitPane);
        return tab;
    }

    private Tab createBooksTab() {
        Tab tab = new Tab("Books");
        tab.setClosable(false);
        
        VBox booksPane = new VBox(10);
        booksPane.setPadding(new Insets(10));
        
        // Filters
        HBox filters = new HBox(10);
        sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("Title", "Author", "Year");
        sortCombo.setValue("Title");
        sortCombo.setOnAction(e -> sortBooks());
        
        Button searchBooksButton = new Button("Search Books");
        searchBooksButton.setOnAction(e -> searchBooks());
        
        filters.getChildren().addAll(new Label("Sort by:"), sortCombo, searchBooksButton);
        
        // Books list
        booksList = new VBox(5);
        ScrollPane booksScroll = new ScrollPane(booksList);
        booksScroll.setFitToWidth(true);
        booksScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
        
        booksPane.getChildren().addAll(filters, booksScroll);
        VBox.setVgrow(booksScroll, Priority.ALWAYS);
        
        tab.setContent(booksPane);
        return tab;
    }

    private Tab createFlashcardsTab() {
        Tab tab = new Tab("Flashcards");
        tab.setClosable(false);
        
        SplitPane splitPane = new SplitPane();
        
        // Left: Deck management
        VBox deckPane = new VBox(10);
        deckPane.setPadding(new Insets(10));
        
        Label deckLabel = new Label("Your Decks");
        deckLabel.setStyle("-fx-font-weight: bold;");
        
        deckListView = new ListView<>(decks);
        deckListView.setCellFactory(param -> new DeckListCell());
        
        HBox deckControls = new HBox(10);
        Button newDeckButton = new Button("New Deck");
        newDeckButton.setOnAction(e -> createNewDeck());
        
        Button studyDeckButton = new Button("Study Deck");
        studyDeckButton.setOnAction(e -> studySelectedDeck());
        
        deckControls.getChildren().addAll(newDeckButton, studyDeckButton);
        
        deckPane.getChildren().addAll(deckLabel, deckListView, deckControls);
        VBox.setVgrow(deckListView, Priority.ALWAYS);
        
        // Right: Study area
        VBox studyPane = new VBox(10);
        studyPane.setPadding(new Insets(10));
        studyPane.setAlignment(Pos.CENTER);
        
        Label studyLabel = new Label("Select a deck to study");
        studyLabel.setStyle("-fx-font-size: 18px;");
        studyPane.getChildren().add(studyLabel);
        
        splitPane.getItems().addAll(deckPane, studyPane);
        splitPane.setDividerPositions(0.4);
        
        tab.setContent(splitPane);
        return tab;
    }

    private Tab createSettingsTab() {
        Tab tab = new Tab("Settings");
        tab.setClosable(false);
        
        GridPane settingsGrid = new GridPane();
        settingsGrid.setPadding(new Insets(20));
        settingsGrid.setHgap(10);
        settingsGrid.setVgap(10);
        
        // Theme selection
        Label themeLabel = new Label("Theme:");
        ComboBox<ThemeManager.Theme> themeCombo = new ComboBox<>();
        themeCombo.getItems().addAll(ThemeManager.Theme.values());
        themeCombo.setValue(ThemeManager.Theme.LIGHT);
        themeCombo.setOnAction(e -> themeManager.applyTheme(scene, themeCombo.getValue()));
        
        // Accessibility
        Label accessibilityLabel = new Label("Accessibility:");
        CheckBox accessibilityCheck = new CheckBox("Enable accessibility features");
        accessibilityCheck.setOnAction(e -> themeManager.setAccessibilityFeatures(scene, accessibilityCheck.isSelected()));
        
        // Font size
        Label fontSizeLabel = new Label("Font Size:");
        Slider fontSizeSlider = new Slider(10, 24, 14);
        fontSizeSlider.setShowTickLabels(true);
        fontSizeSlider.setShowTickMarks(true);
        
        settingsGrid.addRow(0, themeLabel, themeCombo);
        settingsGrid.addRow(1, accessibilityLabel, accessibilityCheck);
        settingsGrid.addRow(2, fontSizeLabel, fontSizeSlider);
        
        tab.setContent(settingsGrid);
        return tab;
    }

    private void setupTheme() {
        themeManager.applyTheme(scene, ThemeManager.Theme.LIGHT);
    }

    private Tab createEnrichmentTab() {
        Tab tab = new Tab("Enrichment");
        tab.setClosable(false);
        
        VBox enrichmentPane = new VBox(10);
        enrichmentPane.setPadding(new Insets(10));
        
        Label enrichmentLabel = new Label("Content Enrichment");
        enrichmentLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        HBox enrichmentControls = new HBox(10);
        Button wikipediaButton = new Button("📚 Wikipedia Summary");
        wikipediaButton.setOnAction(e -> getWikipediaSummary());
        
        Button gutenbergButton = new Button("📖 Gutenberg Preview");
        gutenbergButton.setOnAction(e -> getGutenbergPreview());
        
        enrichmentControls.getChildren().addAll(wikipediaButton, gutenbergButton);
        
        enrichmentArea = new TextArea();
        enrichmentArea.setEditable(false);
        enrichmentArea.setPrefRowCount(15);
        enrichmentArea.setPromptText("Enrichment content will appear here...");
        
        enrichmentPane.getChildren().addAll(enrichmentLabel, enrichmentControls, enrichmentArea);
        VBox.setVgrow(enrichmentArea, Priority.ALWAYS);
        
        tab.setContent(enrichmentPane);
        return tab;
    }

    private Tab createExportTab() {
        Tab tab = new Tab("Export & Share");
        tab.setClosable(false);
        
        VBox exportPane = new VBox(10);
        exportPane.setPadding(new Insets(10));
        
        Label exportLabel = new Label("Export & Sharing Options");
        exportLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        GridPane exportGrid = new GridPane();
        exportGrid.setHgap(10);
        exportGrid.setVgap(10);
        
        Button exportCSVButton = new Button("📊 Export Deck to CSV");
        exportCSVButton.setOnAction(e -> exportDeckToCSV());
        
        Button exportAnkiButton = new Button("📝 Export Deck to Anki");
        exportAnkiButton.setOnAction(e -> exportDeckToAnki());
        
        Button exportStudyPackButton = new Button("📦 Export Study Pack");
        exportStudyPackButton.setOnAction(e -> exportStudyPack());
        
        Button shareLinkButton = new Button("🔗 Generate Share Link");
        shareLinkButton.setOnAction(e -> generateShareLink());
        
        Button exportFavoritesButton = new Button("⭐ Export Favorites");
        exportFavoritesButton.setOnAction(e -> exportFavorites());
        
        exportGrid.addRow(0, exportCSVButton, exportAnkiButton);
        exportGrid.addRow(1, exportStudyPackButton, shareLinkButton);
        exportGrid.addRow(2, exportFavoritesButton);
        
        exportPane.getChildren().addAll(exportLabel, exportGrid);
        
        tab.setContent(exportPane);
        return tab;
    }

    private void performSearch() {
        String query = searchBar.getText().trim();
        if (query.isEmpty()) return;
        
        loadingIndicator.setVisible(true);
        mainTabPane.getSelectionModel().select(0); // Switch to dictionary tab
        
                CompletableFuture
            .supplyAsync(() -> fetchWordDetails(query))
                    .thenAccept(result -> Platform.runLater(() -> {
                cache.addHistory("word", query);
                        wordDetailsArea.setText(result.getDefinition());
                currentAudioUrl = result.getAudioUrl();
                playAudioButton.setDisable(currentAudioUrl == null || currentAudioUrl.isBlank());
                loadingIndicator.setVisible(false);
                    }))
                    .exceptionally(ex -> {
                Platform.runLater(() -> {
                    wordDetailsArea.setText("Error fetching word details");
                    loadingIndicator.setVisible(false);
                });
                        return null;
                    });
    }

    private void toggleOfflineMode() {
        boolean offline = offlineModeCheck.isSelected();
        // In a real app, this would disable network calls and show cached content
        showAlert("Offline Mode", offline ? "Offline mode enabled" : "Offline mode disabled", 
                 offline ? "Only cached content will be shown" : "Online features restored");
    }

    private void addToFavorites() {
        String word = searchBar.getText().trim();
        if (!word.isEmpty()) {
            cache.addFavorite("word", word);
            showAlert("Added to Favorites", "Word added to favorites", word);
        }
    }

    private void translateCurrentWord() {
        String word = searchBar.getText().trim();
        if (word.isEmpty()) return;
        
        String targetLang = languageCombo.getValue().toLowerCase();
        if ("english".equals(targetLang)) {
            showAlert("Translation", "Already in English", "");
            return;
        }
        
        translationService.translate(word, "en", targetLang.substring(0, 2))
            .thenAccept(translation -> Platform.runLater(() -> {
                showAlert("Translation", "Translation result", translation);
            }));
    }

    private void addToDeck() {
        final String word = searchBar.getText().trim();
        if (word.isEmpty()) return;
        
        TextInputDialog dialog = new TextInputDialog("New Deck");
        dialog.setTitle("Add to Deck");
        dialog.setHeaderText("Enter deck name or select existing deck");
        dialog.setContentText("Deck name:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(deckName -> {
            FlashcardDeck deck = decks.stream()
                .filter(d -> d.getName().equals(deckName))
                .findFirst()
                .orElseGet(() -> {
                    FlashcardDeck newDeck = new FlashcardDeck(deckName);
                    decks.add(newDeck);
                    return newDeck;
                });
            
            final String definition = wordDetailsArea.getText();
            deck.addCard(word, definition);
            showAlert("Added to Deck", "Word added to deck: " + deckName, word);
        });
    }

    private void createNewDeck() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Deck");
        dialog.setHeaderText("Create a new flashcard deck");
        dialog.setContentText("Deck name:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(deckName -> {
            FlashcardDeck deck = new FlashcardDeck(deckName);
            decks.add(deck);
            showAlert("Deck Created", "New deck created", deckName);
        });
    }

    private void studySelectedDeck() {
        FlashcardDeck selected = deckListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Deck Selected", "Please select a deck to study", "");
            return;
        }
        
        List<Flashcard> dueCards = selected.getDueCards();
        if (dueCards.isEmpty()) {
            showAlert("No Cards Due", "No cards are due for review", "");
            return;
        }
        
        // Simple study session - in a real app, this would be more sophisticated
        showAlert("Study Session", "Cards due: " + dueCards.size(), 
                 dueCards.stream().map(Flashcard::getWord).collect(Collectors.joining(", ")));
    }

    private void sortBooks() {
        if (filteredBooks.isEmpty()) return;
        
        final String sortBy = sortCombo.getValue();
        switch (sortBy) {
            case "Title":
                filteredBooks.sort(Comparator.comparing(Book::getTitle, Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case "Author":
                filteredBooks.sort(Comparator.comparing(b -> 
                    b.getAuthorName() != null && !b.getAuthorName().isEmpty() ? 
                    b.getAuthorName().get(0) : "", Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case "Year":
                // Would need to add year field to Book class
                break;
        }
    }

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
    private WordAndDefinition fetchWordDetails(String word) {
        // Try cache first
        try {
            var cached = cache.getCachedDefinition(word);
            if (cached.isPresent()) {
                return new WordAndDefinition(word, cached.get(), null);
            }
        } catch (Exception ignore) {}
        try {
            HttpClient client = HttpClient.newHttpClient();
            String encodedWord = java.net.URLEncoder.encode(word, java.nio.charset.StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.dictionaryapi.dev/api/v2/entries/en/" + encodedWord))
                .GET()
                .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new Gson();
            WordDefinition[] definitions = gson.fromJson(response.body(), WordDefinition[].class);
            if (definitions != null && definitions.length > 0) {
                String definition = "Word: " + definitions[0].word + "\n";
                String audioUrl = null;

                for (Meaning meaning : definitions[0].meanings) {
                    definition += "Part of Speech: " + meaning.partOfSpeech + "\n";
                    for (Definition def : meaning.definitions) {
                        definition += "- " + def.definition + "\n";
                    }
                }
                if (definitions[0].phonetics != null) {
                    for (Phonetic ph : definitions[0].phonetics) {
                        if (ph.audio != null && !ph.audio.isBlank()) {
                            audioUrl = ph.audio.startsWith("https:") ? ph.audio : ("https:" + ph.audio);
                            break;
                        }
                    }
                }
                cache.putCachedDefinition(word, definition);
                return new WordAndDefinition(word, definition, audioUrl);
            } else {
                return new WordAndDefinition(word, "No definition found for the word: " + word, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new WordAndDefinition(word, "Error fetching word details", null);
        }
    }

    private void showBookDetails(Book book) {
        StringBuilder details = new StringBuilder();
        details.append("Title: ").append(book.getTitle() == null ? "Unknown" : book.getTitle()).append("\n\n");
        
        List<String> authors = book.getAuthorName();
        details.append("Author(s): ").append((authors == null || authors.isEmpty()) ? "Unknown" : String.join(", ", authors)).append("\n\n");
        
        if (book.getCoverUrl() != null) {
            details.append("Cover ID: ").append(book.getCoverId()).append("\n");
            details.append("Cover URL: ").append(book.getCoverUrl()).append("\n\n");
        }
        
        details.append("This book is available through the Open Library project.\n");
        details.append("You can find more details and potentially read it online.");
        
        showAlert("Book Details", book.getTitle() == null ? "Unknown Title" : book.getTitle(), details.toString());
    }

    // Phase 3: Content Enrichment Methods
    private void getWikipediaSummary() {
        String word = searchBar.getText().trim();
        if (word.isEmpty()) {
            showAlert("No Word", "Please search for a word first", "");
            return;
        }
        
        enrichmentArea.setText("Fetching Wikipedia summary...");
        enrichmentService.getWikipediaSummary(word)
            .thenAccept(summary -> Platform.runLater(() -> {
                enrichmentArea.setText(summary);
            }));
    }

    private void getGutenbergPreview() {
        String word = searchBar.getText().trim();
        if (word.isEmpty()) {
            showAlert("No Word", "Please search for a word first", "");
            return;
        }
        
        enrichmentArea.setText("Fetching Gutenberg preview...");
        enrichmentService.getGutenbergPreview(word)
            .thenAccept(preview -> Platform.runLater(() -> {
                enrichmentArea.setText(preview);
            }));
    }

    private void showAIAssist() {
        String word = searchBar.getText().trim();
        if (word.isEmpty()) {
            showAlert("No Word", "Please search for a word first", "");
            return;
        }
        
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("AI Assist");
        dialog.setHeaderText("Choose AI assistance option");
        
        ButtonType exampleButton = new ButtonType("Generate Example");
        ButtonType suggestionsButton = new ButtonType("Study Suggestions");
        ButtonType studyPlanButton = new ButtonType("Create Study Plan");
        ButtonType summaryButton = new ButtonType("Summarize Definition");
        ButtonType cancelButton = ButtonType.CANCEL;
        
        dialog.getDialogPane().getButtonTypes().addAll(exampleButton, suggestionsButton, studyPlanButton, summaryButton, cancelButton);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == exampleButton) {
                aiAssistService.generateExampleSentence(word, wordDetailsArea.getText())
                    .thenAccept(result -> Platform.runLater(() -> showAlert("AI Example", result, "")));
            } else if (dialogButton == suggestionsButton) {
                aiAssistService.suggestRelatedConcepts(word)
                    .thenAccept(result -> Platform.runLater(() -> showAlert("AI Suggestions", result, "")));
            } else if (dialogButton == studyPlanButton) {
                aiAssistService.createStudyPlan(word, wordDetailsArea.getText(), 3)
                    .thenAccept(result -> Platform.runLater(() -> showAlert("AI Study Plan", result, "")));
            } else if (dialogButton == summaryButton) {
                aiAssistService.summarizeDefinition(wordDetailsArea.getText())
                    .thenAccept(result -> Platform.runLater(() -> showAlert("AI Summary", result, "")));
            }
            return null;
        });
        
        dialog.showAndWait();
    }

    // Phase 3: Export Methods
    private void exportDeckToCSV() {
        FlashcardDeck selected = deckListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Deck Selected", "Please select a deck to export", "");
            return;
        }
        
        try {
            String filePath = exportService.getDefaultExportPath("deck_csv").replace(".txt", ".csv");
            exportService.exportDeckToCSV(selected, filePath);
            showAlert("Export Successful", "Deck exported to CSV", filePath);
        } catch (Exception e) {
            showAlert("Export Failed", "Error exporting deck", e.getMessage());
        }
    }

    private void exportDeckToAnki() {
        FlashcardDeck selected = deckListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Deck Selected", "Please select a deck to export", "");
            return;
        }
        
        try {
            String filePath = exportService.getDefaultExportPath("deck_anki").replace(".txt", ".txt");
            exportService.exportDeckToAnki(selected, filePath);
            showAlert("Export Successful", "Deck exported to Anki format", filePath);
        } catch (Exception e) {
            showAlert("Export Failed", "Error exporting deck", e.getMessage());
        }
    }

    private void exportStudyPack() {
        if (decks.isEmpty()) {
            showAlert("No Decks", "No decks to export", "");
            return;
        }
        
        try {
            String filePath = exportService.getDefaultExportPath("study_pack");
            exportService.exportStudyPack(decks, filePath);
            showAlert("Export Successful", "Study pack exported", filePath);
        } catch (Exception e) {
            showAlert("Export Failed", "Error exporting study pack", e.getMessage());
        }
    }

    private void generateShareLink() {
        String word = searchBar.getText().trim();
        if (word.isEmpty()) {
            showAlert("No Word", "Please search for a word first", "");
            return;
        }
        
        String shareLink = exportService.generateShareableLink(word, wordDetailsArea.getText());
        showAlert("Share Link", "Generated shareable link", shareLink);
    }

    private void exportFavorites() {
        // In a real app, this would get favorites from the cache
        try {
            String filePath = exportService.getDefaultExportPath("favorites");
            exportService.exportFavorites(java.util.List.of("example", "favorites"), filePath);
            showAlert("Export Successful", "Favorites exported", filePath);
        } catch (Exception e) {
            showAlert("Export Failed", "Error exporting favorites", e.getMessage());
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
        String searchText = searchBar.getText().trim();
        final String searchQuery = searchText.isEmpty() ? "java programming" : searchText;
        
        loadingIndicator.setVisible(true);
        mainTabPane.getSelectionModel().select(1); // Switch to books tab
        
            CompletableFuture.supplyAsync(() -> {
            try {
                Gson gson = new Gson();
                String json = cache.getCachedBooks(searchQuery)
                    .orElseGet(() -> {
                try {
                    HttpClient client = HttpClient.newHttpClient();
                            String encodedQuery = java.net.URLEncoder.encode(searchQuery, java.nio.charset.StandardCharsets.UTF_8);
                    HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI("https://openlibrary.org/search.json?q=" + encodedQuery))
                        .GET()
                        .build();
                    HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());
                            cache.putCachedBooks(searchQuery, response.body());
                            return response.body();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                BookSearchResult result = gson.fromJson(json, BookSearchResult.class);
                if (result.getDocs() == null || result.getDocs().isEmpty()) {
                    return new Object[] {"No Books Found", "No books found for the query: " + searchQuery, null};
                }
                cache.addHistory("book_query", searchQuery);
                return new Object[] {"Books Found", "", result};
                } catch (Exception e) {
                    e.printStackTrace();
                return new Object[] {"Error", "Failed to fetch book details", null};
            }
        }).thenAccept(args -> Platform.runLater(() -> {
            String title = (String) args[0];
            if ("Books Found".equals(title) && args[2] instanceof BookSearchResult) {
                renderBooks((BookSearchResult) args[2]);
            } else {
                showAlert(title, (String) args[1], "");
            }
            loadingIndicator.setVisible(false);
        }));
    }

    private void renderBooks(BookSearchResult result) {
        booksList.getChildren().clear();
        int count = 0;
        for (Book book : result.getDocs()) {
            if (count >= 30) break;
            
            HBox row = new HBox(10);
            row.setPadding(new Insets(5));
            row.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px;");
            
            // Cover image
            ImageView coverView = new ImageView();
            coverView.setFitHeight(120);
            coverView.setFitWidth(80);
            coverView.setStyle("-fx-border-color: #dddddd; -fx-border-width: 1px;");
            
            String coverUrl = book.getCoverUrl();
            if (coverUrl != null) {
                try {
                    coverView.setImage(new Image(coverUrl, 80, 120, true, true, true));
                } catch (Exception ignore) {
                    // Set placeholder if image fails to load
                    coverView.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #dddddd; -fx-border-width: 1px;");
                }
        } else {
                coverView.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #dddddd; -fx-border-width: 1px;");
            }
            
            // Book details
            VBox textBox = new VBox(5);
            textBox.setPrefWidth(300);
            
            Label title = new Label(book.getTitle() == null ? "Unknown Title" : book.getTitle());
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            title.setWrapText(true);
            
            List<String> authors = book.getAuthorName();
            Label author = new Label((authors == null || authors.isEmpty()) ? "Unknown Author" : "By: " + String.join(", ", authors));
            author.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
            author.setWrapText(true);
            
            HBox buttonBox = new HBox(5);
            Button fav = new Button("⭐ Favorite");
            fav.setStyle("-fx-font-size: 10px;");
            fav.setOnAction(e -> {
                cache.addFavorite("book", title.getText());
                fav.setText("✓ Favorited");
                fav.setDisable(true);
            });
            
            Button details = new Button("📖 Details");
            details.setStyle("-fx-font-size: 10px;");
            details.setOnAction(e -> showBookDetails(book));
            
            buttonBox.getChildren().addAll(fav, details);
            
            textBox.getChildren().addAll(title, author, buttonBox);
            VBox.setVgrow(textBox, Priority.ALWAYS);
            
            row.getChildren().addAll(coverView, textBox);
            booksList.getChildren().add(row);
            count++;
        }
        
        if (count == 0) {
            Label noResults = new Label("No books found. Try a different search term.");
            noResults.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666;");
            booksList.getChildren().add(noResults);
        }
    }

    private void playCurrentAudio() {
        if (currentAudioUrl == null || currentAudioUrl.isBlank()) return;
        try {
            Media media = new Media(currentAudioUrl);
            MediaPlayer player = new MediaPlayer(media);
            player.play();
        } catch (Exception e) {
            showAlert("Audio", "Unable to play pronunciation", "");
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
    public static class WordDefinition {
        private String word;
        private String phonetic;
        private List<Phonetic> phonetics;
        private String origin;
        private List<Meaning> meanings;
    }

    /**this method just holds instances of phonetics.
     *
     */
    public static class Phonetic {
        private String text;
        private String audio;
    }

    /**this method just holds instances of variables for meaning.
     *
     */
    public static class Meaning {
        private String partOfSpeech;
        private List<Definition> definitions;
    }

    /**this method just holds instances of variables for definition.
     *
     *
     */
    public static class Definition {
        private String definition;
        private String example;
        private List<String> synonyms;
        private List<String> antonyms;
    }

    /**this method only allows 30 count of books.
     *
     *
     */
    public static class BookSearchResult {
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
    public static class Book {
        private String title;
        @SerializedName("author_name")
        private List<String> authorName;
        @SerializedName("cover_i")
        private Integer coverId;
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
            return authorName;
        }

        public String getCoverUrl() {
            if (coverId == null) return null;
            return "https://covers.openlibrary.org/b/id/" + coverId + "-M.jpg";
        }

        public Integer getCoverId() {
            return coverId;
        }
    }

    /**this method intinalizes the word and definition variables.
     *
     */
    public static class WordAndDefinition {
        private String word;
        private String definition;
        private String audioUrl;

        /**this method returns the word and the definition.
         *
         *
         *@param word
         *@param definition
         */
        public WordAndDefinition(String word, String definition, String audioUrl) {
            this.word = word;
            this.definition = definition;
            this.audioUrl = audioUrl;
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

        public String getAudioUrl() {
            return audioUrl;
        }
    }

    /**
     * Custom list cell for displaying flashcard decks.
     */
    private static class DeckListCell extends javafx.scene.control.ListCell<FlashcardDeck> {
        @Override
        protected void updateItem(FlashcardDeck deck, boolean empty) {
            super.updateItem(deck, empty);
            if (empty || deck == null) {
                setText(null);
                setGraphic(null);
            } else {
                VBox content = new VBox(5);
                Label nameLabel = new Label(deck.getName());
                nameLabel.setStyle("-fx-font-weight: bold;");
                Label countLabel = new Label(deck.getCards().size() + " cards");
                countLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
                content.getChildren().addAll(nameLabel, countLabel);
                setGraphic(content);
            }
        }
    }
} // ApiApp
