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
            data.applyToPlayerData();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load player data from " + SAVE_FILE, e);
        }
    }

    private static class SaveData {
        int coins;
        int selectedCharacter;
        boolean[] unlockedItems;
        boolean[] unlockedSkins;
        int[] equippedItems;
        int[][] itemStatsLevels;

        static SaveData fromPlayerData() {
            SaveData data = new SaveData();
            data.coins = PlayerData.coins;
            data.selectedCharacter = PlayerData.selectedCharacter;
            data.unlockedItems = Arrays.copyOf(PlayerData.unlockedItems, PlayerData.unlockedItems.length);
            data.unlockedSkins = Arrays.copyOf(PlayerData.unlockedSkins, PlayerData.unlockedSkins.length);
            data.equippedItems = Arrays.copyOf(PlayerData.equippedItems, PlayerData.equippedItems.length);

            data.itemStatsLevels = new int[PlayerData.itemStatsLevels.length][];
            for (int i = 0; i < PlayerData.itemStatsLevels.length; i++) {
                data.itemStatsLevels[i] = Arrays.copyOf(PlayerData.itemStatsLevels[i],
                        PlayerData.itemStatsLevels[i].length);
            }
            return data;
        }

        void applyToPlayerData() {
            PlayerData.coins = coins;
            if (selectedCharacter >= 0 && selectedCharacter <= 2) {
                PlayerData.selectedCharacter = selectedCharacter;
            }

            if (unlockedItems != null && unlockedItems.length == PlayerData.unlockedItems.length) {
                PlayerData.unlockedItems = Arrays.copyOf(unlockedItems, unlockedItems.length);
            }
            if (unlockedSkins != null && unlockedSkins.length == PlayerData.unlockedSkins.length) {
                PlayerData.unlockedSkins = Arrays.copyOf(unlockedSkins, unlockedSkins.length);
            }
            if (equippedItems != null && equippedItems.length == PlayerData.equippedItems.length) {
                PlayerData.equippedItems = Arrays.copyOf(equippedItems, equippedItems.length);
            }
            if (itemStatsLevels != null && itemStatsLevels.length == PlayerData.itemStatsLevels.length) {
                int[][] copied = new int[itemStatsLevels.length][];
                boolean shapeValid = true;
                for (int i = 0; i < itemStatsLevels.length; i++) {
                    if (itemStatsLevels[i] == null
                            || itemStatsLevels[i].length != PlayerData.itemStatsLevels[i].length) {
                        shapeValid = false;
                        break;
                    }
                    copied[i] = Arrays.copyOf(itemStatsLevels[i], itemStatsLevels[i].length);
                }
                if (shapeValid) {
                    PlayerData.itemStatsLevels = copied;
                }
            }
        }
    }

}
