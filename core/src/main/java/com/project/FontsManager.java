package com.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

public final class FontsManager implements Disposable {
    private final ObjectMap<String, BitmapFont> cache = new ObjectMap<>();

    public BitmapFont get(String internalFontPath, int sizePx) {
        return get(internalFontPath, sizePx, Color.WHITE);
    }

    public BitmapFont get(String internalFontPath, int sizePx, Color color) {
        String key = internalFontPath + "@" + sizePx + "@" + color.toString();
        BitmapFont cached = cache.get(key);
        if (cached != null) {
            return cached;
        }

        FileHandle file = Gdx.files.internal(internalFontPath);
        BitmapFont created;
        if (file.exists()) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(file);
            try {
                FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
                params.size = sizePx;
                params.color = color;
                created = generator.generateFont(params);
            } finally {
                generator.dispose();
            }
        } else {
            created = new BitmapFont();
        }

        cache.put(key, created);
        return created;
    }

    @Override
    public void dispose() {
        for (BitmapFont font : cache.values()) {
            font.dispose();
        }
        cache.clear();
    }
}
