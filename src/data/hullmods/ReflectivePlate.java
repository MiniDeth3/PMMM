package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.WeaponGroupSpec;
import com.fs.starfarer.api.loading.WeaponGroupType;
import org.apache.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ReflectivePlate extends BaseHullMod {
    public static final float BEAM_ABSORPTION = 0.5f;
    private ReflectionManager reflectionManager;

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        super.applyEffectsAfterShipCreation(ship, id);
        reflectionManager = new ReflectionManager(Global.getCombatEngine(), ship, Global.getLogger(ReflectionManager.class));
        ship.getMutableStats().getArmorDamageTakenMult().modifyMult("ReflectivePlate", BEAM_ABSORPTION);
        ship.getMutableStats().getHullDamageTakenMult().modifyMult("ReflectivePlate", BEAM_ABSORPTION);
    }

    static final Color TRANSPARENT = new Color(0,0,0,0);

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        CombatEngineAPI engine = Global.getCombatEngine();

        if (engine.getShips().size() != 1
                && ship.isAlive()
                && !engine.isCombatOver()) {
            reflectionManager.processBeams(engine.getBeams());
        } else {
//            Global.getLogger(ReflectivePlate.class).warn(String.format("Ships in combat %s", engine.getShips().size()));
//            Global.getLogger(ReflectivePlate.class).warn(String.format("Ship with plate: %s isAlive: %s", ship, ship.isAlive()));
//            Global.getLogger(ReflectivePlate.class).warn(String.format("Combat is over: %s", engine.isCombatOver()));
            reflectionManager.cleanup();
        }
    }
    static class ReflectionManager {
        int GENERATION_COUNT = 1000;
        int currentGeneration;
        // Ship with reflective plate
        private final ShipAPI ship;
        private final CombatEngineAPI combatEngine;

        private final Logger logger;
        private final SettingsAPI settings;


        Map<BeamAPI, ShipAPI> reflectionDrones = new HashMap<>();
        Map<BeamAPI, Integer> reflectionGeneration = new HashMap<>();

        public ReflectionManager(CombatEngineAPI combatEngine, ShipAPI ship, Logger logger) {
            this.combatEngine = combatEngine;
            this.ship = ship;
            this.logger = logger;
            this.settings = Global.getSettings();
            logger.warn(String.format("ReflectionManager::init for %s", ship.getName()));
        }

        void processBeams(Collection<BeamAPI> beams) {
            currentGeneration = (currentGeneration + 1) % GENERATION_COUNT;
            BoundsAPI.SegmentAPI segment = null;
            for (BeamAPI beam : beams) {
                segment = reflect(beam);
                if(segment != null) {
                    logger.warn(String.format("Beam [%s, %s] was reflected", beam.getFrom(), beam.getTo()));
                }
            }
            treatCaches();
        }

        void treatCaches() {
            List<BeamAPI> reflectDronesToDelete = new ArrayList<>();

            for(Map.Entry<BeamAPI, Integer> meow : reflectionGeneration.entrySet()) {
                int age = (GENERATION_COUNT + currentGeneration - meow.getValue()) % GENERATION_COUNT;
                if(age > 10) {
                    reflectDronesToDelete.add(meow.getKey());
                }
            }

            for(BeamAPI key : reflectDronesToDelete) {
                combatEngine.removeEntity(reflectionDrones.get(key));
                reflectionDrones.remove(key);
                reflectionGeneration.remove(key);
            }
        }

        BoundsAPI.SegmentAPI reflect(BeamAPI beam) {
            if(beam.getDamageTarget() != ship && beam.getDamageTarget() != ship.getParentStation()) {
                logger.error(String.format("Ship %s is not target %s", ship.getParentStation(), beam.getDamageTarget()));
                return null;
            }

            float reflectedBeamRange = beam.getWeapon().getRange() - beam.getLength();
            if(reflectedBeamRange < 100f) {
                logger.error(String.format("reflectedBeamRange %s is too short", reflectedBeamRange));
                return null;
            }

            Vector2f reflectionDirection = getReflectDirection(beam, ship, reflectedBeamRange);

            if(reflectionDirection == null) {
                logger.error("ReflectionManager::reflect reflectionDirection is null for some reson");
                return null;
            }

            logger.error(String.format("Reflecting beam reflectionDirection is %s", reflectionDirection));

            ShipAPI reflectionDrone =  getReflectionDrone(beam);

            float weaponDisplacement = (float)Math.sqrt((beam.getFrom().x - beam.getWeapon().getLocation().x) * (beam.getFrom().x - beam.getWeapon().getLocation().x)
                    + (beam.getFrom().y - beam.getWeapon().getLocation().y) * (beam.getFrom().y - beam.getWeapon().getLocation().y));

            reflectionDrone.getLocation().set(
                    beam.getTo().x - weaponDisplacement * reflectionDirection.x / reflectionDirection.length(),
                    beam.getTo().y - weaponDisplacement * reflectionDirection.y / reflectionDirection.length());

            Vector2f target = new Vector2f(
                    reflectionDrone.getLocation().x + reflectionDirection.x,
                    reflectionDrone.getLocation().y + reflectionDirection.y);

            reflectionDrone.giveCommand(ShipCommand.FIRE, target, 0);

            WeaponAPI beamDroneWeapon = reflectionDrone.getWeaponGroupsCopy().get(0).getWeaponsCopy().get(0);
            beamDroneWeapon.setTurnRateOverride(1000f);

            ((CombatEntityAPI)beam).setCollisionClass(CollisionClass.RAY_FIGHTER);

            StatBonus mutableWeaponRangeBonus = reflectionDrone.getMutableStats().getBeamWeaponRangeBonus();
            mutableWeaponRangeBonus.modifyFlat("Reflective Plate", reflectedBeamRange - beamDroneWeapon.getRange() + mutableWeaponRangeBonus.getFlatBonus());

            reflectionGeneration.put(beam, currentGeneration);

            return null;
        }

        private ShipAPI getReflectionDrone(BeamAPI beam) {

            if(!reflectionDrones.containsKey(beam)) {
                ShipAPI beamDrone = createReflectDrone(beam.getWeapon().getId());
                reflectionDrones.put(beam, beamDrone);
                logger.warn("getReflectionDrone: creating drone");
            } else {
                logger.warn("getReflectionDrone: reusing drone");
            }

            return reflectionDrones.get(beam);
        }

        ShipAPI createReflectDrone(String weaponId) {
            ShipVariantAPI beamDroneVariant = settings.createEmptyVariant("pmm_beam_drone", settings.getHullSpec("pmm_beam_drone"));

            // Setting up weapon
            WeaponGroupSpec weaponGroupSpec = new WeaponGroupSpec(WeaponGroupType.LINKED);
            weaponGroupSpec.addSlot("WS 001");
            beamDroneVariant.addWeapon("WS 001", weaponId);
            beamDroneVariant.addWeaponGroup(weaponGroupSpec);

            ShipAPI beamDrone = combatEngine.createFXDrone(beamDroneVariant);
            beamDrone.setLayer(CombatEngineLayers.FIGHTERS_LAYER);
            beamDrone.setCollisionClass(CollisionClass.NONE);
            beamDrone.setOwner(ship.getOriginalOwner());

            //Apply stats
            beamDrone.getMutableStats().getBeamWeaponDamageMult().modifyMult("Reflective Plate", 1f - BEAM_ABSORPTION);
            beamDrone.getMutableStats().getBeamWeaponFluxCostMult().modifyMult("Reflective Plate", 0);

            WeaponAPI beamDroneWeapon = beamDrone.getWeaponGroupsCopy().get(0).getWeaponsCopy().get(0);

            beamDroneWeapon.getSprite().setColor(TRANSPARENT);
            beamDroneWeapon.getGlowSpriteAPI().setColor(TRANSPARENT);

            combatEngine.addEntity(beamDrone);

            return beamDrone;
        }
        public Vector2f getReflectDirection(BeamAPI beam, ShipAPI ship, float beamLength) { // direction vector
            double facingAngleRadian = Math.PI * ship.getFacing()/ 180.0;
            return new Vector2f((float)(beamLength * Math.cos(facingAngleRadian)), (float)(beamLength * Math.sin(facingAngleRadian)));
        }

        void cleanup(){
            // place to clear caches remove entities and so on
            if(reflectionDrones.isEmpty()) {
                return;
            }

            for(ShipAPI reflectDrone : reflectionDrones.values()) {
                combatEngine.removeEntity(reflectDrone);
            }
            reflectionDrones.clear();
            reflectionGeneration.clear();
            currentGeneration = 0;
        }
    }
}