package io.github.lix3nn53.guardiansofadelia.database;

import io.github.lix3nn53.guardiansofadelia.GuardiansOfAdelia;
import io.github.lix3nn53.guardiansofadelia.bossbar.HeaderBarManager;
import io.github.lix3nn53.guardiansofadelia.chat.ChatManager;
import io.github.lix3nn53.guardiansofadelia.chat.PremiumRank;
import io.github.lix3nn53.guardiansofadelia.chat.StaffRank;
import io.github.lix3nn53.guardiansofadelia.guardian.GuardianData;
import io.github.lix3nn53.guardiansofadelia.guardian.GuardianDataManager;
import io.github.lix3nn53.guardiansofadelia.guardian.character.RPGCharacter;
import io.github.lix3nn53.guardiansofadelia.guardian.character.RPGCharacterExperienceManager;
import io.github.lix3nn53.guardiansofadelia.guardian.character.RPGClass;
import io.github.lix3nn53.guardiansofadelia.guardian.character.RPGClassManager;
import io.github.lix3nn53.guardiansofadelia.guild.Guild;
import io.github.lix3nn53.guardiansofadelia.guild.GuildManager;
import io.github.lix3nn53.guardiansofadelia.guild.PlayerRankInGuild;
import io.github.lix3nn53.guardiansofadelia.text.ChatPalette;
import io.github.lix3nn53.guardiansofadelia.text.locale.Translation;
import io.github.lix3nn53.guardiansofadelia.utilities.TablistUtils;
import io.github.lix3nn53.guardiansofadelia.utilities.managers.AdeliaRegionManager;
import io.github.lix3nn53.guardiansofadelia.utilities.managers.CharacterSelectionScreenManager;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.LivingWatcher;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class DatabaseManager {

    public static void onDisable() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (GuardianDataManager.hasGuardianData(player)) {
                GuardianData guardianData = GuardianDataManager.getGuardianData(player);
                writeGuardianDataWithCurrentCharacter(player, guardianData);
                GuardiansOfAdelia.getInstance().getLogger().info("Player save on disable: " + player.getName());
            }
        }
        List<Guild> activeGuilds = GuildManager.getActiveGuilds();
        for (Guild guild : activeGuilds) {
            writeGuildData(guild);
            GuardiansOfAdelia.getInstance().getLogger().info("Guild save on disable: " + guild.getName());
        }
        DatabaseQueries.onDisable();
    }

    public static void createTables() {
        DatabaseQueries.createTables();
    }

    public static void loadCharacter(Player player, int charNo, Location location) {
        Bukkit.getScheduler().runTaskAsynchronously(GuardiansOfAdelia.getInstance(), () -> {
            try {
                RPGCharacter rpgCharacter = DatabaseQueries.getCharacterAndSetPlayerInventory(player, charNo);
                if (rpgCharacter != null) {
                    GuardianData guardianData = GuardianDataManager.getGuardianData(player);
                    guardianData.setActiveCharacter(rpgCharacter, charNo);
                    Bukkit.getScheduler().runTask(GuardiansOfAdelia.getInstance(), () -> player.teleport(location));
                    TablistUtils.updateTablist(player);
                    // InventoryUtils.setMenuItemPlayer(player);
                    rpgCharacter.getSkillBar().remakeSkillBar(guardianData.getLanguage());
                    player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                    int totalMaxMana = rpgCharacter.getRpgCharacterStats().getTotalMaxMana();
                    rpgCharacter.getRpgCharacterStats().setCurrentMana(totalMaxMana);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void loadPlayerDataAndCharacterSelection(Player player) {
        //player.sendMessage("Loading your player data..");
        UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(GuardiansOfAdelia.getInstance(), () -> {
            try {
                GuardianData guardianData = DatabaseQueries.getGuardianData(uuid);

                if (guardianData.getLanguage() == null) {
                    String locale = player.getLocale();
                    player.sendMessage(ChatPalette.YELLOW + Translation.t(locale, "general.language.client") + "...");
                    guardianData.setLanguage(player, locale);
                } else {
                    player.sendMessage(ChatPalette.GREEN_DARK + Translation.t(guardianData, "general.language.saved") + ": " + guardianData.getLanguage());
                }

                List<Player> friendsOfPlayer = DatabaseQueries.getFriendsOfPlayer(uuid);
                guardianData.setFriends(friendsOfPlayer);

                GuardianDataManager.addGuardianData(player, guardianData);

                String guildNameOfPlayer = DatabaseQueries.getGuildNameOfPlayer(uuid);
                if (guildNameOfPlayer != null) {
                    Optional<Guild> guildOptional = GuildManager.getGuild(guildNameOfPlayer);
                    if (guildOptional.isPresent()) {
                        Guild guild = guildOptional.get();
                        GuildManager.addPlayerGuild(player, guild);
                        GuildManager.sendJoinMessageToMembers(player);
                    } else {
                        Guild guild = DatabaseQueries.getGuild(player, guildNameOfPlayer);
                        if (guild != null) {
                            HashMap<UUID, PlayerRankInGuild> guildMembers = DatabaseQueries.getGuildMembers(guildNameOfPlayer);
                            guild.setMembers(guildMembers);

                            GuildManager.addPlayerGuild(player, guild);
                        }
                    }
                }

                HeaderBarManager.onPlayerJoin(player, guardianData);
                ChatManager.onPlayerJoin(player);

                //player.sendMessage("Loaded player data");
                //character selection screen
                loadCharacterSelectionAndFormHolograms(player, guardianData);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    //Not async, must run async
    private static void loadCharacterSelectionAndFormHolograms(Player player, GuardianData guardianData) {
        player.sendMessage(ChatPalette.YELLOW + Translation.t(guardianData, "general.welcome") + " " + ChatPalette.GOLD + player.getName());
        for (int charNo = 1; charNo <= 8; charNo++) {
            boolean characterExists = DatabaseQueries.characterExists(player.getUniqueId(), charNo);
            if (characterExists) {
                UUID uuid = player.getUniqueId();

                //load last location of character
                try {
                    Location lastLocationOfCharacter = DatabaseQueries.getLastLocationOfCharacter(uuid, charNo);
                    if (lastLocationOfCharacter != null) {
                        CharacterSelectionScreenManager.setCharLocation(uuid, charNo, lastLocationOfCharacter);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                List<ArmorStand> armorStands = CharacterSelectionScreenManager.getCharacterNoToArmorStands().get(charNo);

                MobDisguise mobDisguiseBase1 = new MobDisguise(DisguiseType.ARMOR_STAND, false);
                MobDisguise mobDisguise1 = mobDisguiseBase1.setModifyBoundingBox(false);
                LivingWatcher livingWatcher1 = mobDisguise1.getWatcher();
                livingWatcher1.setInvisible(true);
                livingWatcher1.setNoGravity(true);
                livingWatcher1.setCustomNameVisible(true);

                MobDisguise mobDisguiseBase2 = new MobDisguise(DisguiseType.ARMOR_STAND, false);
                MobDisguise mobDisguise2 = mobDisguiseBase2.setModifyBoundingBox(false);
                LivingWatcher livingWatcher2 = mobDisguise2.getWatcher();
                livingWatcher2.setInvisible(true);
                livingWatcher2.setNoGravity(true);
                livingWatcher2.setCustomNameVisible(true);

                MobDisguise mobDisguiseBase3 = new MobDisguise(DisguiseType.ARMOR_STAND, false);
                MobDisguise mobDisguise3 = mobDisguiseBase3.setModifyBoundingBox(false);
                LivingWatcher livingWatcher3 = mobDisguise3.getWatcher();
                livingWatcher3.setInvisible(true);
                livingWatcher3.setNoGravity(true);
                livingWatcher3.setCustomNameVisible(true);

                try {
                    String rpgClassOfCharStr = DatabaseQueries.getRPGClassCharacter(uuid, charNo);
                    RPGClass rpgClass = RPGClassManager.getClass(rpgClassOfCharStr);
                    ChatColor classColor = rpgClass.getClassColor().toOldColor();

                    int totalExp = DatabaseQueries.getTotalExp(uuid, charNo);
                    int level = RPGCharacterExperienceManager.getLevel(totalExp);
                    CharacterSelectionScreenManager.setCharLevel(uuid, charNo, level);

                    Bukkit.getScheduler().runTask(GuardiansOfAdelia.getInstance(), () -> {
                        livingWatcher1.setCustomName(ChatColor.GOLD + Translation.t(guardianData, "general.level") + ": " + ChatColor.WHITE + level);
                        DisguiseAPI.disguiseToPlayers(armorStands.get(2), mobDisguise1, player);

                        livingWatcher2.setCustomName(ChatColor.LIGHT_PURPLE + Translation.t(guardianData, "general.experience.total") + ": " + ChatColor.WHITE + totalExp);
                        DisguiseAPI.disguiseToPlayers(armorStands.get(1), mobDisguise2, player);

                        livingWatcher3.setCustomName(ChatColor.GRAY + Translation.t(guardianData, "character.class.name") + ": " + classColor + rpgClassOfCharStr);
                        DisguiseAPI.disguiseToPlayers(armorStands.get(0), mobDisguise3, player);
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            //player.sendMessage("Loaded character-" + charNo);
        }
        player.sendMessage(ChatPalette.YELLOW + Translation.t(guardianData, "general.start"));
    }

    public static void writeGuardianDataWithCurrentCharacter(Player player, GuardianData guardianData) {
        //return if it is not safe to save this character now
        if (AdeliaRegionManager.isCharacterSelectionRegion(player.getLocation())) {
            //if player is in character selection it is not safe to save
            return;
        }
        if (player.getLocation().getWorld().getName().equals("tutorial")) { //tutorial
            return;
        }

        UUID uuid = player.getUniqueId();

        //player
        LocalDate lastPrizeDate = guardianData.getDailyRewardInfo().getLastObtainDate();
        StaffRank staffRank = guardianData.getStaffRank();
        PremiumRank premiumRank = guardianData.getPremiumRank();
        ItemStack[] personalStorage = guardianData.getPersonalStorage();
        ItemStack[] bazaarStorage = guardianData.getBazaarStorage();
        ItemStack[] premiumStorage = guardianData.getPremiumStorage();
        String language = guardianData.getLanguage();
        try {
            DatabaseQueries.setGuardianData(uuid, lastPrizeDate, staffRank, premiumRank, personalStorage, bazaarStorage, premiumStorage, language);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Player> friends = guardianData.getFriends();
        DatabaseQueries.setFriendsOfPlayer(uuid, friends);

        //character
        if (guardianData.hasActiveCharacter()) {
            int activeCharacterNo = guardianData.getActiveCharacterNo();
            RPGCharacter activeCharacter = guardianData.getActiveCharacter();
            ItemStack offHand = null;
            if (!activeCharacter.getRpgInventory().getOffhandSlot().isEmpty(player)) {
                offHand = activeCharacter.getRpgInventory().getOffhandSlot().getItemOnSlot(player);
            }
            try {
                DatabaseQueries.setCharacter(player.getUniqueId(), activeCharacterNo, activeCharacter, player.getInventory().getContents(),
                        player.getLocation(), player.getInventory().getArmorContents(), offHand);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeGuildData(Guild guild) {
        try {
            DatabaseQueries.setGuild(guild);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DatabaseQueries.setMembersOfGuild(guild.getName(), guild.getMembersWithRanks());
    }

    public static void clearGuild(String guildName) {
        try {
            DatabaseQueries.clearMembersOfGuild(guildName);
            DatabaseQueries.clearGuild(guildName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearCharacter(Player player, int charNo) {
        try {
            DatabaseQueries.clearCharacter(player.getUniqueId(), charNo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearPlayer(UUID uuid) {
        try {
            DatabaseQueries.clearFriendsOfPlayer(uuid);
            DatabaseQueries.clearPlayerRanksAndStorages(uuid);
            DatabaseQueries.clearGuildOfPlayer(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeGuildOfPlayer(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(GuardiansOfAdelia.getInstance(), () -> {
            try {
                DatabaseQueries.clearGuildOfPlayer(uuid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
