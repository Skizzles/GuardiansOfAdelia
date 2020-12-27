package io.github.lix3nn53.guardiansofadelia.Items.config;

import io.github.lix3nn53.guardiansofadelia.Items.RpgGears.ItemTier;
import io.github.lix3nn53.guardiansofadelia.Items.RpgGears.WeaponGearType;
import io.github.lix3nn53.guardiansofadelia.Items.list.weapons.WeaponManager;
import io.github.lix3nn53.guardiansofadelia.guardian.character.RPGClass;
import io.github.lix3nn53.guardiansofadelia.guardian.character.RPGClassManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class WeaponReferenceData {
    private final int gearLevel;
    private final int itemIndex;
    private final ItemTier itemTier;
    private final String itemTag;

    public WeaponReferenceData(int gearLevel, int itemIndex, ItemTier itemTier, String itemTag) {
        this.gearLevel = gearLevel;
        this.itemIndex = itemIndex;
        this.itemTier = itemTier;
        this.itemTag = itemTag;
    }

    public WeaponReferenceData(ConfigurationSection configurationSection) {
        this.gearLevel = configurationSection.getInt("gearLevel");
        this.itemIndex = configurationSection.getInt("itemIndex");
        this.itemTier = ItemTier.valueOf(configurationSection.getString("itemTier"));
        this.itemTag = configurationSection.getString("itemTag");
    }

    public List<ItemStack> getItems(String rpgClassStr) {
        RPGClass rpgClass = RPGClassManager.getClass(rpgClassStr);
        List<WeaponGearType> weaponGearTypes = rpgClass.getWeaponGearTypes();

        ArrayList<ItemStack> items = new ArrayList<>();
        for (WeaponGearType type : weaponGearTypes) {
            items.add(WeaponManager.get(type, gearLevel, itemIndex, itemTier, itemTag, true));
        }

        return items;
    }

    public int getGearLevel() {
        return gearLevel;
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public ItemTier getItemTier() {
        return itemTier;
    }

    public String getItemTag() {
        return itemTag;
    }
}