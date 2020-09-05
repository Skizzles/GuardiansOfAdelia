package io.github.lix3nn53.guardiansofadelia.quests.task;

import io.github.lix3nn53.guardiansofadelia.GuardiansOfAdelia;
import io.github.lix3nn53.guardiansofadelia.Items.config.ItemReferenceLoader;
import io.github.lix3nn53.guardiansofadelia.jobs.gathering.GatheringManager;
import io.github.lix3nn53.guardiansofadelia.jobs.gathering.Ingredient;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TaskLoader {

    public static Task load(ConfigurationSection configurationSection) {
        String componentType = configurationSection.getString("taskType");

        if (componentType == null) {
            GuardiansOfAdelia.getInstance().getLogger().info(ChatColor.RED + "NULL TASK TYPE");
            return null;
        }

        if (componentType.equals(TaskCollect.class.getSimpleName())) {
            List<String> keyOfMobsItemDropsFrom = configurationSection.getStringList("keyOfMobsItemDropsFrom");

            List<String> nameOfMobsItemDropsFrom = new ArrayList<>();
            for (String internalName : keyOfMobsItemDropsFrom) {
                MythicMob mythicMob = MythicMobs.inst().getMobManager().getMythicMob(internalName);
                if (mythicMob == null) {
                    GuardiansOfAdelia.getInstance().getLogger().info(ChatColor.RED + "TaskCollect mythicMob null: " + internalName);

                    return null;
                }
                String displayName = mythicMob.getDisplayName().get();
                GuardiansOfAdelia.getInstance().getLogger().info(ChatColor.GREEN + "TaskCollect MM: " + internalName + "-" + displayName);

                nameOfMobsItemDropsFrom.add(internalName);
            }

            double chance = configurationSection.getDouble("chance");
            ConfigurationSection itemDrop = configurationSection.getConfigurationSection("itemDrop");
            ItemStack questItem = ItemReferenceLoader.loadItemReference(itemDrop);
            int amountNeeded = configurationSection.getInt("amountNeeded");

            return new TaskCollect(nameOfMobsItemDropsFrom, chance, questItem, amountNeeded);
        } else if (componentType.equals(TaskDealDamage.class.getSimpleName())) {
            String internalName = configurationSection.getString("mobKey");

            MythicMob mythicMob = MythicMobs.inst().getMobManager().getMythicMob(internalName);
            if (mythicMob == null) {
                GuardiansOfAdelia.getInstance().getLogger().info(ChatColor.RED + "TaskDealDamage mythicMob null: " + internalName);

                return null;
            }
            String displayName = mythicMob.getDisplayName().get();
            GuardiansOfAdelia.getInstance().getLogger().info(ChatColor.GREEN + "TaskDealDamage MM: " + internalName + "-" + displayName);

            int damageNeeded = configurationSection.getInt("damageNeeded");

            return new TaskDealDamage(internalName, damageNeeded);
        } else if (componentType.equals(TaskGathering.class.getSimpleName())) {
            int ingredientIndex = configurationSection.getInt("ingredientIndex");
            Ingredient ingredient = GatheringManager.getIngredient(ingredientIndex);

            int amountNeeded = configurationSection.getInt("amountNeeded");

            return new TaskGathering(ingredient, amountNeeded);
        } else if (componentType.equals(TaskGift.class.getSimpleName())) {
            String internalName = configurationSection.getString("mobKey");

            MythicMob mythicMob = MythicMobs.inst().getMobManager().getMythicMob(internalName);
            if (mythicMob == null) {
                GuardiansOfAdelia.getInstance().getLogger().info(ChatColor.RED + "TaskGift mythicMob null: " + internalName);

                return null;
            }
            String displayName = mythicMob.getDisplayName().get();
            GuardiansOfAdelia.getInstance().getLogger().info(ChatColor.GREEN + "TaskGift MM: " + internalName + "-" + displayName);

            ItemStack itemStack = ItemReferenceLoader.loadItemReference(configurationSection.getConfigurationSection("item"));

            int amountNeeded = configurationSection.getInt("amountNeeded");

            return new TaskGift(amountNeeded, itemStack, internalName);
        } else if (componentType.equals(TaskInteract.class.getSimpleName())) {
            int npcId = configurationSection.getInt("npcId");

            return new TaskInteract(npcId);
        } else if (componentType.equals(TaskKill.class.getSimpleName())) {
            String internalName = configurationSection.getString("mobKey");

            MythicMob mythicMob = MythicMobs.inst().getMobManager().getMythicMob(internalName);
            if (mythicMob == null) {
                GuardiansOfAdelia.getInstance().getLogger().info(ChatColor.RED + "TaskKill mythicMob null: " + internalName);

                return null;
            }
            String displayName = mythicMob.getDisplayName().get();
            GuardiansOfAdelia.getInstance().getLogger().info(ChatColor.GREEN + "TaskKill MM: " + internalName + "-" + displayName);

            int amountNeeded = configurationSection.getInt("amountNeeded");

            return new TaskKill(internalName, amountNeeded);
        } else if (componentType.equals(TaskReach.class.getSimpleName())) {
            World world = Bukkit.getWorld(configurationSection.getString("world"));

            double x = configurationSection.getDouble("x");
            double y = configurationSection.getDouble("y");
            double z = configurationSection.getDouble("z");

            Location location = new Location(world, x, y, z);

            Material blockMaterial = Material.valueOf(configurationSection.getString("blockMaterial"));

            return new TaskReach(location, blockMaterial);
        }

        GuardiansOfAdelia.getInstance().getLogger().info(ChatColor.RED + "NO SUCH TASK IN LOADER");

        return null;
    }

}
