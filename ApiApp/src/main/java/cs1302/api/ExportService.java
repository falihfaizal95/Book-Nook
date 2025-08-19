package cs1302.api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for exporting data in various formats (CSV, Anki, JSON).
 */
public class ExportService {
    
    public void exportDeckToCSV(FlashcardDeck deck, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write("Word,Definition,Next Review,Interval,Consecutive Correct\n");
            for (Flashcard card : deck.getCards()) {
                writer.write(String.format("\"%s\",\"%s\",%d,%d,%d\n",
                    escapeCsv(card.getWord()),
                    escapeCsv(card.getDefinition()),
                    card.getNextReview(),
                    card.getInterval(),
                    card.getConsecutiveCorrect()));
            }
        }
    }

    public void exportDeckToAnki(FlashcardDeck deck, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write("#separator:tab\n");
            writer.write("#html:true\n");
            writer.write("#tags column:4\n");
            writer.write("#deck column:5\n");
            writer.write("#notetype column:6\n");
            
            for (Flashcard card : deck.getCards()) {
                writer.write(String.format("%s\t%s\t\t%s\t%s\tBasic\n",
                    card.getWord(),
                    card.getDefinition().replace("\n", "<br>"),
                    deck.getName(),
                    deck.getName()));
            }
        }
    }

    public void exportStudyPack(List<FlashcardDeck> decks, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write("Study Pack Export\n");
            writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("Total Decks: " + decks.size() + "\n\n");
            
            for (FlashcardDeck deck : decks) {
                writer.write("=== " + deck.getName() + " ===\n");
                writer.write("Cards: " + deck.getCards().size() + "\n");
                writer.write("Created: " + LocalDateTime.ofEpochSecond(deck.getCreatedAt(), 0, null).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\n\n");
                
                for (Flashcard card : deck.getCards()) {
                    writer.write("Word: " + card.getWord() + "\n");
                    writer.write("Definition: " + card.getDefinition() + "\n");
                    writer.write("Next Review: " + LocalDateTime.ofEpochSecond(card.getNextReview(), 0, null).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\n");
                    writer.write("---\n");
                }
                writer.write("\n");
            }
        }
    }

    public String generateShareableLink(String word, String definition) {
        // In a real app, this would generate a URL that others can use
        return String.format("https://your-app.com/share?word=%s&def=%s", 
            word.replace(" ", "%20"), 
            definition.substring(0, Math.min(100, definition.length())).replace(" ", "%20"));
    }

    public void exportFavorites(List<String> favorites, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write("Favorites Export\n");
            writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");
            
            for (String favorite : favorites) {
                writer.write("- " + favorite + "\n");
            }
        }
    }

    private String escapeCsv(String value) {
        return value.replace("\"", "\"\"");
    }

    public String getDefaultExportPath(String type) {
        String home = System.getProperty("user.home");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return home + "/Desktop/" + type + "_" + timestamp + ".txt";
    }
}
