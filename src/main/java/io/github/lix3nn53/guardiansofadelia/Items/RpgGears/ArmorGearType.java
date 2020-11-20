package io.github.lix3nn53.guardiansofadelia.Items.RpgGears;

import io.github.lix3nn53.guardiansofadelia.Items.list.armors.ArmorMaterial;
import io.github.lix3nn53.guardiansofadelia.utilities.PersistentDataContainerUtil;
import org.bukkit.inventory.ItemStack;

public enum ArmorGearType {
    EARTH_ARMOR,
    FIRE_ARMOR,
    LIGHTNING_ARMOR,
    WATER_ARMOR,
    WIND_ARMOR;

    public static ArmorGearType typeOf(ItemStack itemStack) {
        if (PersistentDataContainerUtil.hasString(itemStack, "gearType")) {
            String gearTypeStr = PersistentDataContainerUtil.getString(itemStack, "gearType");
            try {
                return ArmorGearType.valueOf(gearTypeStr);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    public String getDisplayName() {
        switch (this) {
            case EARTH_ARMOR:
                return "Earth Armor";
            case FIRE_ARMOR:
                return "Fire Armor";
            case LIGHTNING_ARMOR:
                return "Lightning Armor";
            case WATER_ARMOR:
                return "Water Armor";
            case WIND_ARMOR:
                return "Wind Armor";
        }

        return "";
    }

    public double getHealthReduction() {
        switch (this) {
            case EARTH_ARMOR:
                return 1;
            case FIRE_ARMOR:
                return 0.7;
            case WATER_ARMOR:
            case WIND_ARMOR:
                return 0.5;
            case LIGHTNING_ARMOR:
                return 0.4;
        }

        return 0;
    }

    public double getPhysicalDefenseReduction() {
        switch (this) {
            case EARTH_ARMOR:
                return 1;
            case FIRE_ARMOR:
                return 0.7;
            case WATER_ARMOR:
            case WIND_ARMOR:
                return 0.5;
            case LIGHTNING_ARMOR:
                return 0.4;
        }

        return 0;
    }

    public double getMagicDefenseReduction() {
        switch (this) {
            case LIGHTNING_ARMOR:
                return 1;
            case WATER_ARMOR:
            case WIND_ARMOR:
                return 0.7;
            case FIRE_ARMOR:
                return 0.5;
            case EARTH_ARMOR:
                return 0.4;
        }

        return 0;
    }

    public ArmorMaterial getArmorMaterial() {
        switch (this) {
            case EARTH_ARMOR:
                return ArmorMaterial.NETHERITE;
            case FIRE_ARMOR:
                return ArmorMaterial.DIAMOND;
            case LIGHTNING_ARMOR:
                return ArmorMaterial.GOLDEN;
            case WATER_ARMOR:
                return ArmorMaterial.IRON;
            case WIND_ARMOR:
                return ArmorMaterial.CHAINMAIL;
        }

        return null;
    }

    public GearSetEffect getSetEffect() {
        switch (this) {
            case EARTH_ARMOR:
                return GearSetEffect.KNOCKBACK_RESISTANCE;
            case FIRE_ARMOR:
                return GearSetEffect.CRITICAL_DAMAGE;
            case LIGHTNING_ARMOR:
                return GearSetEffect.CRITICAL_CHANCE;
            case WATER_ARMOR:
                return GearSetEffect.MANA_REGEN;
            case WIND_ARMOR:
                return GearSetEffect.ATTACK_SPEED_INCREASE;
        }

        return null;
    }

    public boolean isHeavy() {
        switch (this) {
            case EARTH_ARMOR:
            case FIRE_ARMOR:
                return true;
            case LIGHTNING_ARMOR:
            case WATER_ARMOR:
            case WIND_ARMOR:
                return false;
        }

        return true;
    }
}
