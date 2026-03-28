package game.overrun.stage;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import engine.Engine;
import engine.Scene;
import engine.entities.Collidable;
import engine.entities.CollisionManager;
import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.Texture;
import engine.math.Vec2;
import engine.ui.Button;
import game.data.Item;
import game.data.ItemType;
import game.data.PlayerData;
import game.data.PlayerDataSaver;
import game.overrun.InputHandler;
import game.overrun.PlayerManager;
import game.overrun.projectiles.ProjectileManager;
import game.overrun.words.WordEntitiesManager;
import game.overrun.words.WordEntity;

// TODO: Make enemy texture configurable per stage.
public class Stage extends Scene {
    protected final StageConfig config;
    protected float timer = 0f;

    protected Texture solidTexture;
    protected Texture backgroundTexture;
    protected Texture playerTexture;
    public Color playerColor = Color.WHITE;

    protected FontAtlas font;

    protected CollisionManager collision;
    public PlayerManager playerManager;
    public ProjectileManager projectiles;
    public WordEntitiesManager words;
    protected InputHandler inputHandler;

    protected Button pauseButton;
    protected Button exitButton;
    protected Button okButton;

    protected boolean debug = false;
    protected boolean isPaused = false;
    protected boolean deathRewardsGranted = false;
    // Manual spawn stages related fields
    protected boolean isStageWon = false;
    protected boolean winRewardsGranted = false;
    protected boolean isBossSpawned = false;
    protected boolean isBossDefeated = false;
    protected float timeSinceAllPhasesCleared = -1f; // -1 means not started
    private static final float BOSS_SPAWN_DELAY = 2f; // seconds after all phases cleared before boss spawns

    public Stage() {
        this(StageConfigs.STAGE_1);
    }

    public Stage(StageConfig config) {
        this.config = config;
    }

    @Override
    public void preloadAssets() {
        super.preloadAssets();
        Texture.preloadAsync(
                StageConfigs.getSolidTexturePath(),
                config.backgroundTexturePath(),
                getSelectedPlayerTexturePath(),
                StageConfigs.getButtonTexturePath());
        Texture.preloadAsync(config.allEntityTexturePaths());
    }

    @Override
    public void init(int width, int height) {
        Engine.audio.stopSound(config.soundToStopOnInit());

        solidTexture = new Texture(StageConfigs.getSolidTexturePath());
        backgroundTexture = new Texture(config.backgroundTexturePath());
        playerTexture = new Texture(getSelectedPlayerTexturePath());

        font = new FontAtlas(config.fontPath(), config.fontSize());

        collision = new CollisionManager();
        playerManager = new PlayerManager(words);
        projectiles = new ProjectileManager();
        projectiles.playerManager = playerManager;

        words = new WordEntitiesManager(this.config, this.config.manualSpawn());
        words.init();
        words.addListener(playerManager);
        words.addListener(projectiles);

        if (config.manualSpawn()) {
            for (SpawnPhase phase : config.spawnPhases()) {
                phase.spawned = false;
            }
        }

        inputHandler = new InputHandler(words);

        pauseButton = new Button(
                super.layout.topLeft(100, 50),
                new Vec2(100, 50),
                "Pause",
                new Texture(StageConfigs.getButtonTexturePath()));

        pauseButton.setOnClick(() -> {
            isPaused = !isPaused;
            if (isPaused) {
                System.out.println("Game Paused");
            } else {
                System.out.println("Game Resumed");
            }
        });

        exitButton = new Button(
                super.layout.center(0, 0),
                new Vec2(200, 50),
                "Exit Game",
                new Texture(StageConfigs.getButtonTexturePath()));

        exitButton.setOnClick(() -> {
            Engine.setScene(new game.menu.Main());
        });

        okButton = new Button(
                super.layout.center(0, 100),
                new Vec2(240, 50),
                "Ok",
                new Texture(StageConfigs.getButtonTexturePath()));

        okButton.setOnClick(() -> {
            Engine.setScene(new game.menu.mode.Mode());
        });
    }

