package com.project.words;

public interface WordEntitiesListener {
    /**
     * Called when a word entity is "completed".
     */
    void onWordCompleted(String word);

    // TODO Callback on misspell
}
