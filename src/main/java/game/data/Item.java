package game.data;

import java.io.Serializable;

public class Item implements Serializable {
    public boolean unlocked = false;

    public String name;
    public String description;

    public int cooldownLevel = 0;
    public int damageLevel = 0;
    public int durationLevel = 0;
}
