package io.github.lix3nn53.guardiansofadelia.Items.RpgGears.gearset;

import io.github.lix3nn53.guardiansofadelia.Items.RpgGears.ArmorGearType;
import io.github.lix3nn53.guardiansofadelia.guardian.character.RPGCharacterStats;
import io.github.lix3nn53.guardiansofadelia.guardian.skill.component.mechanic.buff.BuffType;
import io.github.lix3nn53.guardiansofadelia.utilities.PersistentDataContainerUtil;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public enum GearSetEffect {
    EMPTY,
    KNOCKBACK_RESISTANCE,
    CRITICAL_DAMAGE,
    CRITICAL_CHANCE,
    // MANA_REGEN,
    SLOW_FALLING,
    SWIMMING_SPEED,
    ATTACK_SPEED_INCREASE,
    ATTACK_SPEED_DECREASE,
    ABILITY_HASTE,
    JUMP_BOOST;

    public static boolean isWearingSameArmorType(ArmorGearType helmet, ArmorGearType chestplate, ArmorGearType leggings, ArmorGearType boots) {
        if (helmet == null || chestplate == null || leggings == null || boots == null) return false;

        return helmet == chestplate && helmet == leggings && helmet == boots;
    }

    public static String getCustomSet(ItemStack itemStack) {
        if (itemStack == null) return null;

        if (PersistentDataContainerUtil.hasString(itemStack, "gearSet")) {
            return PersistentDataContainerUtil.getString(itemStack, "gearSet");
        }

        return null;
    }

    public void applySetEffect(Player player, RPGCharacterStats rpgCharacterStats) {
        player.sendMessage(this.toString());
        switch (this) {
            case KNOCKBACK_RESISTANCE:
                Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(0.7);
                break;
            case CRITICAL_DAMAGE:
                rpgCharacterStats.addToBuffMultiplier(BuffType.CRIT_DAMAGE, 0.2, null);
                break;
            case CRITICAL_CHANCE:
                rpgCharacterStats.addToBuffMultiplier(BuffType.CRIT_CHANCE, 0.1, null);
                break;
            case SLOW_FALLING:
                PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 1);
                player.addPotionEffect(potionEffect);
                break;
            case SWIMMING_SPEED:
                potionEffect = new PotionEffect(PotionEffectType.DOLPHINS_GRACE, Integer.MAX_VALUE, 1);
                player.addPotionEffect(potionEffect);
                break;
            case ATTACK_SPEED_INCREASE:
                potionEffect = new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1);
                player.addPotionEffect(potionEffect);
                break;
            case ATTACK_SPEED_DECREASE:
                potionEffect = new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 1);
                player.addPotionEffect(potionEffect);
                break;
            case ABILITY_HASTE:
                rpgCharacterStats.addToBuffMultiplier(BuffType.ABILITY_HASTE, 20, null);
                break;
            case JUMP_BOOST:
                potionEffect = new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1);
                player.addPotionEffect(potionEffect);
                break;
        }
    }

    public void clearSetEffect(Player player, RPGCharacterStats rpgCharacterStats) {
        switch (this) {
            case KNOCKBACK_RESISTANCE:
                player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
                break;
            case CRITICAL_DAMAGE:
                rpgCharacterStats.addToBuffMultiplier(BuffType.CRIT_DAMAGE, -0.2, null);
                break;
            case CRITICAL_CHANCE:
                rpgCharacterStats.addToBuffMultiplier(BuffType.CRIT_CHANCE, -0.1, null);
                break;
            case SLOW_FALLING:
                player.removePotionEffect(PotionEffectType.SLOW_FALLING);
                break;
            case SWIMMING_SPEED:
                player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
                break;
            case ATTACK_SPEED_INCREASE:
                player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                break;
            case ATTACK_SPEED_DECREASE:
                player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                break;
            case ABILITY_HASTE:
                rpgCharacterStats.addToBuffMultiplier(BuffType.ABILITY_HASTE, -20, null);
                break;
            case JUMP_BOOST:
                player.removePotionEffect(PotionEffectType.JUMP);
                break;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case EMPTY:
                return "Empty";
            case KNOCKBACK_RESISTANCE:
                return ChatColor.GRAY + "Knockback Resist 70%";
            case CRITICAL_DAMAGE:
                return ChatColor.GRAY + "Critical Damage +20%";
            case CRITICAL_CHANCE:
                return ChatColor.GRAY + "Critical Chance +10%";
            /*case MANA_REGEN:
                return ChatColor.GRAY + "Mana Regen";*/
            case SLOW_FALLING:
                return ChatColor.GRAY + "Slow Fallling";
            case SWIMMING_SPEED:
                return ChatColor.GRAY + "Swimming Speed +?%";
            case ATTACK_SPEED_INCREASE:
                return ChatColor.GRAY + "Attack Speed +?%";
            case ATTACK_SPEED_DECREASE:
                return ChatColor.GRAY + "Attack Speed -?%";
            case ABILITY_HASTE:
                return ChatColor.GRAY + "Ability Haste +20";
            case JUMP_BOOST:
                return ChatColor.GRAY + "Jump Height +?";
        }

        final StringBuilder sb = new StringBuilder("GearSetEffect{");
        sb.append('}');
        return sb.toString();
    }
}
