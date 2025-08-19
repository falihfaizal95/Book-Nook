package cs1302.api;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a flashcard deck with spaced repetition.
 */
public class FlashcardDeck {
    private String name;
    private List<Flashcard> cards;
    private long createdAt;
    private long lastStudied;

    public FlashcardDeck(String name) {
        this.name = name;
        this.cards = new ArrayList<>();
        this.createdAt = Instant.now().getEpochSecond();
        this.lastStudied = 0;
    }

    public void addCard(String word, String definition) {
        cards.add(new Flashcard(word, definition));
    }

    public List<Flashcard> getDueCards() {
        long now = Instant.now().getEpochSecond();
        List<Flashcard> due = new ArrayList<>();
        for (Flashcard card : cards) {
            if (card.isDue(now)) {
                due.add(card);
            }
        }
        return due;
    }

    public String getName() { return name; }
    public List<Flashcard> getCards() { return cards; }
    public long getCreatedAt() { return createdAt; }
    public long getLastStudied() { return lastStudied; }
    public void setLastStudied(long time) { this.lastStudied = time; }
}

class Flashcard {
    private String word;
    private String definition;
    private long nextReview;
    private int interval; // days
    private int easeFactor; // 1.3 to 2.5
    private int consecutiveCorrect;

    public Flashcard(String word, String definition) {
        this.word = word;
        this.definition = definition;
        this.nextReview = Instant.now().getEpochSecond();
        this.interval = 1;
        this.easeFactor = 250; // 2.5 * 100
        this.consecutiveCorrect = 0;
    }

    public boolean isDue(long now) {
        return now >= nextReview;
    }

    public void review(boolean correct) {
        if (correct) {
            consecutiveCorrect++;
            if (consecutiveCorrect == 1) {
                interval = 6;
            } else {
                interval = (int) (interval * (easeFactor / 100.0));
            }
            easeFactor = Math.min(250, easeFactor + 50);
        } else {
            consecutiveCorrect = 0;
            interval = 1;
            easeFactor = Math.max(130, easeFactor - 200);
        }
        nextReview = Instant.now().getEpochSecond() + (interval * 24 * 60 * 60);
    }

    public String getWord() { return word; }
    public String getDefinition() { return definition; }
    public long getNextReview() { return nextReview; }
    public int getInterval() { return interval; }
    public int getConsecutiveCorrect() { return consecutiveCorrect; }
}
