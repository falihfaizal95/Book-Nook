package cs1302.api;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Service for enriching content with Wikipedia summaries and Project Gutenberg previews.
 */
public class ContentEnrichmentService {
    private static final String WIKIPEDIA_API = "https://en.wikipedia.org/api/rest_v1/page/summary/";
    private static final String GUTENBERG_API = "https://gutendex.com/books/?search=";
    private final HttpClient client;
    private final Gson gson;

    public ContentEnrichmentService() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public CompletableFuture<String> getWikipediaSummary(String topic) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String encodedTopic = URLEncoder.encode(topic, StandardCharsets.UTF_8);
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(WIKIPEDIA_API + encodedTopic))
                    .GET()
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                WikipediaResponse result = gson.fromJson(response.body(), WikipediaResponse.class);
                
                if (result != null && result.extract != null) {
                    return result.extract.length() > 300 ? 
                        result.extract.substring(0, 300) + "..." : result.extract;
                }
                return "No Wikipedia summary available for: " + topic;
            } catch (Exception e) {
                return "Error fetching Wikipedia summary: " + e.getMessage();
            }
        });
    }

    public CompletableFuture<String> getGutenbergPreview(String bookTitle) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String encodedTitle = URLEncoder.encode(bookTitle, StandardCharsets.UTF_8);
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GUTENBERG_API + encodedTitle))
                    .GET()
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                GutenbergResponse result = gson.fromJson(response.body(), GutenbergResponse.class);
                
                if (result != null && result.results != null && !result.results.isEmpty()) {
                    GutenbergBook book = result.results.get(0);
                    return String.format("Title: %s\nAuthor: %s\nSubjects: %s\nFirst Sentence: %s",
                        book.title,
                        book.authors != null && !book.authors.isEmpty() ? book.authors.get(0).name : "Unknown",
                        book.subjects != null ? String.join(", ", book.subjects.subList(0, Math.min(3, book.subjects.size()))) : "None",
                        book.excerpt != null ? book.excerpt.substring(0, Math.min(200, book.excerpt.length())) + "..." : "No preview available");
                }
                return "No Gutenberg preview available for: " + bookTitle;
            } catch (Exception e) {
                return "Error fetching Gutenberg preview: " + e.getMessage();
            }
        });
    }

    private static class WikipediaResponse {
        @SerializedName("extract")
        private String extract;
        @SerializedName("title")
        private String title;
    }

    private static class GutenbergResponse {
        @SerializedName("results")
        private java.util.List<GutenbergBook> results;
    }

    private static class GutenbergBook {
        @SerializedName("title")
        private String title;
        @SerializedName("authors")
        private java.util.List<GutenbergAuthor> authors;
        @SerializedName("subjects")
        private java.util.List<String> subjects;
        @SerializedName("excerpt")
        private String excerpt;
    }

    private static class GutenbergAuthor {
        @SerializedName("name")
        private String name;
    }
}
