package game.data;

public class PlayerData {
    // 99999 starting coins for testing
    public static int coins = 99999;

    // Selected character index (0-2)
    public static int selectedCharacter = 0;

    // index 0 = Item 1 (unlocked by default)
    public static boolean[] unlockedItems = { true, false, false, false, false };

    // Skins (all locked by default)
    public static boolean[] unlockedSkins = { false, false, false };

    // Unlocked Stages (Stage 1 unlocked by default)
    public static boolean[] unlockedStages = { true, false, false, false, false };

    // Equipped Items [Slot 1, Slot 2]. Value = Item Index (0-4), -1 = Empty
    public static int[] equippedItems = { -1, -1 };

    // Item Upgrade Levels. Format: [itemIndex][statIndex]
    // statIndex 0 = Cooldown, 1 = Damage, 2 = Duration
    public static int[][] itemStatsLevels = new int[5][3];

    // Helper functions for readability
    public static boolean hasEnoughCoins(int cost) {
        return coins >= cost;
    }

    public static void deductCoins(int amount) {
        coins -= amount;
        if (coins < 0)
            coins = 0;
    }
}
