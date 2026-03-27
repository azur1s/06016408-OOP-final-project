package game.data;

import engine.graphics.TextureBatch;
import game.overrun.stage.Stage;

public interface ItemAbility {
    /**
     * Activate the item's ability. This will be called when the player uses the
     * item during gameplay.
     *
     * @param words The WordEntitiesManager instance, can be used to interact
     *              with the words on the screen.
     * @param stage The current Stage instance, can be used to access player and
     *              enemy information for ability effects.
     */
    void activate(Stage stage);

    /**
     * Render any visual effects related to the item.
     *
     * @param stage The current Stage instance, can be used to access player and
     *              enemy information for rendering purposes.
     * @param batch The TextureBatch used for rendering
     */
    void render(Stage stage, TextureBatch batch);
}
