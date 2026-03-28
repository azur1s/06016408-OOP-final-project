package game.overrun.words;

import engine.Engine;
import engine.entities.Collidable;
import engine.entities.CollisionLayer;
import engine.entities.Entity;
import engine.graphics.AnimationClip;
import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.TextureBatch;
import engine.math.Vec2;

public class WordEntity extends Entity {
    // TODO move all this to WordEntitiesManager
    public static final float LANE_Y_OFFSET = -5f;
    public static final float LANE_HEIGHT = 100f;
    public static final float LANE_GAP = 20f;
    public static final float LANE_SPACING = LANE_HEIGHT + LANE_GAP;

    public Color color = Color.WHITE;
    public AnimationClip clip;

    public String word;
    public int progress = 0; // How many characters have been correctly typed
    public WordEffect effect = new WordEffect.Normal();
    public boolean missedLeftEdge = false; // Whether the word has moved past the left edge without being fully typed

    public float speed;
    /**
     * The lane the word entity is in [0 - 3].
     */
    public int lane;

    public WordEntity(AnimationClip clip, String word, float xPosition, float speed, int lane) {
        // lane 0 is top, lane 3 is bottom
        super(new Vec2(xPosition, (lane - 2) * LANE_SPACING + LANE_Y_OFFSET),
                new Vec2(74f, 74f));
        this.clip = clip;
        this.word = word;
        this.speed = speed;
        this.lane = lane;
    }

    @Override
    public int getLayer() {
        return CollisionLayer.ENEMY;
    }

    @Override
    public int getMask() {
        return CollisionLayer.PLAYER_PROJECTILE;
    }

    @Override
    public void onCollision(Collidable other) {
        this.active = false;
    }

    public void update(float delta) {
        move(new Vec2(-this.speed, 0f), delta);
    }

    public void render(TextureBatch batch, FontAtlas font) {
        batch.setColor(this.color);
        batch.draw(clip.getFrame(Engine.graphics.getTime()), position.x, position.y - 8f, size.x, size.y);

        Color previewColor = new Color(0.75f, 0.75f, 0.75f, 1f);
        Color typedColor = Color.WHITE;

        // Shift the text up so it's above the texture
        Vec2 pos = position.add(new Vec2(0f, 30f));

        float fontSize = 18f;

        if (effect instanceof WordEffect.Hidden hiddenEffect) {

            // Hide last N characters based on the effect, and only show it when
            // progress reaches N
            // e.g. if word is "example" and count is 2, display "examp__"
            // and when user types "examp", display "exampl_" and so on
            int hiddenCount = hiddenEffect.count();
            int visibleCount = Math.max(0, word.length() - hiddenCount);

            String hiddenPart = "";
            // Randomly generate characters to hide the word
            for (int i = 0; i < hiddenCount; i++) {
                hiddenPart += (char) ('a' + (int) (Math.random() * 26));
            }

            if (progress < visibleCount) {
                font.drawTextHorizontalAligned(batch,
                        word.substring(0, visibleCount) + hiddenPart,
                        pos.x, pos.y, previewColor, fontSize);
            } else {
                font.drawTextHorizontalAligned(batch, word, pos.x, pos.y, previewColor, fontSize);
            }

        } else if (effect instanceof WordEffect.Repeat repeatEffect) {
            // Display the word with a multiplier based on how many times it needs to be
            // repeated, e.g. if count is 3, display "example³"
            Vec2 wordSize = font.measure(word, fontSize);
            font.drawTextHorizontalAligned(batch, word, pos.x, pos.y, previewColor, fontSize);

            // Draw the multiplier in the top right corner of the word
            String multiplier = "x" + (1 + repeatEffect.count() - repeatEffect.typedCount());

            font.drawTextHorizontalAligned(batch, multiplier, pos.x + (wordSize.x / 2f) + 12f, pos.y + wordSize.y / 2f,
                    typedColor, 12);
        }

        // Normal variant
        else {
            font.drawTextHorizontalAligned(batch, word, pos.x, pos.y, previewColor, fontSize);
        }

        // Draw the typed part of the word on top, shifted to the left so it overlaps
        // with the preview text
        if (progress > 0) {
            float typedWidth = font.getTextWidth(word.substring(0, progress), fontSize);
            font.drawTextHorizontalAligned(batch, word.substring(0, progress),
                    pos.x - (font.getTextWidth(word, fontSize) / 2f) + (typedWidth / 2f), pos.y, typedColor, fontSize);
        }
    }

    public void setEffect(WordEffect effect) {
        this.effect = effect;
    }
}
