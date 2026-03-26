package game.data;

import game.data.items.Freeze;

public final class ItemFactory {
    private ItemFactory() {
    }

    public static Item create(ItemType itemType) {
        if (itemType == null) {
            return new Item();
        }

        switch (itemType) {
            case FREEZE:
                return new Freeze();
            default:
                return new Item();
        }
    }
}