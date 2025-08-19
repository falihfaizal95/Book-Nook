package cs1302.api;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * AI assist service for generating example sentences and suggesting related concepts.
 * Uses a simple template-based approach for demonstration.
 */
public class AIAssistService {
    private static final String[] EXAMPLE_TEMPLATES = {
        "The word '%s' is commonly used in academic writing.",
        "In everyday conversation, you might say: 'I need to %s this problem.'",
        "A good example sentence: 'The %s of this concept is quite complex.'",
        "You could use it like this: 'Let me %s what you mean.'",
        "In literature: 'The %s of the story reveals deeper meaning.'"
    };

    private static final String[] RELATED_CONCEPTS = {
        "synonyms", "antonyms", "etymology", "usage patterns", "collocations",
        "word families", "semantic fields", "register", "frequency", "context"
    };

    public CompletableFuture<String> generateExampleSentence(String word, String definition) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Simple template-based generation
                String template = EXAMPLE_TEMPLATES[(int) (Math.random() * EXAMPLE_TEMPLATES.length)];
                String example = String.format(template, word);
                
                // Add context based on definition
                if (definition.contains("noun")) {
                    example += " (used as a noun)";
                } else if (definition.contains("verb")) {
                    example += " (used as a verb)";
                } else if (definition.contains("adjective")) {
                    example += " (used as an adjective)";
                }
                
                return example;
            } catch (Exception e) {
                return "Unable to generate example sentence for: " + word;
            }
        });
    }

    public CompletableFuture<String> suggestRelatedConcepts(String word) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                StringBuilder suggestions = new StringBuilder();
                suggestions.append("Related concepts for '").append(word).append("':\n\n");
                
                // Generate suggestions based on word characteristics
                if (word.length() > 8) {
                    suggestions.append("• Long word - consider breaking it down\n");
                }
                if (word.contains("-")) {
                    suggestions.append("• Compound word - study each part\n");
                }
                if (word.endsWith("tion") || word.endsWith("sion")) {
                    suggestions.append("• Noun ending - related to action/process\n");
                }
                if (word.endsWith("ly")) {
                    suggestions.append("• Adverb ending - describes how\n");
                }
                
                suggestions.append("\nStudy suggestions:\n");
                suggestions.append("• Look up ").append(RELATED_CONCEPTS[(int) (Math.random() * RELATED_CONCEPTS.length)]).append("\n");
                suggestions.append("• Practice in different contexts\n");
                suggestions.append("• Create your own example sentences\n");
                suggestions.append("• Review similar words in your deck\n");
                
                return suggestions.toString();
            } catch (Exception e) {
                return "Unable to generate suggestions for: " + word;
            }
        });
    }

    public CompletableFuture<String> createStudyPlan(String word, String definition, int difficulty) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                StringBuilder plan = new StringBuilder();
                plan.append("Study Plan for '").append(word).append("'\n");
                plan.append("Difficulty Level: ").append(difficulty).append("/5\n\n");
                
                plan.append("Day 1:\n");
                plan.append("• Review definition and pronunciation\n");
                plan.append("• Create 2 example sentences\n");
                plan.append("• Add to flashcard deck\n\n");
                
                plan.append("Day 2:\n");
                plan.append("• Review flashcard\n");
                plan.append("• Practice pronunciation\n");
                plan.append("• Find synonyms and antonyms\n\n");
                
                plan.append("Day 3:\n");
                plan.append("• Use word in conversation/writing\n");
                plan.append("• Review related concepts\n");
                plan.append("• Test understanding\n\n");
                
                plan.append("Day 7:\n");
                plan.append("• Final review\n");
                plan.append("• Assess retention\n");
                plan.append("• Plan next review date\n");
                
                return plan.toString();
            } catch (Exception e) {
                return "Unable to create study plan for: " + word;
            }
        });
    }

    public CompletableFuture<String> summarizeDefinition(String definition) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Simple summarization by extracting key parts
                String[] lines = definition.split("\n");
                StringBuilder summary = new StringBuilder();
                summary.append("Summary:\n");
                
                for (String line : lines) {
                    if (line.contains("Part of Speech:")) {
                        summary.append("• ").append(line.trim()).append("\n");
                    } else if (line.startsWith("- ")) {
                        summary.append("• ").append(line.substring(2)).append("\n");
                        break; // Just the first definition
                    }
                }
                
                return summary.toString();
            } catch (Exception e) {
                return "Unable to summarize definition";
            }
        });
    }
}
