package game.data;

import java.io.Serializable;

import engine.graphics.Texture;
import engine.graphics.TextureBatch;
import game.overrun.stage.Stage;

public class Item implements ItemAbility, Serializable {
    public boolean unlocked = false;

    public Texture icon;

    public String name;
    public String description;

    public int cooldownLevel = 0;
    public int damageLevel = 0;
    public int durationLevel = 0;

    protected float cooldownTime = 0f;
    protected float activeTime = 0f;

    protected boolean active = false;

    public void update(float deltaTime) {
        if (cooldownTime > 0f) {
            cooldownTime -= deltaTime;
            if (cooldownTime < 0f) {
                cooldownTime = 0f;
            }
        }

        if (activeTime > 0f) {
            activeTime -= deltaTime;
            if (activeTime < 0f) {
                activeTime = 0f;
            }
        }
    }

    public boolean canActivate() {
        return cooldownTime <= 0f && activeTime <= 0f;
    }

    public float getCooldownTime() {
        return cooldownTime;
    }

    public float getActiveTime() {
        return activeTime;
    }

    public float getCooldownDuration() {
        return this.getDuration() + Math.max(0.1f, 5f - cooldownLevel * 0.5f);
    }

    public float getDuration() {
        return 3f + durationLevel * 2f;
    }

    public void setTimer() {
        this.activeTime = this.getDuration();
        this.cooldownTime = this.getCooldownDuration();
    }

    @Override
    public void activate(Stage stage) {
    }

    @Override
    public void render(Stage stage, TextureBatch batch) {
    }
}
