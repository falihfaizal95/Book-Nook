package cs1302.api;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Translation service using LibreTranslate API.
 */
public class TranslationService {
    private static final String LIBRE_TRANSLATE_URL = "https://libretranslate.de/translate";
    private final HttpClient client;
    private final Gson gson;

    public TranslationService() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public CompletableFuture<String> translate(String text, String fromLang, String toLang) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonBody = gson.toJson(new TranslationRequest(text, fromLang, toLang));
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LIBRE_TRANSLATE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                TranslationResponse result = gson.fromJson(response.body(), TranslationResponse.class);
                return result != null ? result.translatedText : "Translation failed";
            } catch (Exception e) {
                return "Translation error: " + e.getMessage();
            }
        });
    }

    private static class TranslationRequest {
        @SerializedName("q")
        private String text;
        @SerializedName("source")
        private String source;
        @SerializedName("target")
        private String target;

        public TranslationRequest(String text, String source, String target) {
            this.text = text;
            this.source = source;
            this.target = target;
        }
    }

    private static class TranslationResponse {
        @SerializedName("translatedText")
        private String translatedText;
    }
}
