package game.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PlayerDataSaver {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // Windows: C:\Users\Username\Documents\project\save\playerdata.json
    // macOS/Linux: /home/username/Documents/project/save/playerdata.json
    private static final Path SAVE_FILE = Paths.get(
            System.getProperty("user.home"),
            "Documents",
            "project",
            "save",
            "playerdata.json");

    public static void save() {
        SaveData snapshot = SaveData.fromPlayerData();
        try {
            Files.createDirectories(SAVE_FILE.getParent());
            String json = GSON.toJson(snapshot);
            Files.writeString(SAVE_FILE, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save player data to " + SAVE_FILE, e);
        }
    }

    public static void load() {
        if (!Files.exists(SAVE_FILE)) {
            return;
        }

        try {
            String json = Files.readString(SAVE_FILE, StandardCharsets.UTF_8);
            SaveData data = GSON.fromJson(json, SaveData.class);
            if (data == null) {
                return;
            }
            // Check if data is valid
            if (data.items == null || data.equippedItems == null) {
                return;
            }
            data.applyToPlayerData();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load player data from " + SAVE_FILE, e);
        }
    }

    private static class SaveData {
        int coins;
        int selectedCharacter;
        Item[] items;
        int[] equippedItems;

        static SaveData fromPlayerData() {
            SaveData data = new SaveData();
            data.coins = PlayerData.coins;
            data.selectedCharacter = PlayerData.selectedCharacter;

            // Deep copy items
            data.items = new Item[PlayerData.items.length];
            for (int i = 0; i < PlayerData.items.length; i++) {
                Item item = PlayerData.items[i];
                if (item != null) {
                    Item itemCopy = new Item();
                    itemCopy.unlocked = item.unlocked;
                    itemCopy.name = item.name;
                    itemCopy.description = item.description;
                    itemCopy.cooldownLevel = item.cooldownLevel;
                    itemCopy.damageLevel = item.damageLevel;
                    itemCopy.durationLevel = item.durationLevel;
                    data.items[i] = itemCopy;
                } else {
                    data.items[i] = null;
                }
            }

            data.equippedItems = Arrays.copyOf(PlayerData.equippedItems, PlayerData.equippedItems.length);

            return data;
        }

        void applyToPlayerData() {
            PlayerData.coins = this.coins;
            PlayerData.selectedCharacter = this.selectedCharacter;

            for (int i = 0; i < this.items.length; i++) {
                Item item = this.items[i];
                if (item != null) {
                    Item itemCopy = new Item();
                    itemCopy.unlocked = item.unlocked;
                    itemCopy.name = item.name;
                    itemCopy.description = item.description;
                    itemCopy.cooldownLevel = item.cooldownLevel;
                    itemCopy.damageLevel = item.damageLevel;
                    itemCopy.durationLevel = item.durationLevel;
                    PlayerData.items[i] = itemCopy;
                } else {
                    PlayerData.items[i] = null;
                }
            }

            PlayerData.equippedItems = Arrays.copyOf(this.equippedItems, this.equippedItems.length);
        }
    }

}
