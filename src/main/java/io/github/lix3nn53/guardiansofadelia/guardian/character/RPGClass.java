package io.github.lix3nn53.guardiansofadelia.guardian.character;

import org.bukkit.ChatColor;

public enum RPGClass {
    ARCHER,
    KNIGHT,
    MAGE,
    MONK,
    ROGUE,
    PALADIN,
    WARRIOR,
    HUNTER,
    NO_CLASS;

    public String getClassString() {
        ChatColor color = ChatColor.GREEN;
        String name = "Archer";
        if (this == RPGClass.KNIGHT) {
            color = ChatColor.AQUA;
            name = "Knight";
        } else if (this == RPGClass.MAGE) {
            color = ChatColor.LIGHT_PURPLE;
            name = "Mage";
        } else if (this == RPGClass.MONK) {
            color = ChatColor.GOLD;
            name = "Monk";
        } else if (this == RPGClass.ROGUE) {
            color = ChatColor.BLUE;
            name = "Rogue";
        } else if (this == RPGClass.PALADIN) {
            color = ChatColor.YELLOW;
            name = "Paladin";
        } else if (this == RPGClass.WARRIOR) {
            color = ChatColor.RED;
            name = "Warrior";
        } else if (this == RPGClass.HUNTER) {
            color = ChatColor.DARK_GREEN;
            name = "Hunter";
        }
        return color + name;
    }

    public ChatColor getClassColor() {
        ChatColor color = ChatColor.GREEN;
        if (this == RPGClass.KNIGHT) {
            color = ChatColor.AQUA;
        } else if (this == RPGClass.MAGE) {
            color = ChatColor.LIGHT_PURPLE;
        } else if (this == RPGClass.MONK) {
            color = ChatColor.GOLD;
        } else if (this == RPGClass.ROGUE) {
            color = ChatColor.BLUE;
        } else if (this == RPGClass.PALADIN) {
            color = ChatColor.YELLOW;
        } else if (this == RPGClass.WARRIOR) {
            color = ChatColor.RED;
        } else if (this == RPGClass.HUNTER) {
            color = ChatColor.DARK_GREEN;
        }
        return color;
    }

    public String getClassStringNoColor() {
        String name = "Archer";
        if (this == RPGClass.KNIGHT) {
            name = "Knight";
        } else if (this == RPGClass.MAGE) {
            name = "Mage";
        } else if (this == RPGClass.MONK) {
            name = "Monk";
        } else if (this == RPGClass.ROGUE) {
            name = "Rogue";
        } else if (this == RPGClass.PALADIN) {
            name = "Paladin";
        } else if (this == RPGClass.WARRIOR) {
            name = "Warrior";
        } else if (this == RPGClass.HUNTER) {
            name = "Hunter";
        }
        return name;
    }

    public int getBonusHealthForLevel(int level) {
        double multiplier = 1;
        if (this == RPGClass.ARCHER) {
            multiplier = 1.75;
        } else if (this == RPGClass.KNIGHT) {
            multiplier = 1.85;
        } else if (this == RPGClass.MAGE) {
            multiplier = 1.75;
        } else if (this == RPGClass.MONK) {
            multiplier = 1.8;
        } else if (this == RPGClass.ROGUE) {
            multiplier = 1.8;
        } else if (this == RPGClass.PALADIN) {
            multiplier = 1.85;
        } else if (this == RPGClass.WARRIOR) {
            multiplier = 1.8;
        } else if (this == RPGClass.HUNTER) {
            multiplier = 1.75;
        }
        return (int) (Math.pow(level, multiplier) - (level) + 0.5);
    }
}
