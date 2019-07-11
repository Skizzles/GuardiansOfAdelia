package io.github.lix3nn53.guardiansofadelia.guardian.skill.component.trigger;

import io.github.lix3nn53.guardiansofadelia.GuardiansOfAdelia;
import io.github.lix3nn53.guardiansofadelia.guardian.skill.SkillDataManager;
import io.github.lix3nn53.guardiansofadelia.guardian.skill.component.TriggerComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class TookMeleeDamageTrigger extends TriggerComponent {

    private final long cooldown;
    LivingEntity caster;
    int skillLevel;
    String castKey;

    public TookMeleeDamageTrigger(long cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    public boolean execute(LivingEntity caster, int skillLevel, List<LivingEntity> targets, String castKey) {
        this.caster = caster;
        this.skillLevel = skillLevel;
        this.castKey = castKey;

        TookMeleeDamageTrigger tookPhysicalDamageTrigger = this;

        new BukkitRunnable() {
            @Override
            public void run() {
                for (LivingEntity target : targets) {
                    if (target instanceof Player) {
                        TriggerListener.startListeningTookMeleeDamage((Player) target, tookPhysicalDamageTrigger);
                    }
                }
            }
        }.runTaskLaterAsynchronously(GuardiansOfAdelia.getInstance(), 10L);

        return true;
    }

    @Override
    public List<String> getSkillLoreAdditions(int skillLevel) {
        return new ArrayList<>();
    }

    /**
     * The callback when player lands that applies child components
     */
    public void callback(Player target) {
        ArrayList<LivingEntity> targets = new ArrayList<>();
        targets.add(target);
        executeChildren(caster, skillLevel, targets, castKey);

        TookMeleeDamageTrigger trigger = this;

        BukkitTask bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                TriggerListener.startListeningTookMeleeDamage(target, trigger);
            }
        }.runTaskLaterAsynchronously(GuardiansOfAdelia.getInstance(), cooldown);

        SkillDataManager.onRepeatTaskCreate(castKey, bukkitTask);
    }
}