    @Override
    public void update(float delta) {
        timer += delta;

        if (Engine.input.isKeyPressed(GLFW_KEY_F3)) {
            debug = !debug;
        }

        ItemType equippedItem1 = PlayerData.equippedItems[0];
        if (equippedItem1 != null) {
            Item item1 = PlayerData.getItemByType(equippedItem1);
            if (item1 != null) {
                if (!isPaused)
                    item1.update(delta);
                if (Engine.input.isKeyPressed(GLFW_KEY_1) && item1.canActivate()) {
                    System.out.println(
                            "Activating Item in Slot 1: "
                                    + (equippedItem1 != null ? equippedItem1.displayName() : "None"));
                    item1.activate(this);
                }
            }
        }
        ItemType equippedItem2 = PlayerData.equippedItems[1];
        if (equippedItem2 != null) {
            Item item2 = PlayerData.getItemByType(equippedItem2);
            if (item2 != null) {
                if (!isPaused)
                    item2.update(delta);
                if (Engine.input.isKeyPressed(GLFW_KEY_2) && item2.canActivate()) {
                    System.out.println(
                            "Activating Item in Slot 2: "
                                    + (equippedItem2 != null ? equippedItem2.displayName() : "None"));
                    item2.activate(this);
                }
            }
        }

        if (!isPaused && !playerManager.isDead() && !isStageWon) {
            inputHandler.update();
            words.update(delta);
            projectiles.update(delta);

            ArrayList<Collidable> collidables = new ArrayList<>();
            collidables.addAll(words.getCollidables());
            collidables.addAll(projectiles.getCollidables());
            collision.detectAndDispatch(collidables);

            words.removeInactive();
            projectiles.removeInactive();
        } else {
            if (isPaused) {
                exitButton.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
            } else if (isStageWon) {
                okButton.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
            } else if (playerManager.isDead()) {
                if (!deathRewardsGranted) {
                    // Update player data
                    PlayerData.coins += playerManager.score / 5;
                    deathRewardsGranted = true;
                    PlayerDataSaver.save();
                }

                exitButton.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
            }
        }

        pauseButton.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));

        if (config.manualSpawn() && !isStageWon && !playerManager.isDead()) {
            for (SpawnPhase phase : config.spawnPhases()) {
                if (!phase.spawned && timer >= phase.timerAt) {
                    words.addNewEntites(phase.count);
                    phase.spawned = true;
                }
            }

            boolean allPhasesSpawned = true;
            for (SpawnPhase phase : config.spawnPhases()) {
                if (!phase.spawned) {
                    allPhasesSpawned = false;
                    break;
                }
            }

            // Check if all phases spawned and all words cleared
            if (allPhasesSpawned && words.getWordEntities().isEmpty()) {
                // Start boss spawn timer if not already started
                if (timeSinceAllPhasesCleared < 0f) {
                    timeSinceAllPhasesCleared = 0f;
                    System.out.println("All phases cleared! Boss will spawn in " + BOSS_SPAWN_DELAY + " seconds...");
                }

                // Update boss spawn timer
                timeSinceAllPhasesCleared += delta;

                // Spawn boss after delay
                if (timeSinceAllPhasesCleared >= BOSS_SPAWN_DELAY && !isBossSpawned) {
                    words.addEntity(config.bossWordEntity());
                    isBossSpawned = true;
                    System.out.println("Boss spawned!");
                }

                // Win when boss is defeated (all words cleared including boss)
                if (isBossSpawned && words.getWordEntities().isEmpty()) {
                    if (!winRewardsGranted) {
                        PlayerData.coins += playerManager.score / 5;
                        winRewardsGranted = true;
                        PlayerDataSaver.save();
                    }
                    isStageWon = true;
                }
            }

            if (timer >= config.maxTime() && !deathRewardsGranted) {
                playerManager.hurt(99999);
            }
        }
    }

    @Override
    public void renderWorld(float delta) {
        glClearColor(0.4f, 0.4f, 0.4f, 1.0f);

        super.batch.setColor(Color.WHITE);
        super.batch.draw(backgroundTexture, 0, 0, Engine.width, Engine.height);

        if (debug) {
            for (int i = 0; i < 4; i++) {
                float y = (i - 2) * WordEntity.LANE_SPACING + WordEntity.LANE_Y_OFFSET;
                super.batch.setColor(i % 2 == 0
                        ? new Color(1.0f, 0.0f, 0.0f, 0.2f)
                        : new Color(1.0f, 0.0f, 0.0f, 0.4f));
                super.batch.draw(solidTexture, 0, y, 2000, WordEntity.LANE_HEIGHT);
            }
        }

        super.batch.setColor(Color.WHITE);
        words.render(super.batch, font);
        projectiles.render(super.batch);

        Vec2 playerPos = playerManager.getPosition();
        super.batch.setColor(playerColor);
        super.batch.draw(playerTexture, playerPos.x, playerPos.y, 81f, 100f);

        // Render active item effects (e.g. shield)
        ItemType equippedItem1 = PlayerData.equippedItems[0];
        if (equippedItem1 != null) {
            Item item1 = PlayerData.getItemByType(equippedItem1);
            if (item1 != null) {
                item1.render(this, super.batch);
            }
        }
        ItemType equippedItem2 = PlayerData.equippedItems[1];
        if (equippedItem2 != null) {
            Item item2 = PlayerData.getItemByType(equippedItem2);
            if (item2 != null) {
                item2.render(this, super.batch);
            }
        }
    }

    @Override
    public void renderUI(float delta) {
        pauseButton.render(super.batch, font, mouseScreen);

        float healthBarWidth = Engine.width;
        float healthBarHeight = 40f;
        float healthPercent = (float) playerManager.health / playerManager.maxHealth;
        super.batch.setColor(Color.BLACK);
        super.batch.draw(solidTexture, healthBarWidth / 2f, 0, healthBarWidth, healthBarHeight);
        super.batch.setColor(new Color(1f, 0.41f, 0.71f, 1f));
        super.batch.draw(solidTexture, healthBarWidth / 2f + (healthBarWidth * (healthPercent - 1f) / 2f), 0,
                healthBarWidth * healthPercent, healthBarHeight);
        super.batch.setColor(Color.WHITE);
        font.drawTextUnaligned(super.batch, "Health: " + playerManager.health + "/" + playerManager.maxHealth, 30,
                40, Color.WHITE, 16);

        // draw item icons and cooldowns at bottom center
        for (int i = 0; i < PlayerData.equippedItems.length; i++) {
            // draw background for item slot
            super.batch.setColor(new Color(0f, 0f, 0f, 0.5f));
            float slotSize = 64f;
            float slotSpacing = 20f;
            float totalWidth = PlayerData.equippedItems.length * slotSize
                    + (PlayerData.equippedItems.length - 1) * slotSpacing;
            float startX = (Engine.width - totalWidth) / 2f;
            float x = startX + i * (slotSize + slotSpacing);
            float y = Engine.height - slotSize - 20f;
            super.batch.draw(solidTexture,
                    x + slotSize / 2f,
                    y + slotSize / 2f,
                    slotSize, slotSize);

            ItemType equippedItem = PlayerData.equippedItems[i];
            if (equippedItem != null) {
                Item item = PlayerData.getItemByType(equippedItem);
                if (item != null && item.icon != null) {
                    // draw item icon
                    super.batch.setColor(Color.WHITE);
                    super.batch.draw(item.icon,
                            x + slotSize / 2f,
                            y + slotSize / 2f,
                            slotSize, slotSize);

                    // draw cooldown overlay from bottom to top
                    float cooldownPercent = item.getCooldownTime() / item.getCooldownDuration();
                    if (cooldownPercent > 0f) {
                        super.batch.setColor(new Color(0f, 0f, 0f, 0.5f));
                        super.batch.draw(solidTexture,
                                x + slotSize / 2f,
                                y + slotSize / 2f - slotSize * (1f - cooldownPercent) / 2f,
                                slotSize, slotSize * cooldownPercent);
                    }
                }
            }

            // draw slot number key
            font.drawTextUnaligned(super.batch,
                    String.valueOf(i + 1),
                    x + slotSize - 15f,
                    y + slotSize - 10f,
                    Color.WHITE, 12);
        }

        if (isPaused) {
            super.batch.setColor(new Color(0f, 0f, 0f, 0.5f));
            super.batch.draw(solidTexture, Engine.width * 0.5f, Engine.height * 0.5f, Engine.width, Engine.height);
            super.batch.setColor(Color.WHITE);

            exitButton.render(super.batch, font, mouseScreen);

            Vec2 pauseTextPos = super.layout.center(0, -100);
            font.drawTextAligned(super.batch, "PAUSED", pauseTextPos.x, pauseTextPos.y, Color.WHITE, 64);
        } else if (isStageWon) {
            super.batch.setColor(new Color(0f, 0f, 0f, 0.5f));
            super.batch.draw(solidTexture, Engine.width * 0.5f, Engine.height * 0.5f, Engine.width, Engine.height);
            super.batch.setColor(Color.WHITE);

            Vec2 stageClearTextPos = super.layout.center(0, -100);
            font.drawTextAligned(super.batch, "Stage Clear!", stageClearTextPos.x, stageClearTextPos.y, Color.WHITE,
                    64);
            font.drawTextAligned(super.batch, "Reward: +" + (playerManager.score / 5) + " coins",
                    Engine.width * 0.5f,
                    Engine.height * 0.5f - 20f, Color.WHITE, 22);

            okButton.render(super.batch, font, mouseScreen);
        } else if (playerManager.isDead()) {
            super.batch.setColor(new Color(0f, 0f, 0f, 0.5f));
            super.batch.draw(solidTexture, Engine.width * 0.5f, Engine.height * 0.5f, Engine.width, Engine.height);
            super.batch.setColor(Color.WHITE);

            Vec2 gameOverTextPos = super.layout.center(0, -100);
            font.drawTextAligned(super.batch, "Game Over", gameOverTextPos.x, gameOverTextPos.y, Color.WHITE, 64);
            exitButton.render(super.batch, font, mouseScreen);
        }

        if (debug) {
            font.drawTextUnaligned(super.batch,
                    String.format("Mouse World: (%.2f, %.2f)", mouseWorld.x, mouseWorld.y), 20, 20, Color.BLACK, 16);
            font.drawTextUnaligned(super.batch,
                    String.format("Mouse Screen: (%.2f, %.2f)", mouseScreen.x, mouseScreen.y), 20, 50, Color.BLACK,
                    16);
        }
    }

    @Override
    public void cleanup() {
        solidTexture.cleanup();
        backgroundTexture.cleanup();
        playerTexture.cleanup();
    }

    protected String getSelectedPlayerTexturePath() {
        String[] texturePaths = StageConfigs.getPlayerTexturePaths();
        int index = PlayerData.selectedCharacter;
        if (index < 0 || index >= texturePaths.length) {
            index = 0;
        }
        return texturePaths[index];
    }
}