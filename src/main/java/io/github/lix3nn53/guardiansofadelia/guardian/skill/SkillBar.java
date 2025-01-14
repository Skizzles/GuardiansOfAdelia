package io.github.lix3nn53.guardiansofadelia.guardian.skill;

import io.github.lix3nn53.guardiansofadelia.GuardiansOfAdelia;
import io.github.lix3nn53.guardiansofadelia.guardian.GuardianData;
import io.github.lix3nn53.guardiansofadelia.guardian.GuardianDataManager;
import io.github.lix3nn53.guardiansofadelia.guardian.character.RPGCharacter;
import io.github.lix3nn53.guardiansofadelia.guardian.character.RPGCharacterStats;
import io.github.lix3nn53.guardiansofadelia.guardian.character.RPGClassExperienceManager;
import io.github.lix3nn53.guardiansofadelia.guardian.character.RPGClassStats;
import io.github.lix3nn53.guardiansofadelia.guardian.skill.component.mechanic.statuseffect.StatusEffectManager;
import io.github.lix3nn53.guardiansofadelia.guardian.skill.component.trigger.InitializeTrigger;
import io.github.lix3nn53.guardiansofadelia.guardian.skill.component.trigger.TriggerListener;
import io.github.lix3nn53.guardiansofadelia.items.list.OtherItems;
import io.github.lix3nn53.guardiansofadelia.text.ChatPalette;
import io.github.lix3nn53.guardiansofadelia.text.locale.Translation;
import io.github.lix3nn53.guardiansofadelia.utilities.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Each player character has unique skill-bar
 */
public class SkillBar {

    private final HashMap<String, Boolean> skillsOnCooldown = new HashMap<>();
    private final Player player;
    private final HashMap<Integer, Integer> investedSkillPoints = new HashMap<>();
    private final HashMap<Integer, Skill> skillSet;

    private int castCounter = 0;

