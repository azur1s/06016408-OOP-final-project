package game.data;

public class PlayerData {
    // 99999 starting coins for testing
    public static int coins = 99999;

    // Selected character index (0-2)
    public static int selectedCharacter = -1;

    public static boolean hasCompletedTutorial = false;

    public static final ItemType[] ITEM_CATALOG = {
            ItemType.FREEZE,
            ItemType.SHIELD,
            ItemType.TORNADO,
            ItemType.TORNADO,
            ItemType.TORNADO,
    };

    // Items are still stored by shop slot index for UI compatibility.
    public static Item[] items = createDefaultItems();

    // Equipped item IDs by slot. null means empty.
    public static ItemType[] equippedItems = { null, null };

    // Skins (all locked by default)
    public static boolean[] unlockedSkins = { false, false, false };

    // Unlocked Stages (Stage 1 unlocked by default)
    public static boolean[] unlockedStages = { true, true, true, true };

    // Helper functions for readability
    public static boolean hasEnoughCoins(int cost) {
        return coins >= cost;
    }

    public static void deductCoins(int amount) {
        coins -= amount;
        if (coins < 0)
            coins = 0;
    }

    public static boolean isItemUnlocked(int itemIndex) {
        Item item = game.data.PlayerData.items[itemIndex];
        return item != null && item.unlocked;
    }

    public static int getItemCount() {
        return ITEM_CATALOG.length;
    }

    public static ItemType getItemTypeForIndex(int itemIndex) {
        if (itemIndex < 0 || itemIndex >= ITEM_CATALOG.length) {
            return null;
        }
        return ITEM_CATALOG[itemIndex];
    }

    public static int getItemIndexForType(ItemType itemType) {
        if (itemType == null) {
            return -1;
        }

        for (int i = 0; i < ITEM_CATALOG.length; i++) {
            if (ITEM_CATALOG[i] == itemType) {
                return i;
            }
        }
        return -1;
    }

    public static int getEquippedItemIndex(int slot) {
        if (slot < 0 || slot >= equippedItems.length) {
            return -1;
        }
        return getItemIndexForType(equippedItems[slot]);
    }

    public static Item getItemByType(ItemType itemType) {
        int index = getItemIndexForType(itemType);
        if (index < 0 || index >= items.length) {
            return null;
        }
        return items[index];
    }

    public static Item ensureItemAtIndex(int itemIndex) {
        if (itemIndex < 0 || itemIndex >= items.length) {
            return null;
        }

        if (items[itemIndex] == null) {
            items[itemIndex] = ItemFactory.create(getItemTypeForIndex(itemIndex));
        }
        return items[itemIndex];
    }

    public static Item[] createDefaultItems() {
        Item[] defaults = new Item[ITEM_CATALOG.length];
        for (int i = 0; i < ITEM_CATALOG.length; i++) {
            defaults[i] = ItemFactory.create(ITEM_CATALOG[i]);
        }
        return defaults;
    }
}
