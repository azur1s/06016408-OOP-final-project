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
            if (data.items == null) {
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
        boolean hasCompletedTutorial;
        ItemState[] items;
        Object[] equippedItems;

        private static class ItemState {
            String itemType;
            boolean unlocked;
            String name;
            String description;
            int cooldownLevel;
            int damageLevel;
            int durationLevel;
        }

        static SaveData fromPlayerData() {
            SaveData data = new SaveData();
            data.coins = PlayerData.coins;
            data.selectedCharacter = PlayerData.selectedCharacter;
            data.hasCompletedTutorial = PlayerData.hasCompletedTutorial;

            data.items = new ItemState[PlayerData.items.length];
            for (int i = 0; i < PlayerData.items.length; i++) {
                Item item = PlayerData.items[i];
                if (item != null) {
                    ItemState state = new ItemState();
                    ItemType type = PlayerData.getItemTypeForIndex(i);
                    state.itemType = type == null ? null : type.name();
                    state.unlocked = item.unlocked;
                    state.name = item.name;
                    state.description = item.description;
                    state.cooldownLevel = item.cooldownLevel;
                    state.damageLevel = item.damageLevel;
                    state.durationLevel = item.durationLevel;
                    data.items[i] = state;
                } else {
                    data.items[i] = null;
                }
            }

            data.equippedItems = new Object[PlayerData.equippedItems.length];
            for (int i = 0; i < PlayerData.equippedItems.length; i++) {
                ItemType equippedType = PlayerData.equippedItems[i];
                data.equippedItems[i] = equippedType == null ? null : equippedType.name();
            }

            return data;
        }

        void applyToPlayerData() {
            PlayerData.coins = this.coins;
            PlayerData.selectedCharacter = this.selectedCharacter;
            PlayerData.hasCompletedTutorial = this.hasCompletedTutorial;
            
            PlayerData.items = PlayerData.createDefaultItems();

            int itemCount = Math.min(PlayerData.items.length, this.items.length);
            for (int i = 0; i < itemCount; i++) {
                ItemState item = this.items[i];
                if (item != null) {
                    ItemType itemType = parseItemType(item.itemType, i);
                    Item itemCopy = ItemFactory.create(itemType);
                    itemCopy.unlocked = item.unlocked;
                    if (item.name != null) {
                        itemCopy.name = item.name;
                    }
                    if (item.description != null) {
                        itemCopy.description = item.description;
                    }
                    itemCopy.cooldownLevel = item.cooldownLevel;
                    itemCopy.damageLevel = item.damageLevel;
                    itemCopy.durationLevel = item.durationLevel;
                    PlayerData.items[i] = itemCopy;
                } else {
                    PlayerData.items[i] = null;
                }
            }

            Arrays.fill(PlayerData.equippedItems, null);
            if (this.equippedItems != null) {
                int slots = Math.min(PlayerData.equippedItems.length, this.equippedItems.length);
                for (int i = 0; i < slots; i++) {
                    PlayerData.equippedItems[i] = parseEquippedItemType(this.equippedItems[i]);
                }
            }
        }

        private ItemType parseItemType(String savedType, int index) {
            if (savedType != null) {
                try {
                    return ItemType.valueOf(savedType);
                } catch (IllegalArgumentException ignored) {
                }
            }
            return PlayerData.getItemTypeForIndex(index);
        }

        private ItemType parseEquippedItemType(Object rawValue) {
            if (rawValue == null) {
                return null;
            }

            if (rawValue instanceof Number) {
                int index = ((Number) rawValue).intValue();
                if (index < 0) {
                    return null;
                }
                return PlayerData.getItemTypeForIndex(index);
            }

            if (rawValue instanceof String) {
                String typeName = (String) rawValue;
                if (typeName.isBlank()) {
                    return null;
                }
                try {
                    return ItemType.valueOf(typeName);
                } catch (IllegalArgumentException ignored) {
                    return null;
                }
            }

            return null;
        }
    }

}
