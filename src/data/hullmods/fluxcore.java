package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class fluxcore extends BaseHullMod {

    // Constants for arc damage and EMP
    public static final float ARC_DAM = 200f;
    public static final float ARC_EMP = 50f;
    public static final float ARC_DAM_SELF = 350f;
    public static final float ARC_EMP_SELF = 0f;
    public static final float HARD_DISS = 20f;

    // Lightning colors
    public static final Color LIGHTNING_CORE_COLOR = new Color(255, 150, 255, 255);
    public static final Color LIGHTNING_FRINGE_COLOR = new Color(255, 50, 255, 255);

    // Interval utilities
    private IntervalUtil zapInterval = new IntervalUtil(2f, 2.5f);
    private IntervalUtil selfzapInterval = new IntervalUtil(0.2f, 0.4f);

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {

        // Check if the ship is overloaded or if flux level is high
        if (ship.getFluxTracker().isOverloaded() || ship.getFluxTracker().getFluxLevel() > 0.8f) {
            ship.getMutableStats().getHardFluxDissipationFraction().modifyFlat(ship.getId(), HARD_DISS * 0.01f);
            Global.getSoundPlayer().playLoop("emp_loop", ship, 0.8f, 0.8f, ship.getLocation(), ship.getVelocity());

            // Handle self zap
            selfzapInterval.advance(amount);
            if (selfzapInterval.intervalElapsed()) {
                selfzapInterval.setInterval(0.3f, 0.6f);

                List<WeaponSlotAPI> vents = new ArrayList<>();
                for (WeaponSlotAPI weaponSlot : ship.getHullSpec().getAllWeaponSlotsCopy()) {
                    if (weaponSlot.isSystemSlot()) {
                        vents.add(weaponSlot);
                    }
                }

                // If vents exist, spawn EMP arc to self
                if (!vents.isEmpty()) {
                    Vector2f sourcePoint = vents.get(MathUtils.getRandomNumberInRange(0, vents.size() - 1)).computePosition(ship);

                    Global.getCombatEngine().spawnEmpArcPierceShields(ship, sourcePoint, ship, ship,
                            DamageType.ENERGY, MathUtils.getRandomNumberInRange(0.8f, 1.2f) * ARC_DAM_SELF,
                            MathUtils.getRandomNumberInRange(0.8f, 1.2f) * ARC_EMP_SELF, 100000,
                            "system_emp_emitter_impact", 20f, LIGHTNING_CORE_COLOR, LIGHTNING_FRINGE_COLOR);
                }
            }
        } else if (ship.getFluxTracker().getFluxLevel() < 0.8f) {
            ship.getMutableStats().getHardFluxDissipationFraction().unmodify();
        }

        // Check if flux level is above threshold for EMP arcs
        if (ship.getFluxTracker().getFluxLevel() > 0.15f) {
            // Play sound based on flux level
            Global.getSoundPlayer().playLoop("system_high_energy_focus_loop", ship, ship.getFluxLevel() * 2.5f, ship.getFluxLevel() + 0.6f, ship.getLocation(), ship.getVelocity());
            Global.getSoundPlayer().playLoop("system_emp_emitter_loop", ship, ship.getFluxLevel() + 0.5f, ship.getFluxLevel() + 0.1f, ship.getLocation(), ship.getVelocity());

            float EMP_arc_speed_level = (0.9f - ship.getFluxTracker().getFluxLevel());

            // Set lightning range
            float actualLightningRange = 800f;

            // Avoid spawning EMP arcs while overloaded or dead
            if (!ship.isHulk()) {
                zapInterval.advance(amount);

                // Check if we should spawn an EMP arc
                if (zapInterval.intervalElapsed()) {
                    zapInterval.setInterval(EMP_arc_speed_level, EMP_arc_speed_level * 1.5f);

                    List<WeaponSlotAPI> vents = new ArrayList<>();
                    for (WeaponSlotAPI weaponSlot : ship.getHullSpec().getAllWeaponSlotsCopy()) {
                        if (weaponSlot.isSystemSlot()) {
                            vents.add(weaponSlot);
                        }
                    }

                    // If vents exist, spawn EMP arc to random valid target
                    if (!vents.isEmpty()) {
                        Vector2f sourcePoint = vents.get(MathUtils.getRandomNumberInRange(0, vents.size() - 1)).computePosition(ship);

                        List<CombatEntityAPI> validTargets = new ArrayList<>();
                        for (CombatEntityAPI entity : CombatUtils.getEntitiesWithinRange(sourcePoint, actualLightningRange)) {
                            if (entity instanceof ShipAPI || entity instanceof AsteroidAPI || entity instanceof MissileAPI) {
                                // Exclude invalid targets
                                if (entity instanceof ShipAPI) {
                                    if (((ShipAPI) entity).isPhased() || entity == ship || (entity.getOwner() == ship.getOwner())) {
                                        continue;
                                    }
                                }
                                if (entity instanceof MissileAPI) {
                                    if (entity.getOwner() == ship.getOwner() || ((MissileAPI) entity).isFizzling()) {
                                        continue;
                                    }
                                }
                                if (entity.getCollisionClass().equals(CollisionClass.NONE)) {
                                    continue;
                                }
                                validTargets.add(entity);
                            }
                        }

                        // If no valid targets, spawn arc to random point near ship
                        if (validTargets.isEmpty()) {
                            validTargets.add(new SimpleEntity(MathUtils.getRandomPointInCircle(sourcePoint, actualLightningRange)));
                        }

                        // Choose a random valid target
                        CombatEntityAPI target = validTargets.get(MathUtils.getRandomNumberInRange(0, validTargets.size() - 1));
                        Vector2f targetLocation = target.getLocation();

                        // Spawn the EMP arc
                        Global.getCombatEngine().spawnEmpArc(ship, sourcePoint, ship, target,
                                DamageType.ENERGY, MathUtils.getRandomNumberInRange(0.8f, 1.2f) * ARC_DAM,
                                MathUtils.getRandomNumberInRange(0.8f, 1.2f) * ARC_EMP, 100000, "", 20f,
                                LIGHTNING_CORE_COLOR, LIGHTNING_FRINGE_COLOR);

                        // Play impact sound
                        if (target instanceof SimpleEntity) {
                            Global.getSoundPlayer().playSound("system_emp_emitter_impact", 0.8f, 0.8f, targetLocation, new Vector2f());
                        } else {
                            Global.getSoundPlayer().playSound("system_emp_emitter_impact", 1f, 1f, targetLocation, new Vector2f());
                        }

                        validTargets.clear();
                    }
                }
            }
        }
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        switch (index) {
            case 0:
                return "15%";
            case 1:
                return (int) ARC_DAM + " Energy";
            case 2:
                return (int) ARC_EMP + " EMP";
            case 3:
                return "80%";
            case 4:
                return (int) ARC_DAM_SELF + " Energy";
            case 5:
                return (int) ARC_EMP_SELF + " EMP";
            default:
                return null;
        }
    }
}
