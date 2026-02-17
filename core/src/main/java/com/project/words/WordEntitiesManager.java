package com.project.words;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WordEntitiesManager {
    public static final int MAX_LANES = 5;

    /** List of all possible words that can be generated from a file. */
    public String[] possibleWordList;
    /** The largest length of any word in the possible word list. */
    private int maxWordLength = 0;
    /**
     * The average length of words in the possible word list. Used for
     * calculating WPM
     */
    public float averageWordLength = 0f;

    /** List of word entites currently on the screen. */
    Vector<WordEntity> entities = new Vector<>();
    /** List of entites count in each lane. */
    int[] laneCounts = new int[MAX_LANES];

    /** The current input buffer. */
    public String inputBuffer = "";
    /**
     * Prefixes of the current input buffer. Recalculated whenever the input
     * buffer changes.
     * e.g. input buffer = "col" -> input buffer suffix = ["col", "ol", "l"]
     */
    Vector<String> inputBufferSuffix = new Vector<>();

    /** Listeners for word entity events. */
    Vector<WordEntitiesListener> listeners = new Vector<>();

    // ========================================================================

    public void init() {
        FileHandle file = Gdx.files.internal("words_java.txt");

        String[] lines = file.readString().split("\n");
        List<String> validWords = new ArrayList<>();

        for (String line : lines) {
            // Remove any leading/trailing whitespaces or "\r"
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                validWords.add(trimmed);
                if (trimmed.length() > maxWordLength) {
                    maxWordLength = trimmed.length();
                }
            }
        }

        possibleWordList = validWords.toArray(new String[0]);
        averageWordLength = (float) validWords.stream()
                .mapToInt(String::length)
                .average()
                .orElse(0.0);

        System.out.println(
                "Loaded " + possibleWordList.length
                        + " words from " + file.path());
    }

    public void renderAll(SpriteBatch batch, BitmapFont font) {
        for (WordEntity wordEntity : entities) {
            wordEntity.render(batch, font);
        }
    }

    public void updateAll(float delta) {
        for (WordEntity wordEntity : entities) {
            wordEntity.update(delta);

            // update word's progress based on the current input buffer
            for (String suffix : inputBufferSuffix) {
                String word = wordEntity.word;
                if (word.startsWith(suffix)) {
                    // matched a prefix of the word, set progress to the suffix // length
                    wordEntity.progress = suffix.length();
                    break;
                } else {
                    // no match, set progress to 0
                    wordEntity.progress = 0;
                }
            }
        }
    }

    public void addListener(WordEntitiesListener listener) {
        listeners.add(listener);
    }

    // ========================================================================
    // Entites/buffer interaction

    /**
     * Selects a lane for a new word entity based on the current lane counts.
     * Lanes with fewer entities are more likely to be selected.
     */
    private int selectLaneByWeight() {
        // calculate total weight
        int total = 0;
        for (int count : laneCounts) {
            total += (MAX_LANES - count); // more entities in lane -> less weight
        }

        // pick a random N between [0, total)
        int r = (int) (Math.random() * total);

        // find which lane the random N falls into
        int cumulative = 0;
        for (int i = 0; i < MAX_LANES; i++) {
            // iterate through lanes, adding up weights until we exceed the
            // random N
            cumulative += (MAX_LANES - laneCounts[i]);
            if (r < cumulative) {
                laneCounts[i]++;
                return i;
            }
        }

        // fallback, should never reach here
        return (int) (Math.random() * MAX_LANES);
    }

    /**
     * Generates a new word entity/entites and adds it to the current word list.
     * Ensured that the generated word(s) is not already in the current word list.
     */
    public void addNewEntites(int count) {
        Set<String> existing = new HashSet<>(entities.stream()
                .map(wordEntity -> wordEntity.word)
                .toList());

        List<String> candidates = new ArrayList<>();
        for (String word : possibleWordList) {
            if (!existing.contains(word)) {
                candidates.add(word);
            }
        }

        Collections.shuffle(candidates);
        int limit = Math.min(count, candidates.size());
        for (int i = 0; i < limit; i++) {
            String word = candidates.get(i);

            entities.add(new WordEntity(
                    word,
                    // random x position off the right edge
                    1000f + (float) Math.random() * 200f,
                    // calculate speed based on word length, longer words move slower
                    10f + (maxWordLength - word.length()) * 10f
                    // random variation for fun
                            + (float) Math.random() * 20f - 10f,
                    selectLaneByWeight()));
        }

        if (limit < count) {
            System.out.println("Warning: requested " + count
                    + " words, but only " + limit + " unique words are available.");
        }
    }

    /**
     * Checks if any word in the current word list matches the current input
     * buffer (prefixes), if so, removes the matched word and generates a new
     * one. Also handles clearing the input buffer if necessary.
     */
    public void checkAndRemoveMatchedWord() {
        Vector<String> matchedWords = new Vector<>();
        boolean keepInputBuffer = false;

        for (WordEntity e : entities) {
            for (String suffix : inputBufferSuffix) {
                String word = e.word;
                if (word.equals(suffix)) {
                    matchedWords.add(word);

                    // if there's another word that starts with the same prefix,
                    // keep the input buffer instead of clearing it
                    // e.g. [final] [final]ly -> keep "final"
                    // [switch] [ch]ar -> clear input buffer
                    // [volatile] [e]xtends -> clear input buffer
                    for (WordEntity other : entities) {
                        String otherWord = other.word;
                        if (!otherWord.equals(word) && otherWord.startsWith(word)) {
                            keepInputBuffer = true;
                            break;
                        }
                    }

                    break;
                }
            }
        }

        // remove matched words and generate new ones
        for (String matchedWord : matchedWords) {
            entities.removeIf(wordEntity -> wordEntity.word.equals(matchedWord));
            addNewEntites(1);
            for (WordEntitiesListener l : listeners) {
                l.onWordCompleted(matchedWord);
            }
        }

        // clear the input buffer if a word was matched and there's no other
        // word that can be matched with the current input buffer
        if (!keepInputBuffer && matchedWords.size() > 0) {
            clearInputBuffer();
        }
    }

    // ========================================================================
    // Utility methods for word entites/buffer

    public void clearWordList() {
        entities.clear();
    }

    public void clearInputBuffer() {
        inputBuffer = "";
        inputBufferSuffix.clear();
        for (WordEntity wordEntity : entities) {
            wordEntity.progress = 0;
        }
    }

    public void addInputChar(char c) {
        // circular buffer with max length equal to the longest word in the
        // possible word list
        if (inputBuffer.length() < maxWordLength) {
            inputBuffer += c;
        } else {
            inputBuffer = inputBuffer.substring(1) + c;
        }

        // recalculate the input buffer prefixes
        // c -> "c" -> [c]
        // o -> "co" -> [co, o]
        // l -> "col" -> [col, ol, l]
        inputBufferSuffix.clear();
        for (int i = 0; i <= inputBuffer.length() - 1; i++) {
            inputBufferSuffix.add(inputBuffer.substring(i, inputBuffer.length()));
        }
    }

    public void removeInputChar() {
        if (inputBuffer.length() > 0) {
            inputBuffer = inputBuffer.substring(0, inputBuffer.length() - 1);

            // recalculate the input buffer prefixes
            inputBufferSuffix.clear();
            for (int i = 0; i <= inputBuffer.length() - 1; i++) {
                inputBufferSuffix.add(inputBuffer.substring(i, inputBuffer.length()));
            }
        }
    }

}
