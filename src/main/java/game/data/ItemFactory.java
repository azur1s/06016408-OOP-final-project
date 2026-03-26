package game.data;

import game.data.items.Freeze;
import game.data.items.Shield;

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
            case SHIELD:
                return new Shield();
            default:
                return new Item();
        }
    }
}