package com.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Words {
    public static String[] possibleWordList;
    private static int maxWordLength = 0;

    Vector<String> currentWordList = new Vector<>();

    String inputBuffer = "";
    Vector<String> inputBufferPrefixes = new Vector<>();

    public static void init() {
        FileHandle file = Gdx.files.internal("words.txt");

        possibleWordList = java.util.Arrays.stream(file.readString().split("\n"))
                .filter(line -> {
                    boolean isValid = !line.isEmpty() && !line.startsWith("#");
                    // also calculate the max word length
                    if (isValid && line.length() > maxWordLength)
                        maxWordLength = line.length();
                    return isValid;
                })
                .toArray(String[]::new);

        System.out.println(
                "Loaded " + possibleWordList.length
                        + " words from " + file.path());
    }

    public void clearWordList() {
        currentWordList.clear();
    }

    public void clearInputBuffer() {
        inputBuffer = "";
        inputBufferPrefixes.clear();
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
        inputBufferPrefixes.clear();
        for (int i = 0; i <= inputBuffer.length() - 1; i++) {
            inputBufferPrefixes.add(inputBuffer.substring(i, inputBuffer.length()));
        }
    }

    public void removeInputChar() {
        if (inputBuffer.length() > 0) {
            inputBuffer = inputBuffer.substring(0, inputBuffer.length() - 1);

            // recalculate the input buffer prefixes
            inputBufferPrefixes.clear();
            for (int i = 0; i <= inputBuffer.length() - 1; i++) {
                inputBufferPrefixes.add(inputBuffer.substring(i, inputBuffer.length()));
            }
        }
    }

    /**
     * Generates a random word list that doesn't contain duplicates.
     */
    public void generateWordList(int count) {
        Set<String> existing = new HashSet<>(currentWordList);

        List<String> candidates = new ArrayList<>();
        for (String word : possibleWordList) {
            if (!existing.contains(word)) {
                candidates.add(word);
            }
        }

        Collections.shuffle(candidates);
        int limit = Math.min(count, candidates.size());
        currentWordList.addAll(candidates.subList(0, limit));

        if (limit < count) {
            System.out.println("Warning: requested " + count
                    + " words, but only " + limit + " unique words are available.");
        }
    }

    public Vector<Pair<String, String>> getCurrentWordListHighlighted() {
        // for each word, split into highlighted and non-highlighted parts
        // based on the longest matching prefix
        // e.g. input buffer = "volati", words = [volatile, if, import]
        // -> ("volati", "le"), ("i", "f"), ("i", "mport")
        Vector<Pair<String, String>> highlightedWords = new Vector<>();

        for (String word : currentWordList) {
            String highlightedPart = "";
            String nonHighlightedPart = word;

            for (String prefix : inputBufferPrefixes) {
                if (word.startsWith(prefix)
                        && prefix.length() > highlightedPart.length()) {
                    highlightedPart = prefix;
                    nonHighlightedPart = word.substring(prefix.length());
                }
            }

            highlightedWords.add(new Pair<>(highlightedPart, nonHighlightedPart));
        }

        return highlightedWords;
    }

    public void checkAndRemoveMatchedWord() {
        Vector<String> matchedWords = new Vector<>();
        boolean keepInputBuffer = false;

        for (String word : currentWordList) {
            prefixLoop: for (String prefix : inputBufferPrefixes) {
                if (word.equals(prefix)) {
                    matchedWords.add(word);

                    // if there's another word that starts with the same prefix,
                    // keep the input buffer instead of clearing it
                    // e.g. [final] [final]ly -> keep "final"
                    // [switch] [ch]ar -> clear input buffer
                    // [volatile] [e]xtends -> clear input buffer
                    for (String otherWord : currentWordList) {
                        if (!otherWord.equals(word) && otherWord.startsWith(word)) {
                            keepInputBuffer = true;
                            break;
                        }
                    }

                    break prefixLoop;
                }
            }
        }

        for (String matchedWord : matchedWords) {
            currentWordList.remove(matchedWord);
            generateWordList(1);
        }

        // clear the input buffer if a word was matched and there's no other
        // word that can be matched with the current input buffer
        if (!keepInputBuffer && matchedWords.size() > 0) {
            clearInputBuffer();
        }
    }
}
