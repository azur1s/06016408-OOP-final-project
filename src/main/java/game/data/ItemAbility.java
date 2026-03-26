package game.data;

import game.overrun.words.WordEntitiesManager;

public interface ItemAbility {
    /**
     * Activate the item's ability. This will be called when the player uses the
     * item during gameplay.
     *
     * @param words The WordEntitiesManager instance, can be used to interact
     *              with the words on the screen.
     */
    void activate(WordEntitiesManager words);
}
