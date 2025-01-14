package io.github.lix3nn53.guardiansofadelia.creatures.mythicmobs.mechanics;

import io.github.lix3nn53.guardiansofadelia.guardian.skill.component.mechanic.projectile.ProjectileCallback;
import io.github.lix3nn53.guardiansofadelia.guardian.skill.component.mechanic.projectile.ProjectileMechanicBase;
import io.github.lix3nn53.guardiansofadelia.guardian.skill.component.mechanic.projectile.SpreadType;
import io.github.lix3nn53.guardiansofadelia.utilities.particle.arrangement.ArrangementDrawCylinder;
import io.github.lix3nn53.guardiansofadelia.utilities.particle.arrangement.ParticleArrangement;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.Skill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MMMechanicProjectile extends SkillMechanic implements ITargetedEntitySkill, ProjectileCallback {

    protected final Optional<Skill> onHitSkill;
    private final ProjectileMechanicBase base;
    private SkillMetadata data;

    public MMMechanicProjectile(MythicLineConfig config) {
        super(config.getLine(), config);
        this.setAsyncSafe(false);
        this.setTargetsCreativePlayers(false);

        String onHitSkillName = config.getString(new String[]{"onHit"}, null);
        if (onHitSkillName != null) {
            this.onHitSkill = getPlugin().getSkillManager().getSkill(onHitSkillName);
        } else {
            this.onHitSkill = Optional.empty();
        }

        String projectileClass = config.getString(new String[]{"projectile", "p"}, "Arrow");

        Class<? extends Projectile> projectileType = null;
        try {
            projectileType = (Class<? extends Projectile>) Class.forName("org.bukkit.entity." + projectileClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        SpreadType spreadType = SpreadType.valueOf(config.getString(new String[]{"spreadType", "st"}, "CONE"));
        float speed = config.getFloat(new String[]{"speed", "s"}, 1);

        List<Integer> amountList = new ArrayList<>();
        int amount = config.getInteger(new String[]{"amount", "a"}, 1);
        amountList.add(amount);

        String amountValueKey = config.getString(new String[]{"amountValueKey"}, null);
        float angle = config.getFloat(new String[]{"angle"}, 30);
        float range = config.getFloat(new String[]{"range"}, 200);
        boolean mustHitToWork = config.getBoolean(new String[]{"mustHitToWork"}, false);

        float radius = config.getFloat(new String[]{"radius"}, 0);
        float height = config.getFloat(new String[]{"height"}, 0);

        //Particle projectile
        ParticleArrangement particleArrangement = null;

        String particleStr = config.getString(new String[]{"particle"}, null);
        if (particleStr != null) {
            Particle particle = Particle.valueOf(particleStr);

            int dustColor = config.getInteger(new String[]{"dustColor"}, 10040319);
            int dustSize = config.getInteger(new String[]{"dustSize"}, 1);

            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(dustColor), dustSize);

            particleArrangement = new ArrangementDrawCylinder(particle, dustOptions, 1, 1, 1, 1, 1);
        }

        float upward = config.getFloat(new String[]{"upward"}, 0);

        //custom options
        boolean addCasterAsFirstTargetIfHitSuccess = config.getBoolean(new String[]{"addCasterAsFirstTargetIfHitSuccess"}, false);
        boolean addCasterAsSecondTargetIfHitFail = config.getBoolean(new String[]{"addCasterAsSecondTargetIfHitFail"}, false);

        boolean isProjectileInvisible = config.getBoolean(new String[]{"isProjectileInvisible"}, false);

        // Disguise
        Optional<Material> disguiseMaterial = Optional.empty();
        String disguiseMaterialStr = config.getString(new String[]{"disguiseMaterial"}, null);
        if (disguiseMaterialStr != null) {
            disguiseMaterial = Optional.of(Material.valueOf(disguiseMaterialStr));
        }

        int disguiseCustomModelData = config.getInteger(new String[]{"disguiseCustomModelData"}, 1);

        this.base = new ProjectileMechanicBase(projectileType, spreadType, radius, height, speed, amountList,
                amountValueKey, angle, upward, range, mustHitToWork, particleArrangement, isProjectileInvisible,
                disguiseMaterial, disguiseCustomModelData, addCasterAsFirstTargetIfHitSuccess, addCasterAsSecondTargetIfHitFail);
    }

    @Override
    public boolean castAtEntity(SkillMetadata data, AbstractEntity abstractEntity) {
        this.data = data;

        LivingEntity caster = (LivingEntity) data.getCaster().getEntity().getBukkitEntity();
        ArrayList<LivingEntity> targets = new ArrayList<>();
        targets.add(caster);

        return this.base.execute(caster, 1, targets, base.getSkillIndex(), this);
    }

    @Override
    public ArrayList<LivingEntity> callback(Projectile projectile, Entity hit) {
        ArrayList<LivingEntity> targets = this.base.callback(projectile, hit);

        if (targets.isEmpty()) return targets;

        List<AbstractEntity> abstractTargets = new ArrayList<>();
        List<AbstractLocation> abstractLocations = new ArrayList<>();

        for (LivingEntity target : targets) {
            AbstractEntity adapt = BukkitAdapter.adapt(target);
            abstractTargets.add(adapt);

            Location location = target.getLocation();
            AbstractLocation adapt1 = BukkitAdapter.adapt(location);
            abstractLocations.add(adapt1);
        }

        if (onHitSkill.isPresent()) {
            Skill skill = onHitSkill.get();

            SkillMetadata sData = this.data.deepClone();
            sData.setOrigin(abstractTargets.get(0).getLocation());
            sData.setEntityTargets(abstractTargets);
            sData.setLocationTargets(abstractLocations);
            if (skill.isUsable(sData)) {
                skill.execute(sData);
            }
        }

        return targets;
    }
}
