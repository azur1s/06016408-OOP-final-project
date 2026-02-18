package com.project;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable {
    private static final String BACKGROUND_PATH = "test_background.png";
    private static final String SHOOTER_PATH = "test_shooter.png";
    private static final String SOLID_PATH = "white.png";

    private final AssetManager manager;
    public final Texture background;
    public final Texture shooter;
    public final Texture solid;

    public Assets() {
        manager = new AssetManager();
        manager.load(BACKGROUND_PATH, Texture.class);
        manager.load(SHOOTER_PATH, Texture.class);
        manager.load(SOLID_PATH, Texture.class);
        manager.finishLoading();

        background = manager.get(BACKGROUND_PATH, Texture.class);
        shooter = manager.get(SHOOTER_PATH, Texture.class);
        solid = manager.get(SOLID_PATH, Texture.class);
    }

    @Override
    public void dispose() {
        manager.dispose();
    }
}
