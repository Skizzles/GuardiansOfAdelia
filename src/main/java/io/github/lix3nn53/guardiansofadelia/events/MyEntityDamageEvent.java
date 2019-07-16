package io.github.lix3nn53.guardiansofadelia.events;

import io.github.lix3nn53.guardiansofadelia.creatures.pets.PetManager;
import io.github.lix3nn53.guardiansofadelia.guardian.skill.component.mechanic.immunity.ImmunityListener;
import io.github.lix3nn53.guardiansofadelia.party.Party;
import io.github.lix3nn53.guardiansofadelia.party.PartyManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class MyEntityDamageEvent implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEvent(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) return;

        EntityDamageEvent.DamageCause cause = event.getCause();

        if (cause.equals(EntityDamageEvent.DamageCause.VOID)) {
            event.setDamage(10000D);
        } else if (cause.equals(EntityDamageEvent.DamageCause.SUFFOCATION)) {
            entity.teleport(entity.getLocation().clone().add(0, 5, 0));
        }

        if (ImmunityListener.isImmune((LivingEntity) entity, cause)) {
            event.setCancelled(true);
            return;
        }

        double customNaturalDamage = getCustomNaturalDamage(cause, (LivingEntity) entity);
        if (customNaturalDamage > 0) {
            event.setDamage(customNaturalDamage);
        }

        EntityType entityType = event.getEntityType();

        if (entityType.equals(EntityType.PLAYER)) {
            Player player = (Player) entity;

            double finalDamage = event.getFinalDamage();
            if (PartyManager.inParty(player)) {
                Party party = PartyManager.getParty(player);
                party.getBoard().updateHP(player.getName(), (int) (player.getHealth() - finalDamage + 0.5));
            }
        } else if (entityType.equals(EntityType.WOLF) || entityType.equals(EntityType.HORSE)) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (PetManager.isPet(livingEntity)) {
                    PetManager.onPetTakeDamage(livingEntity, livingEntity.getHealth(), event.getFinalDamage());
                }
            }
        }
    }

    private double getCustomNaturalDamage(EntityDamageEvent.DamageCause cause, LivingEntity entity) {
        double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        //TODO balance natural damages
        if (cause.equals(EntityDamageEvent.DamageCause.FALL)) {
            //TODO fall damage
        } else if (cause.equals(EntityDamageEvent.DamageCause.POISON)) {
            return maxHealth / 100;
        } else if (cause.equals(EntityDamageEvent.DamageCause.WITHER)) {
            return maxHealth / 100;
        } else if (cause.equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
            return maxHealth / 100;
        } else if (cause.equals(EntityDamageEvent.DamageCause.FIRE)) {
            return maxHealth / 100;
        } else if (cause.equals(EntityDamageEvent.DamageCause.DROWNING)) {
            return maxHealth / 100;
        } else if (cause.equals(EntityDamageEvent.DamageCause.HOT_FLOOR)) {
            return maxHealth / 100;
        } else if (cause.equals(EntityDamageEvent.DamageCause.LAVA)) {
            return maxHealth / 100;
        } else if (cause.equals(EntityDamageEvent.DamageCause.CONTACT)) {
            return maxHealth / 100;
        }
        return -1;
    }

}