    public SkillBar(Player player, int one, int two, int three, int passive, int ultimate, HashMap<Integer, Skill> skillSet, boolean remake) {
        this.player = player;
        this.skillSet = skillSet;

        player.getInventory().setHeldItemSlot(4);

        investedSkillPoints.put(0, one);
        investedSkillPoints.put(1, two);
        investedSkillPoints.put(2, three);
        investedSkillPoints.put(3, passive);
        investedSkillPoints.put(4, ultimate);

        if (remake) {
            GuardianData guardianData = GuardianDataManager.getGuardianData(player);
            remakeSkillBar(guardianData.getLanguage());
        }

        //activate init triggers
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 4; i++) {
                    if (investedSkillPoints.get(i) <= 0) continue;

                    Skill skill = skillSet.get(i);
                    List<InitializeTrigger> initializeTriggers = skill.getInitializeTriggers();
                    for (InitializeTrigger initializeTrigger : initializeTriggers) {
                        int nextSkillLevel = skill.getCurrentSkillLevel(getInvestedSkillPoints(i));
                        TriggerListener.onSkillUpgrade(player, initializeTrigger, i, nextSkillLevel, castCounter);
                        castCounter++;
                    }
                }
            }
        }.runTaskLaterAsynchronously(GuardiansOfAdelia.getInstance(), 40L);
    }

    /**
     * @param skillIndex 0,1,2 normal skills, 3 passive, 4 ultimate
     */
    public boolean upgradeSkill(int skillIndex, RPGClassStats rpgClassStats, String lang) {
        if (!this.skillSet.containsKey(skillIndex)) return false;

        Skill skill = this.skillSet.get(skillIndex);

        Integer invested = investedSkillPoints.get(skillIndex);
        int currentSkillLevel = skill.getCurrentSkillLevel(invested);

        if (currentSkillLevel >= skill.getMaxSkillLevel()) {
            return false;
        }

        int reqSkillPoints = skill.getReqSkillPoints(currentSkillLevel);

        if (getSkillPointsLeftToSpend() >= reqSkillPoints) {
            List<InitializeTrigger> initializeTriggers = skill.getInitializeTriggers();
            for (InitializeTrigger initializeTrigger : initializeTriggers) {
                TriggerListener.onSkillUpgrade(player, initializeTrigger, skillIndex, currentSkillLevel + 1, castCounter);
                castCounter++;
            }
            int newInvested = invested + reqSkillPoints;
            investedSkillPoints.put(skillIndex, newInvested);
            remakeSkillBarIcon(skillIndex, lang);

            rpgClassStats.setInvestedSkillPoint(skillIndex, newInvested);
            return true;
        }

        return false;
    }

    /**
     * @param skillIndex 0,1,2 normal skills, 3 passive, 4 ultimate
     */
    public boolean downgradeSkill(int skillIndex, RPGClassStats rpgClassStats, String lang) {
        if (!this.skillSet.containsKey(skillIndex)) return false;

        Skill skill = this.skillSet.get(skillIndex);

        Integer invested = investedSkillPoints.get(skillIndex);
        int currentSkillLevel = skill.getCurrentSkillLevel(invested);

        if (currentSkillLevel <= 0) return false;

        List<InitializeTrigger> initializeTriggers = skill.getInitializeTriggers();
        for (InitializeTrigger initializeTrigger : initializeTriggers) {
            TriggerListener.onSkillDowngrade(player, initializeTrigger, skillIndex, currentSkillLevel - 1, castCounter);
        }

        int reqSkillPoints = skill.getReqSkillPoints(currentSkillLevel - 1);

        int newInvested = invested - reqSkillPoints;
        investedSkillPoints.put(skillIndex, newInvested);
        remakeSkillBarIcon(skillIndex, lang);

        rpgClassStats.setInvestedSkillPoint(skillIndex, newInvested);
        return true;
    }

    public boolean resetSkillPoints(String lang) {
        investedSkillPoints.clear();
        investedSkillPoints.put(0, 0);
        investedSkillPoints.put(1, 0);
        investedSkillPoints.put(2, 0);
        investedSkillPoints.put(3, 0);
        investedSkillPoints.put(4, 0);

        for (int skillIndex = 0; skillIndex < 5; skillIndex++) {
            Skill skill = this.skillSet.get(skillIndex);

            List<InitializeTrigger> initializeTriggers = skill.getInitializeTriggers();
            for (InitializeTrigger initializeTrigger : initializeTriggers) {
                TriggerListener.onSkillDowngrade(player, initializeTrigger, skillIndex, 0, castCounter);
                castCounter++;
            }

            remakeSkillBarIcon(skillIndex, lang);
        }
        return true;
    }

    public int getInvestedSkillPoints(int skillIndex) {
        return investedSkillPoints.get(skillIndex);
    }

    public int getSkillPointsLeftToSpend() {
        int points = 1;
        if (GuardianDataManager.hasGuardianData(player)) {
            GuardianData guardianData = GuardianDataManager.getGuardianData(player);
            if (guardianData.hasActiveCharacter()) {
                RPGCharacter activeCharacter = guardianData.getActiveCharacter();
                RPGClassStats currentRPGClassStats = activeCharacter.getCurrentRPGClassStats();

                int totalExperience = currentRPGClassStats.getTotalExperience();

                points = RPGClassExperienceManager.getLevel(totalExperience);
            }
        }

        for (int invested : investedSkillPoints.values()) {
            points -= invested;
        }

        return points;
    }

    public void remakeSkillBar(String lang) {
        for (int i = 0; i < investedSkillPoints.size(); i++) {
            int slot = i;

            if (i == 3) { //don't place passive skill on hot-bar
                continue;
            } else if (i > 3) {
                slot--;
            }

            Integer invested = investedSkillPoints.get(i);
            if (invested > 0) {
                Skill skill = this.skillSet.get(i);
                ItemStack icon = skill.getIcon(lang, getSkillPointsLeftToSpend(), invested);
                player.getInventory().setItem(slot, icon);
            } else {
                player.getInventory().setItem(slot, OtherItems.getUnassignedSkill());
            }
        }
    }

    public void remakeSkillBarIcon(int skillIndex, String lang) {
        int slot = skillIndex;

        if (skillIndex == 3) { //don't place passive skill on hot-bar
            return;
        } else if (skillIndex > 3) {
            slot--;
        }

        Integer invested = investedSkillPoints.get(skillIndex);
        if (invested > 0) {
            Skill skill = this.skillSet.get(skillIndex);
            ItemStack icon = skill.getIcon(lang, getSkillPointsLeftToSpend(), invested);
            player.getInventory().setItem(slot, icon);
        } else {
            player.getInventory().setItem(slot, OtherItems.getUnassignedSkill());
        }
    }

    public static float abilityHasteToMultiplier(float abilityHaste) {
        return 100 / (100 + abilityHaste);
    }

    public HashMap<Integer, Skill> getSkillSet() {
        return this.skillSet;
    }

    public HashMap<Integer, Integer> getInvestedSkillPoints() {
        return investedSkillPoints;
    }

    public int getCurrentSkillLevel(int skillIndex) {
        Skill skill = this.skillSet.get(skillIndex);

        Integer invested = investedSkillPoints.get(skillIndex);
        return skill.getCurrentSkillLevel(invested);
    }

    public void reloadSkillSet(HashMap<Integer, Skill> skillSet, String lang) {
        this.skillSet.clear();
        this.skillSet.putAll(skillSet);
        remakeSkillBar(lang);
    }

    public boolean castSkill(GuardianData guardianData, int slot) {
        if (StatusEffectManager.isSilenced(player)) {
            return false;
        }

        int skillIndex = slot;
        if (slot == 3) skillIndex = 4; //ultimate is one off

        if (skillsOnCooldown.containsKey("" + skillIndex)) {
            player.sendMessage(ChatPalette.RED + Translation.t(guardianData, "skill.cooldown"));
            return false;
        }

        Skill skill = this.skillSet.get(skillIndex);

        Integer invested = investedSkillPoints.get(skillIndex);
        int skillLevel = skill.getCurrentSkillLevel(invested);

        if (skillLevel <= 0) {
            player.sendMessage(ChatPalette.RED + Translation.t(guardianData, "skill.unlearned"));
            return false;
        }

        int manaCost = skill.getManaCost(skillLevel);
        RPGCharacter activeCharacter = guardianData.getActiveCharacter();
        RPGCharacterStats rpgCharacterStats = activeCharacter.getRpgCharacterStats();
        int currentMana = rpgCharacterStats.getCurrentMana();
        if (currentMana < manaCost) {
            player.sendMessage(ChatPalette.RED + Translation.t(guardianData, "skill.nomana"));
            return false;
        }

        boolean cast = skill.cast(player, skillLevel, new ArrayList<>(), castCounter, skillIndex);//cast ends when this returns

        if (!cast) {
            player.sendMessage(ChatPalette.RED + Translation.t(guardianData, "skill.fail"));
            return false; //dont go on cooldown and consume mana if cast failed
        }

        castCounter++;
        TriggerListener.onPlayerSkillCast(player);

        // mana cost
        rpgCharacterStats.consumeMana(manaCost);

        float abilityHaste = rpgCharacterStats.getTotalAbilityHaste();

        int cooldownInTicks = (int) (((skill.getCooldown(skillLevel) * 20) * abilityHasteToMultiplier(abilityHaste)) + 0.5); // Ability haste formula from League of Legends
        PlayerInventory inventory = player.getInventory();

        skillsOnCooldown.put("" + skillIndex, true);

        final int finalSkillIndex = skillIndex;
        new BukkitRunnable() {

            int ticksPassed = 0;

            @Override
            public void run() {
                if (ticksPassed >= cooldownInTicks) {
                    cancel();
                    skillsOnCooldown.remove("" + finalSkillIndex);
                } else {
                    int cooldownLeft = cooldownInTicks - ticksPassed;
                    int secondsLeft = cooldownLeft / 20;
                    float modulus = cooldownLeft % 20;

                    if (modulus > 0) {
                        secondsLeft++;
                    }

                    ItemStack item = inventory.getItem(slot);
                    int currentAmount = item.getAmount();
                    if (currentAmount != secondsLeft) {
                        if (InventoryUtils.isAirOrNull(item)) {
                            remakeSkillBarIcon(finalSkillIndex, guardianData.getLanguage());
                            item = inventory.getItem(slot);
                        }
                        item.setAmount(secondsLeft);
                    }
                }
                ticksPassed++;
            }
        }.runTaskTimer(GuardiansOfAdelia.getInstance(), 0L, 1L);

        return true;
    }
}
