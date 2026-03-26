package game.data;

public enum ItemType {
    FREEZE("Freeze");

    private final String displayName;

    ItemType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}