# Enhanced Dictionary & Library App

A comprehensive JavaFX application that combines dictionary lookup, book discovery, flashcard learning, and AI-assisted study features.

## Features

### Phase 1: Core Features ✅
- **Dictionary Lookup**: Search words with definitions, pronunciations, and audio
- **Book Discovery**: Search Open Library for books with covers and details
- **Split-View UI**: Dictionary and book results in separate panes
- **Caching System**: SQLite database for offline access to recent searches
- **Audio Pronunciation**: Play word pronunciations from dictionary API
- **Favorites & History**: Save words and books, track search history

### Phase 2: Learning & Accessibility ✅
- **Flashcard System**: Spaced repetition learning with custom decks
- **Translation Service**: Multi-language support (English, Spanish, French, German, Italian)
- **Theme Support**: Light, Dark, and High Contrast themes
- **Offline Mode**: Work with cached content when offline
- **Book Sorting**: Sort books by title, author, or year
- **Accessibility Features**: Screen reader support and keyboard navigation

### Phase 3: Enrichment & Distribution ✅
- **Content Enrichment**: Wikipedia summaries and Project Gutenberg previews
- **AI Assist**: Generate example sentences, study plans, and suggestions
- **Export Features**: Export to CSV, Anki format, study packs, and favorites
- **Sharing**: Generate shareable links for words and definitions
- **Native Packaging**: Ready for distribution as native macOS app

## Technology Stack

- **Java 17** with JavaFX 21
- **Maven** for dependency management
- **SQLite** for local caching and data storage
- **Gson** for JSON parsing
- **HTTP Client** for API communication
- **Open Library API** for book data
- **Dictionary API** for word definitions
- **LibreTranslate** for translations
- **Wikipedia API** for content enrichment

## Installation & Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build and Run

1. **Clone the repository**:
   ```bash
   git clone https://github.com/falihfaizal95/cs1302-api-app.git
   cd cs1302-api-app
   ```

2. **Compile the project**:
   ```bash
   mvn clean compile
   ```

3. **Run the application**:
   ```bash
   mvn javafx:run
   ```

### Alternative: Run with JavaFX Maven Plugin
```bash
mvn clean javafx:run
```

## Usage

### Dictionary Tab
- Enter a word in the search bar
- View definitions, parts of speech, and examples
- Play audio pronunciation
- Add words to favorites or flashcard decks
- Translate words to other languages
- Use AI assist for learning help

### Books Tab
- Search for books by title, author, or topic
- View book covers and details
- Sort results by different criteria
- Add books to favorites
- View detailed book information

### Flashcards Tab
- Create custom study decks
- Add words from dictionary searches
- Study with spaced repetition algorithm
- Track learning progress

### Enrichment Tab
- Get Wikipedia summaries for topics
- Preview Project Gutenberg books
- Access additional context and information

### Export Tab
- Export flashcard decks to CSV or Anki format
- Create study packs with all your materials
- Export favorites and search history
- Generate shareable links

### Settings Tab
- Choose theme (Light, Dark, High Contrast)
- Enable accessibility features
- Adjust font sizes and preferences

## API Integration

The app integrates with several free APIs:

- **Dictionary API**: `https://api.dictionaryapi.dev/`
- **Open Library**: `https://openlibrary.org/`
- **LibreTranslate**: `https://libretranslate.de/`
- **Wikipedia**: `https://en.wikipedia.org/api/`
- **Project Gutenberg**: `https://gutendex.com/`

## Project Structure

```
src/main/java/cs1302/api/
├── ApiApp.java              # Main application class
├── ApiDriver.java           # Application launcher
├── CacheService.java        # SQLite caching service
├── TranslationService.java  # Multi-language translation
├── ContentEnrichmentService.java # Wikipedia/Gutenberg integration
├── AIAssistService.java     # AI-powered learning assistance
├── ExportService.java       # Data export functionality
├── ThemeManager.java        # UI theming and accessibility
├── FlashcardDeck.java       # Spaced repetition system
└── OpenLibrarySearchApi.java # Book search example
```

## Development

### Adding New Features
1. Create new service classes in the `cs1302.api` package
2. Update the main `ApiApp.java` to integrate new features
3. Add UI components as needed
4. Update the README with new feature descriptions

### Building for Distribution
```bash
# Create native package (macOS)
mvn clean package jpackage:jpackage
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is open source and available under the MIT License.

## Acknowledgments

- Open Library for book data and covers
- Dictionary API for word definitions
- LibreTranslate for translation services
- Wikipedia for content enrichment
- Project Gutenberg for public domain books

---

**Built with ❤️ for CS1302 API Project**
