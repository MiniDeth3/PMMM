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
    public static float ARC_DAM = 200f;
    public static float ARC_EMP = 50f;
    public static float ARC_DAM_SELF = 400f;
    public static float ARC_EMP_SELF = 0f;
    public static float HARD_DISS = 20f;

    public static Color LIGHTNING_CORE_COLOR = new Color(255, 150, 255, 255);
    public static Color LIGHTNING_FRINGE_COLOR = new Color(255, 50, 255, 255);

    private IntervalUtil zapInterval = new IntervalUtil(2f, 2.5f);
    private IntervalUtil selfzapInterval = new IntervalUtil (0.2f,0.4f);



    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (ship.getFluxTracker().isOverloaded() || (ship.getFluxTracker().getFluxLevel() > 0.8f)) {
            ship.getMutableStats().getHardFluxDissipationFraction().modifyFlat(ship.getId(), HARD_DISS * 0.01f);
            Global.getSoundPlayer().playLoop("emp_loop", ship, 1f,.6f, ship.getLocation(), ship.getVelocity());
            selfzapInterval.advance(amount);
            if (selfzapInterval.intervalElapsed()) {
                selfzapInterval.setInterval(0.3f, 0.6f);

                List<WeaponSlotAPI> vents = new ArrayList<WeaponSlotAPI>();
                for (WeaponSlotAPI weaponSlotAPI : ship.getHullSpec().getAllWeaponSlotsCopy()) {
                    if (weaponSlotAPI.isSystemSlot()) {
                        vents.add(weaponSlotAPI);
                    }
                }
                //If we have no vents, we can't do a dangerous overload this frame; ignore the rest of the code
                if (!vents.isEmpty()) {
                    Vector2f sourcePoint = vents.get(MathUtils.getRandomNumberInRange(0, vents.size() - 1)).computePosition(ship);

                    //And finally, fire at a random valid target
                    CombatEntityAPI selftarget = ship;
                    Global.getCombatEngine().spawnEmpArcPierceShields(ship, sourcePoint, ship, selftarget,
                            DamageType.ENERGY, //Damage type
                            MathUtils.getRandomNumberInRange(0.8f, 1.2f) * ARC_DAM_SELF, //Damage
                            MathUtils.getRandomNumberInRange(0.8f, 1.2f) * ARC_EMP_SELF, //Emp
                            100000, //Max range
                            "system_emp_emitter_impact", //Impact sound
                            20f, // thickness of the lightning bolt
                            LIGHTNING_CORE_COLOR, //Central color
                            LIGHTNING_FRINGE_COLOR //Fringe Color
                    );
                }
            }
        } else if (ship.getFluxTracker().isOverloaded() || (ship.getFluxTracker().getFluxLevel() < 0.8f)){
            ship.getMutableStats().getHardFluxDissipationFraction().unmodify();
        }

        if (ship.getFluxTracker().getFluxLevel() > 0.15f) {
            Global.getSoundPlayer().playLoop("system_high_energy_focus_loop", ship, ship.getFluxLevel() * 2.5f, ship.getFluxLevel() * 1.3f + 0.6f, ship.getLocation(), ship.getVelocity());
            Global.getSoundPlayer().playLoop("system_emp_emitter_loop", ship, ship.getFluxLevel() + 0.5f, (ship.getFluxLevel() + 0.1f), ship.getLocation(), ship.getVelocity());

            float EMP_arc_speed_level = (0.9f - ship.getFluxTracker().getFluxLevel());
            //        float EMP_arc_dmg_level = (1f + ship.getFluxTracker().getFluxLevel());

            //        IntervalUtil zapInterval = new IntervalUtil((EMP_arc_speed_level * 0.6f), (EMP_arc_speed_level * 0.8f));


            //Sets our hullsize-dependant variables
            float actualLightningRange = 800f;

            //Can't EMP while overloaded or Dead
            if (!ship.isHulk()); {


                zapInterval.advance(amount);
                //Checks if we should send lightning this frame
                if (zapInterval.intervalElapsed()) {
                    zapInterval.setInterval(EMP_arc_speed_level * 1f, EMP_arc_speed_level * 1.5f);

                    //Choose a random vent port to send lightning from
                    List<WeaponSlotAPI> vents = new ArrayList<WeaponSlotAPI>();
                    for (WeaponSlotAPI weaponSlotAPI : ship.getHullSpec().getAllWeaponSlotsCopy()) {
                        if (weaponSlotAPI.isSystemSlot()) {
                            vents.add(weaponSlotAPI);
                        }
                    }

                    //If we have no vents, we can't do a dangerous overload this frame; ignore the rest of the code
                    if (!vents.isEmpty()) {
                        Vector2f sourcePoint = vents.get(MathUtils.getRandomNumberInRange(0, vents.size() - 1)).computePosition(ship);

                        //Then, find all valid targets: we can only shoot missiles, ships and asteroids [including ourselves]
                        List<CombatEntityAPI> validTargets = new ArrayList<CombatEntityAPI>();
                        for (CombatEntityAPI entityToTest : CombatUtils.getEntitiesWithinRange(sourcePoint, actualLightningRange)) {
                            if (entityToTest instanceof ShipAPI || entityToTest instanceof AsteroidAPI || entityToTest instanceof MissileAPI) {
                                //Phased targets, and targets with no collision, are ignored
                                if (entityToTest instanceof ShipAPI) {
                                    if (((ShipAPI) entityToTest).isPhased() || entityToTest == ship || (entityToTest.getOwner() == ship.getOwner())) {
                                        continue;
                                    }
                                }
                                //This Should mean it targets enemy missiles and not friendly ones...?
                                if (entityToTest instanceof MissileAPI) {
                                    if ((entityToTest.getOwner() == ship.getOwner()) || (((MissileAPI) entityToTest).isFizzling())) {
                                        continue;
                                    }
                                }
                                if (entityToTest.getCollisionClass().equals(CollisionClass.NONE)) {
                                    continue;
                                }
                                validTargets.add(entityToTest);
                            }
                        }

                        //If we have no valid targets, zap a random point near us
                        if (validTargets.isEmpty()) {
                            validTargets.add(new SimpleEntity(MathUtils.getRandomPointInCircle(sourcePoint, actualLightningRange)));
                        }

                        //And finally, fire at a random valid target
                        CombatEntityAPI target = validTargets.get(MathUtils.getRandomNumberInRange(0, validTargets.size() - 1));
                        Vector2f NearTarget = target.getLocation();
                        Global.getCombatEngine().spawnEmpArc(ship, sourcePoint, ship, target,
                                DamageType.ENERGY, //Damage type
                                MathUtils.getRandomNumberInRange(0.8f, 1.2f) * ARC_DAM, //Damage
                                MathUtils.getRandomNumberInRange(0.8f, 1.2f) * ARC_EMP, //Emp
                                100000, //Max range
                                "", //Impact sound
                                20f, // thickness of the lightning bolt
                                LIGHTNING_CORE_COLOR, //Central color
                                LIGHTNING_FRINGE_COLOR //Fringe Color
                        );
                        validTargets.clear();

                        // Fixes sound not playing on hitting nothing (Thanks Vanilla!)
                        if (target instanceof SimpleEntity){
                            Global.getSoundPlayer().playSound("system_emp_emitter_impact", 0.8f, 0.8f, NearTarget, new Vector2f());
                        } else {
                            Global.getSoundPlayer().playSound("system_emp_emitter_impact", 1f, 1f, NearTarget, new Vector2f());
                        }
                    }
                    vents.clear();
                }
            }
        }
    }
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return "" + (int) 15f + "%";
        if (index == 1) return "" + (int) ARC_DAM + " Energy";
        if (index == 2) return "" + (int) ARC_EMP + " EMP";
        if (index == 3) return "" + (int) 80f + "%";
        if (index == 4) return "" + (int) ARC_DAM_SELF + " Energy";
        if (index == 5) return "" + (int) ARC_EMP_SELF + " EMP";

        return null;
    }
}