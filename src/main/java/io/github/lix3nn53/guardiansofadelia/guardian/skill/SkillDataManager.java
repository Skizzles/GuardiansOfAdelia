package io.github.lix3nn53.guardiansofadelia.guardian.skill;

import io.github.lix3nn53.guardiansofadelia.GuardiansOfAdelia;
import io.github.lix3nn53.guardiansofadelia.guardian.skill.component.trigger.TriggerListener;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SkillDataManager {

    private static final HashMap<LivingEntity, HashMap<String, Integer>> keyEntityPlusKeyToValue = new HashMap<>();
    private static final HashMap<LivingEntity, HashSet<String>> keyEntityToSkillFlags = new HashMap<>();
    private static final HashMap<LivingEntity, HashMap<Integer, BukkitTask>> keyEntityToCastCounterToRepeatTask = new HashMap<>();
    private static final HashMap<LivingEntity, HashMap<Integer, List<LivingEntity>>> keyEntityToCastCounterToSavedEntities = new HashMap<>();

    public static void setValue(LivingEntity keyEntity, String key, int value) {
        if (keyEntityPlusKeyToValue.containsKey(keyEntity)) {
            HashMap<String, Integer> hashMap = keyEntityPlusKeyToValue.get(keyEntity);
            hashMap.put(key, value);
            keyEntityPlusKeyToValue.put(keyEntity, hashMap);
        } else {
            HashMap<String, Integer> hashMap = new HashMap<>();
            hashMap.put(key, value);
            keyEntityPlusKeyToValue.put(keyEntity, hashMap);
        }
    }

    public static void addValue(LivingEntity keyEntity, String key, int value) {
        if (keyEntityPlusKeyToValue.containsKey(keyEntity)) {
            HashMap<String, Integer> hashMap = keyEntityPlusKeyToValue.get(keyEntity);
            int oldValue = 0;
            if (hashMap.containsKey(key)) {
                oldValue = hashMap.get(key);
            }
            hashMap.put(key, oldValue + value);
            keyEntityPlusKeyToValue.put(keyEntity, hashMap);
        } else {
            HashMap<String, Integer> hashMap = new HashMap<>();
            hashMap.put(key, value);
            keyEntityPlusKeyToValue.put(keyEntity, hashMap);
        }
    }

    public static int getValue(LivingEntity keyEntity, String key) {
        if (keyEntityPlusKeyToValue.containsKey(keyEntity)) {
            return keyEntityPlusKeyToValue.get(keyEntity).get(key);
        }
        return 0;
    }

    public static void addFlag(LivingEntity keyEntity, String flag) {
        if (keyEntityToSkillFlags.containsKey(keyEntity)) {
            keyEntityToSkillFlags.get(keyEntity).add(flag);
        } else {
            HashSet<String> flags = new HashSet<>();
            flags.add(flag);
            keyEntityToSkillFlags.put(keyEntity, flags);
        }
    }

    public static boolean hasFlag(LivingEntity keyEntity, String flag) {
        if (keyEntityToSkillFlags.containsKey(keyEntity)) {
            return keyEntityToSkillFlags.get(keyEntity).contains(flag);
        }
        return false;
    }

    public static void removeFlag(LivingEntity keyEntity, String flag) {
        if (keyEntityToSkillFlags.containsKey(keyEntity)) {
            HashSet<String> strings = keyEntityToSkillFlags.get(keyEntity);
            strings.remove(flag);
            if (strings.isEmpty()) {
                keyEntityToSkillFlags.remove(keyEntity);
            }
        }
    }

    public static void clearFlags(LivingEntity keyEntity) {
        keyEntityToSkillFlags.remove(keyEntity);
    }

    public static void onRepeatTaskCreate(LivingEntity keyEntity, BukkitTask bukkitTask, int castCounter) {
        if (keyEntityToCastCounterToRepeatTask.containsKey(keyEntity)) {
            HashMap<Integer, BukkitTask> hashMap = keyEntityToCastCounterToRepeatTask.get(keyEntity);
            hashMap.put(castCounter, bukkitTask);
        } else {
            HashMap<Integer, BukkitTask> hashMap = new HashMap<>();
            hashMap.put(castCounter, bukkitTask);
            keyEntityToCastCounterToRepeatTask.put(keyEntity, hashMap);
        }
    }

    public static void removeRepeatTask(LivingEntity keyEntity, int castCounter) {
        if (keyEntityToCastCounterToRepeatTask.containsKey(keyEntity)) {
            HashMap<Integer, BukkitTask> hashMap = keyEntityToCastCounterToRepeatTask.get(keyEntity);
            hashMap.remove(castCounter);
            if (hashMap.isEmpty()) {
                keyEntityToCastCounterToRepeatTask.remove(keyEntity);
            }
        }
    }

    public static void stopRepeatTasksOfCast(LivingEntity keyEntity, int castCounter) {
        if (keyEntityToCastCounterToRepeatTask.containsKey(keyEntity)) {
            HashMap<Integer, BukkitTask> hashMap = keyEntityToCastCounterToRepeatTask.get(keyEntity);

            if (hashMap.containsKey(castCounter)) {
                BukkitTask bukkitTask = hashMap.get(castCounter);
                bukkitTask.cancel();

                hashMap.remove(castCounter);

                if (hashMap.isEmpty()) {
                    keyEntityToCastCounterToRepeatTask.remove(keyEntity);
                }
            }
        }
    }

    public static void onSkillEntityCreateWithSaveOption(LivingEntity keyEntity, LivingEntity created, int castCounter) {
        if (keyEntityToCastCounterToSavedEntities.containsKey(keyEntity)) {
            HashMap<Integer, List<LivingEntity>> hashMap = keyEntityToCastCounterToSavedEntities.get(keyEntity);

            List<LivingEntity> entities;
            if (hashMap.containsKey(castCounter)) {
                entities = hashMap.get(castCounter);
            } else {
                entities = new ArrayList<>();
            }

            entities.add(created);
            hashMap.put(castCounter, entities);
        } else {
            HashMap<Integer, List<LivingEntity>> hashMap = new HashMap<>();
            List<LivingEntity> entities = new ArrayList<>();
            entities.add(created);
            hashMap.put(castCounter, entities);
            keyEntityToCastCounterToSavedEntities.put(keyEntity, hashMap);
        }
        if (keyEntity instanceof Player) {
            TriggerListener.onPlayerSavedEntitySpawn((Player) keyEntity, created);
        }
    }

    public static void removeSavedEntities(LivingEntity keyEntity, int castCounter) {
        if (keyEntityToCastCounterToSavedEntities.containsKey(keyEntity)) {
            HashMap<Integer, List<LivingEntity>> castCounterToSavedEntities = keyEntityToCastCounterToSavedEntities.get(keyEntity);
            List<LivingEntity> removedEntities = castCounterToSavedEntities.remove(castCounter);
            for (LivingEntity removed : removedEntities) {
                removed.remove();
            }
            if (castCounterToSavedEntities.isEmpty()) {
                keyEntityToCastCounterToSavedEntities.remove(keyEntity);
            }
        }
    }

    public static void removeSavedEntity(LivingEntity keyEntity, int castCounter, LivingEntity toRemove) {
        if (keyEntityToCastCounterToSavedEntities.containsKey(keyEntity)) {
            HashMap<Integer, List<LivingEntity>> hashMap = keyEntityToCastCounterToSavedEntities.get(keyEntity);
            List<LivingEntity> entities = hashMap.get(castCounter);

            if (entities == null) {
                GuardiansOfAdelia.getInstance().getLogger().info(ChatColor.RED + "removeSavedEntity entities null");
                return;
            }
            if (toRemove == null) {
                GuardiansOfAdelia.getInstance().getLogger().info(ChatColor.RED + "removeSavedEntity toRemove null");
                return;
            }

            if (entities.contains(toRemove)) {
                entities.remove(toRemove);
                toRemove.remove();
            }
        }
    }

    public static void removeSavedEntity(LivingEntity keyEntity, int castCounter, String mobCode, int amount) {
        int removed = 0;
        if (keyEntityToCastCounterToSavedEntities.containsKey(keyEntity)) {
            HashMap<Integer, List<LivingEntity>> hashMap = keyEntityToCastCounterToSavedEntities.get(keyEntity);
            List<LivingEntity> entities = hashMap.get(castCounter);

            if (entities == null) {
                GuardiansOfAdelia.getInstance().getLogger().info(ChatColor.RED + "removeSavedEntity entities null");
                return;
            }

            BukkitAPIHelper apiHelper = MythicMobs.inst().getAPIHelper();
            for (LivingEntity entity : entities) {
                if (removed == amount) break;
                boolean mythicMob = apiHelper.isMythicMob(entity);
                if (mythicMob) {
                    ActiveMob mythicMobInstance = apiHelper.getMythicMobInstance(entity);
                    String internalName = mythicMobInstance.getType().getInternalName();
                    if (mobCode.equals(internalName)) {
                        entity.remove();
                        removed++;
                    }
                }
            }
        }
    }

    public static List<LivingEntity> getSavedEntitiesOfCaster(LivingEntity keyEntity) {
        List<LivingEntity> allEntities = new ArrayList<>();

        if (keyEntityToCastCounterToSavedEntities.containsKey(keyEntity)) {
            HashMap<Integer, List<LivingEntity>> hashMap = keyEntityToCastCounterToSavedEntities.get(keyEntity);

            for (int castCount : hashMap.keySet()) {
                List<LivingEntity> entities = hashMap.get(castCount);

                allEntities.addAll(entities);
            }
        }

        return allEntities;
    }

    /**
     * clear player cast number
     *
     * @param player
     */
    public static void onPlayerQuit(Player player) {
        keyEntityToSkillFlags.remove(player);
        keyEntityToCastCounterToRepeatTask.remove(player);
        keyEntityPlusKeyToValue.remove(player);
        keyEntityToCastCounterToSavedEntities.remove(player);
    }

    /**
     * clear mob cast number. Do not use for players.
     *
     * @param deathEntity
     */
    public static void onEntityDeath(LivingEntity deathEntity) {
        keyEntityToSkillFlags.remove(deathEntity);
        keyEntityToCastCounterToRepeatTask.remove(deathEntity);
        keyEntityPlusKeyToValue.remove(deathEntity);

        for (LivingEntity keyEntity : keyEntityToCastCounterToSavedEntities.keySet()) {
            HashMap<Integer, List<LivingEntity>> castCounterToSavedEntities = keyEntityToCastCounterToSavedEntities.get(keyEntity);

            for (int castCounter : castCounterToSavedEntities.keySet()) {
                List<LivingEntity> livingEntities = castCounterToSavedEntities.get(castCounter);

                boolean remove = livingEntities.remove(deathEntity);

                if (remove) {
                    if (keyEntity instanceof Player) {
                        TriggerListener.onPlayerSavedEntityDeath((Player) keyEntity, deathEntity);
                    }

                    if (livingEntities.isEmpty()) {
                        castCounterToSavedEntities.remove(castCounter);
                        if (castCounterToSavedEntities.isEmpty()) {
                            keyEntityToCastCounterToSavedEntities.remove(keyEntity);
                        }
                    }
                    break;
                }
            }
        }
    }
}
