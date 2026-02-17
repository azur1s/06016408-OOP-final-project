package com.project.words;

public interface WordEntitiesListener {
    /**
     * Called when a word entity is "completed".
     */
    void onWordCompleted(WordEntity wordEntity);

    /**
     * Called when a word entity reaches the left edge of the screen without being
     * completed.
     */
    void onWordMissed(WordEntity wordEntity);
}
