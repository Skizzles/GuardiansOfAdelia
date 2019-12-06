package io.github.lix3nn53.guardiansofadelia.commands;

import io.github.lix3nn53.guardiansofadelia.Items.RpgGears.ItemTier;
import io.github.lix3nn53.guardiansofadelia.Items.enchanting.EnchantStone;
import io.github.lix3nn53.guardiansofadelia.Items.list.eggs.Companions;
import io.github.lix3nn53.guardiansofadelia.Items.list.eggs.Mounts;
import io.github.lix3nn53.guardiansofadelia.Items.list.passiveItems.PassiveItemList;
import io.github.lix3nn53.guardiansofadelia.Items.list.weapons.Weapons;
import io.github.lix3nn53.guardiansofadelia.creatures.AdeliaEntity;
import io.github.lix3nn53.guardiansofadelia.creatures.pets.Companion;
import io.github.lix3nn53.guardiansofadelia.creatures.pets.Mount;
import io.github.lix3nn53.guardiansofadelia.guardian.GuardianData;
import io.github.lix3nn53.guardiansofadelia.guardian.GuardianDataManager;
import io.github.lix3nn53.guardiansofadelia.guardian.character.RPGCharacter;
import io.github.lix3nn53.guardiansofadelia.guardian.character.RPGClass;
import io.github.lix3nn53.guardiansofadelia.guardian.skill.SkillUtils;
import io.github.lix3nn53.guardiansofadelia.guardian.skill.component.mechanic.DamageMechanic;
import io.github.lix3nn53.guardiansofadelia.rpginventory.slots.RPGSlotType;
import io.github.lix3nn53.guardiansofadelia.towns.Town;
import io.github.lix3nn53.guardiansofadelia.towns.TownManager;
import io.github.lix3nn53.guardiansofadelia.utilities.InventoryUtils;
import io.github.lix3nn53.guardiansofadelia.utilities.StaffRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class CommandLix implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!command.getName().equals("lix")) {
            return false;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length < 1) {
                player.sendMessage(ChatColor.DARK_PURPLE + "---- ADMIN ----");
                player.sendMessage(ChatColor.DARK_PURPLE + "/lix setstaff <player> [NONE|OWNER|ADMIN|DEVELOPER|BUILDER|SUPPORT|YOUTUBER|TRAINEE]");
                player.sendMessage(ChatColor.DARK_PURPLE + "/lix exp <player> <amount>");
                player.sendMessage(ChatColor.LIGHT_PURPLE + "---- UTILS ----");
                player.sendMessage(ChatColor.LIGHT_PURPLE + "/lix tp [town|?] <num>");
                player.sendMessage(ChatColor.BLUE + "---- ITEMS ----");
                player.sendMessage(ChatColor.BLUE + "/lix weapon [class] <num>");
                player.sendMessage(ChatColor.BLUE + "/lix companion [type]");
                player.sendMessage(ChatColor.BLUE + "/lix mount [type]");
                player.sendMessage(ChatColor.BLUE + "/lix stone <grade> <amount>");
                player.sendMessage(ChatColor.BLUE + "/lix passive [parrot|earring|necklace|glove|ring] <num>");
                player.sendMessage(ChatColor.BLUE + "/lix model portal<1-5>");
            } else if (args[0].equals("exp")) {
                if (args.length == 3) {
                    int expToGive = Integer.parseInt(args[2]);
                    Player player2 = Bukkit.getPlayer(args[1]);
                    if (player2 != null) {
                        UUID uuid = player2.getUniqueId();
                        if (GuardianDataManager.hasGuardianData(uuid)) {
                            GuardianData guardianData = GuardianDataManager.getGuardianData(uuid);
                            if (guardianData.hasActiveCharacter()) {
                                RPGCharacter activeCharacter = guardianData.getActiveCharacter();
                                activeCharacter.getRpgCharacterStats().giveExp(expToGive);
                            }
                        }
                    }
                }
            } else if (args[0].equals("setstaff")) {
                if (args.length == 3) {
                    try {
                        StaffRank staffRank = StaffRank.valueOf(args[2]);
                        Player player2 = Bukkit.getPlayer(args[1]);
                        if (player2 != null) {
                            UUID uuid = player2.getUniqueId();
                            if (GuardianDataManager.hasGuardianData(uuid)) {
                                GuardianData guardianData = GuardianDataManager.getGuardianData(uuid);
                                guardianData.setStaffRank(staffRank);
                            }
                        }
                    } catch (IllegalArgumentException illegalArgumentException) {
                        player.sendMessage("Unknown staff-rank");
                    }
                }
            } else if (args[0].equals("tp")) {
                if (args.length == 3) {
                    if (args[1].equals("town")) {
                        int no = Integer.parseInt(args[2]);
                        if (no < 6 && no > 0) {
                            Town town = TownManager.getTown(no);
                            player.teleport(town.getLocation());
                        }
                    }
                }
            } else if (args[0].equals("fly")) {
                boolean allowFlight = player.getAllowFlight();
                player.setFlying(!allowFlight);
            } else if (args[0].equals("debug")) {
                LivingEntity caster = AdeliaEntity.SLIME.getMob(player.getLocation());
                SkillUtils.setDamageType(DamageMechanic.DamageType.MELEE);
                player.setNoDamageTicks(0);
                player.damage(100, caster);
            } else if (args[0].equals("weapon")) {
                if (args.length == 3) {
                    RPGClass rpgClass = RPGClass.valueOf(args[1]);
                    int no = Integer.parseInt(args[2]);
                    ItemStack weapon = Weapons.getWeapon(rpgClass, no, ItemTier.LEGENDARY, "Command", 0, 0, 0);
                    InventoryUtils.giveItemToPlayer(player, weapon);
                }
            } else if (args[0].equals("companion")) {
                if (args.length == 2) {
                    Companion mount = Companion.valueOf(args[1]);

                    ItemStack egg = Companions.get(mount, ItemTier.COMMON, 20);
                    InventoryUtils.giveItemToPlayer(player, egg);
                }
            } else if (args[0].equals("mount")) {
                if (args.length == 2) {
                    Mount mount = Mount.valueOf(args[1]);

                    ItemStack egg = Mounts.get(mount, ItemTier.COMMON, 20);
                    InventoryUtils.giveItemToPlayer(player, egg);
                }
            } else if (args[0].equals("stone")) {
                if (args.length == 3) {
                    int grade = Integer.parseInt(args[1]);
                    int amount = Integer.parseInt(args[2]);
                    EnchantStone enchantStone = EnchantStone.TIER_ONE;
                    if (grade == 2) {
                        enchantStone = EnchantStone.TIER_TWO;
                    } else if (grade == 3) {
                        enchantStone = EnchantStone.TIER_THREE;
                    } else if (grade == 4) {
                        enchantStone = EnchantStone.TIER_FOUR;
                    }
                    InventoryUtils.giveItemToPlayer(player, enchantStone.getItemSTack(amount));
                }
            } else if (args[0].equals("passive")) {
                if (args.length == 3) {
                    RPGSlotType rpgSlotType = RPGSlotType.valueOf(args[1]);
                    int no = Integer.parseInt(args[2]);
                    ItemStack passive = PassiveItemList.get(no, rpgSlotType, ItemTier.LEGENDARY, "Command", 0, 100, 2);
                    InventoryUtils.giveItemToPlayer(player, passive);
                }
            } else if (args[0].equals("model")) {
                if (args.length == 2) {
                    Location location = player.getLocation();
                    ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                    ItemStack itemStack = new ItemStack(Material.IRON_PICKAXE);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (args[1].equals("portal1")) {
                        itemMeta.setCustomModelData(10000006);
                    } else if (args[1].equals("portal2")) {
                        itemMeta.setCustomModelData(10000007);
                    } else if (args[1].equals("portal3")) {
                        itemMeta.setCustomModelData(10000008);
                    } else if (args[1].equals("portal4")) {
                        itemMeta.setCustomModelData(10000009);
                    } else if (args[1].equals("portal5")) {
                        itemMeta.setCustomModelData(10000010);
                    }
                    itemMeta.setUnbreakable(true);
                    itemStack.setItemMeta(itemMeta);
                    armorStand.setHelmet(itemStack);
                    armorStand.setVisible(false);
                    armorStand.setInvulnerable(true);
                    armorStand.setGravity(false);
                }
            } else if (args[0].equals("test")) {
                AdeliaEntity.BOSS_DARKNESS.getMob(player.getLocation());
            }

            // If the player (or console) uses our command correct, we can return true
            return true;
        }
        return false;
    }
}
