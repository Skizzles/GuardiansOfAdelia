package io.github.lix3nn53.guardiansofadelia.Items.list;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestItems {


    private static HashMap<Integer, ItemStack> questNoToItem = new HashMap<>();

    static {
        ItemStack item = new ItemStack(Material.ROTTEN_FLESH);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(ChatColor.DARK_GREEN + "Rotten Flesh");
        im.setLore(new ArrayList() {{
            add("");
            add(ChatColor.GRAY + "Quest item for #14");
        }});
        item.setItemMeta(im);
        questNoToItem.put(14, item);

        ItemStack item2 = new ItemStack(Material.ROTTEN_FLESH);
        im.setDisplayName(ChatColor.LIGHT_PURPLE + "Zombie Brain");
        im.setLore(new ArrayList() {{
            add("");
            add(ChatColor.GRAY + "Quest item for #15");
        }});
        item2.setItemMeta(im);
        questNoToItem.put(15, item2);

        ItemStack item3 = new ItemStack(Material.BONE);
        im.setDisplayName(ChatColor.GRAY + "Magical Bone");
        im.setLore(new ArrayList() {{
            add("");
            add(ChatColor.GRAY + "Quest item for #23");
        }});
        item3.setItemMeta(im);
        questNoToItem.put(23, item3);

        ItemStack item4 = new ItemStack(Material.BONE);
        im.setDisplayName(ChatColor.LIGHT_PURPLE + "Bad tasting sugar");
        im.setLore(new ArrayList() {{
            add("");
            add(ChatColor.GRAY + "Quest item for #31");
        }});
        item4.setItemMeta(im);
        questNoToItem.put(31, item4);
    }

    public static ItemStack getQuestItem(int questNo) {
        return questNoToItem.get(questNo);
    }
}
