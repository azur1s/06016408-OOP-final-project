package com.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import com.badlogic.gdx.files.FileHandle;

public class Words {
    public static String[] possibleWordList;

    Vector<String> currentWordList = new Vector<>();

    String inputBuffer = "";
    Vector<String> inputBufferPrefixes = new Vector<>();

    public static void init() {
        FileHandle file = new FileHandle("words.txt");
        possibleWordList = java.util.Arrays.stream(file.readString().split("\n"))
            .filter(line -> !line.isEmpty() && !line.startsWith("#"))
            .toArray(String[]::new);
        System.out.println("Loaded " + possibleWordList.length + " words from " + file.path());
    }

    public void clearWordList() {
        currentWordList.clear();
    }

    public void clearInputBuffer() {
        inputBuffer = "";
    }

    public void addInputChar(char c) {
        inputBuffer += c;

        // recalculate the input buffer prefixes
        // c -> "c"   -> [c]
        // o -> "co"  -> [co, o]
        // l -> "col" -> [col, ol, l]
        inputBufferPrefixes.clear();
        for (int i = 0; i <= inputBuffer.length() - 1; i++) {
            inputBufferPrefixes.add(inputBuffer.substring(i, inputBuffer.length()));
        }

        System.out.println("Input buffer: " + inputBuffer);
        System.out.println("Input buffer prefixes: " + inputBufferPrefixes);
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
            + " words, but only " + limit + " unique words are available."
            );
        }
    }

    public Vector<Pair<String, String>> getCurrentWordListHighlighted() {
        // and for each word, split into highlighted and non-highlighted parts
        // based on the longest matching prefix
        // e.g. input buffer = "volati", words = [volatile, if, import]
        // -> ("volati", "le"), ("i", "f"), ("i", "mport")
        Vector<Pair<String, String>> highlightedWords = new Vector<>();

        for (String word : currentWordList) {
            String highlightedPart = "";
            String nonHighlightedPart = word;

            for (String prefix : inputBufferPrefixes) {
                if (word.startsWith(prefix) && prefix.length() > highlightedPart.length()) {
                    highlightedPart = prefix;
                    nonHighlightedPart = word.substring(prefix.length());
                }
            }

            highlightedWords.add(new Pair<>(highlightedPart, nonHighlightedPart));
        }

        return highlightedWords;
    }

    /**
     * Checks whether the input buffer can't be matched to any of the current
     * words.
     */
    public boolean isInputBufferMatchable() {
        for (String word : currentWordList) {
            if (word.startsWith(inputBuffer)) {
                return true;
            }
        }
        return false;
    }

    public void checkAndRemoveMatchedWord() {
        for (String word : currentWordList) {
            for (String prefix : inputBufferPrefixes) {
                if (word.equals(prefix)) {
                    removeWord(word);
                    generateWordList(1);

                    if (isInputBufferMatchable()) {
                        // if the buffer can still be matched, keep it as is so the
                        // user can continue typing the next word
                    } else {
                        // otherwise, clear the buffer so the user can start fresh
                        // on the next word
                        clearInputBuffer();
                    }

                    return;
                }
            }
        }
    }

    public void removeWord(String word) {
        currentWordList.remove(word);
    }
}
