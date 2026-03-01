package com.project.scenes.game.words;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.Texture;
import com.project.engine.graphics.TextureBatch;
import com.project.engine.utils.Resources;

public class WordEntitiesManager {
    public static final int MAX_LANES = 5;
    public static final float SPAWN_INTERVAL = 6f; // s between spawning new words
    public static final float LANE_COOLDOWN = 0.5f; // s cooldown for a lane after spawning a word

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
    public float[] laneCooldowns = new float[MAX_LANES];
    public float spawnCooldown = 0f;
    // Accumulated difficulty ramp that increases the speed of generated words over
    // time
    public float difficultyRamp = 0f;

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

    Texture wordTexture;

    // ========================================================================

    public void init() {
        String path = "words/words_java.txt";
        String[] lines = Resources.loadResourcesText(path).split("\n");
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
                        + " words from " + path);

        wordTexture = new Texture("textures/test.png");
    }

    public void render(TextureBatch batch, FontAtlas font) {
        for (WordEntity wordEntity : entities) {
            wordEntity.render(batch, font);
        }
    }

    public void update(float delta) {
        // Decrease lane cooldowns every second
        spawnCooldown -= delta;
        if (spawnCooldown <= 0) {
            addNewEntites(1);
            spawnCooldown = SPAWN_INTERVAL - difficultyRamp;
        }

        for (int i = 0; i < MAX_LANES; i++) {
            if (laneCooldowns[i] > 0) {
                laneCooldowns[i] -= delta;
                if (laneCooldowns[i] < 0) {
                    laneCooldowns[i] = 0;
                }
            }
        }

        for (int i = entities.size() - 1; i >= 0; i--) {
            WordEntity wordEntity = entities.get(i);
            wordEntity.update(delta);

            // update word's progress based on the current input buffer
            int previousProgress = wordEntity.progress;
            int newProgress = 0;

            for (String suffix : inputBufferSuffix) {
                String word = wordEntity.word;
                if (word.startsWith(suffix)) {
                    // matched a prefix of the word, set progress to the suffix length
                    newProgress = suffix.length();
                    break;
                }
            }

            if (previousProgress != newProgress) {
                wordEntity.progress = newProgress;
                for (WordEntitiesListener l : listeners) {
                    l.onWordProgress(wordEntity);
                }
            } else {
                wordEntity.progress = newProgress;
            }

            // if the word reaches the left edge of the screen, remove it and generate a new
            // one
            if (wordEntity.position.x < -500f) {
                entities.remove(i);
                addNewEntites(1);
                for (WordEntitiesListener l : listeners) {
                    l.onWordMissed(wordEntity);
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
     * Selects a lane for a new word entity to spawn in based on the lane cooldowns.
     * Lanes with no cooldown are selected, and if there are multiple, one is
     * randomly chosen. If all lanes are on cooldown, a random lane is selected.
     */
    private int selectLane() {
        List<Integer> availableLanes = new ArrayList<>();
        for (int i = 0; i < MAX_LANES; i++) {
            if (laneCooldowns[i] <= 0) {
                availableLanes.add(i);
            }
        }

        int selectedLane;
        if (!availableLanes.isEmpty()) {
            selectedLane = availableLanes.get((int) (Math.random() * availableLanes.size()));
        } else {
            selectedLane = (int) (Math.random() * availableLanes.size());
        }

        // Set cooldown for the selected lane
        laneCooldowns[selectedLane] = SPAWN_INTERVAL;

        return selectedLane;
    }

    /**
     * Generates a list of unique candidate words not already in the current word
     * list.
     */
    private List<String> generateUniqueWordsCandidate(int count) {
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
        return candidates.subList(0, Math.min(count, candidates.size()));
    }

    /**
     * Generates a new word entity/entites and adds it to the current word list.
     * Ensured that the generated word(s) is not already in the current word list.
     */
    public void addNewEntites(int count) {
        List<String> candidates = generateUniqueWordsCandidate(count);
        int limit = candidates.size();

        for (int i = 0; i < limit; i++) {
            String word = candidates.get(i);

            WordEntity entity = new WordEntity(
                    wordTexture,
                    word,
                    // Random x position off the right edge
                    700f,
                    // Calculate speed based on word length, longer words move slower
                    20f + (maxWordLength - word.length()) * 2f
                    // Random variation for fun
                            + (float) Math.random() * 20f - 10f,
                    selectLane());

            WordEffect eff = WordEffect.randomEffect(entity);
            entity.setEffect(eff);

            entities.add(entity);
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
        Vector<WordEntity> matchedEntities = new Vector<>();
        boolean keepInputBuffer = false;

        for (WordEntity e : entities) {
            for (String suffix : inputBufferSuffix) {
                String word = e.word;
                if (word.equals(suffix)) {
                    matchedEntities.add(e);

                    // If there's another word that starts with the same prefix,
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

        // Remove matched words and generate new ones
        for (WordEntity matchedEntity : matchedEntities) {
            // Check if the matched entity has a repeat effect and is not completed yet
            if (matchedEntity.effect instanceof WordEffect.Repeat repeatEffect) {
                if (!repeatEffect.isCompleted()) {
                    // If not completed, increment the typed count and skip removing it
                    matchedEntity.setEffect(repeatEffect.incrementTypedCount());
                    continue;
                }
            }
            // Otherwise, remove the matched entity
            entities.remove(matchedEntity);
            // Decrease the spawn timer
            spawnCooldown -= 0.5f;
            difficultyRamp += 0.1f;
            // Cap the difficulty ramp
            difficultyRamp = Math.min(difficultyRamp, SPAWN_INTERVAL - (SPAWN_INTERVAL / 2f));
            for (WordEntitiesListener l : listeners) {
                l.onWordCompleted(matchedEntity);
            }
        }

        // Clear the input buffer if a word was matched and there's no other
        // word that can be matched with the current input buffer
        if (!keepInputBuffer && matchedEntities.size() > 0) {
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
        // Circular buffer with max length equal to the longest word in the
        // possible word list
        if (inputBuffer.length() < maxWordLength) {
            inputBuffer += c;
        } else {
            inputBuffer = inputBuffer.substring(1) + c;
        }

        // Recalculate the input buffer prefixes
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

            // Recalculate the input buffer prefixes
            inputBufferSuffix.clear();
            for (int i = 0; i <= inputBuffer.length() - 1; i++) {
                inputBufferSuffix.add(inputBuffer.substring(i, inputBuffer.length()));
            }
        }
    }

}
