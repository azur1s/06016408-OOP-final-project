package game.overrun.stage;

import engine.Engine;

public class StageContext {
    public static StageContext defaultContext() {
        return new StageContext();
    }

    public void exitToMainMenu() {
        Engine.setScene(new game.menu.Main());
    }
}