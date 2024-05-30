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
    public static final float BEAM_ABSORPTION = 0.0f;
    private ReflectionManager reflectionManager;

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        super.applyEffectsAfterShipCreation(ship, id);
        reflectionManager = new ReflectionManager(Global.getCombatEngine(), ship, Global.getLogger(ReflectionManager.class));
        ship.getMutableStats().getArmorDamageTakenMult().modifyMult("ReflectivePlate", BEAM_ABSORPTION);
        ship.getMutableStats().getHullDamageTakenMult().modifyMult("ReflectivePlate", BEAM_ABSORPTION);
    }

    Map<String, Integer> removeCandidates = new HashMap<>();

    static final Color TRANSPARENT = new Color(0,0,0,0);

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        CombatEngineAPI engine = Global.getCombatEngine();

        if (engine.getShips().size() != 1
                && ship.isAlive()
                && !engine.isCombatOver()) {

            reflectionManager.drawBorders();
            reflectionManager.processBeams(engine.getBeams());
        } else {
//            Global.getLogger(ReflectivePlate.class).warn(String.format("Ships in combat %s", engine.getShips().size()));
//            Global.getLogger(ReflectivePlate.class).warn(String.format("Ship with plate: %s isAlive: %s", ship, ship.isAlive()));
//            Global.getLogger(ReflectivePlate.class).warn(String.format("Combat is over: %s", engine.isCombatOver()));
            reflectionManager.cleanup();
        }
    }
    static class ReflectionManager {
        int GENERATION_COUNT = 20;
        int currentGeneration;
        // Ship with reflective plate
        private final ShipAPI ship;
        private final CombatEngineAPI combatEngine;

        private final Logger logger;
        private final SettingsAPI settings;

        Map<Integer, ShipAPI> borderDrones = new HashMap<>();
        Map<BeamAPI, ShipAPI> reflectionDrones = new HashMap<>();
        Map<BeamAPI, Integer> reflectionGeneration = new HashMap<>();

        public ReflectionManager(CombatEngineAPI combatEngine, ShipAPI ship, Logger logger) {
            this.combatEngine = combatEngine;
            this.ship = ship;
            this.logger = logger;
            this.settings = Global.getSettings();
            logger.warn(String.format("ReflectionManager::init for %s", ship.getName()));
        }

        void drawBorders(){
            logger.warn(String.format("Updated ship location: %s", ship.getLocation()));
            logger.warn(String.format("updateBorderDrones segment count == %s", ship.getExactBounds().getSegments().size()));

            int segmentIdx = 0;
            for(BoundsAPI.SegmentAPI segment: ship.getExactBounds().getSegments()) {
                ShipAPI borderDrone = borderDrones.get(segmentIdx);
                if(borderDrone == null){
                    borderDrone = createBorderDrone();
                    borderDrones.put(segmentIdx, borderDrone);
                }

                borderDrone.getLocation().set(segment.getP1());
                logger.warn(String.format("Updated drone for point %s", segment.getP1()));

                segmentIdx++;
            }
        }

        private ShipAPI createBorderDrone () {
            SettingsAPI settings = Global.getSettings();

            ShipVariantAPI dotDroneVariant = Global.getSettings().createEmptyVariant("wasp_wing", settings.getHullSpec("wasp"));
            ShipAPI dotDrone = combatEngine.createFXDrone(dotDroneVariant);

            dotDrone.setLayer(CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER);
            dotDrone.setCollisionClass(CollisionClass.NONE);
            combatEngine.addEntity(dotDrone);
            dotDrone.setOwner(ship.getOwner());

            return dotDrone;
        }

        void processBeams(Collection<BeamAPI> beams) {
            currentGeneration = (currentGeneration + 1) % GENERATION_COUNT;
            for (BeamAPI beam : beams) {
                if(reflect(beam)) {
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

        boolean reflect(BeamAPI beam) {
            if(beam.getDamageTarget() != ship && beam.getDamageTarget() != ship.getParentStation()) {
                logger.error(String.format("Ship %s is not target %s", ship.getParentStation(), beam.getDamageTarget()));
                return false;
            }

            float reflectedBeamRange = beam.getWeapon().getRange() - beam.getLength();
            if(reflectedBeamRange < 100f) {
                logger.error(String.format("reflectedBeamRange %s is too short", reflectedBeamRange));
                return false;
            }

            Vector2f reflectionDirection = getReflectDirection(beam, ship);

            if(reflectionDirection == null) {
                logger.error("ReflectionManager::reflect reflectionDirection is null for some reson");
                return false;
            }

            logger.error(String.format("Reflecting beam reflectionDirection is %s", reflectionDirection));

            ShipAPI reflectionDrone =  getReflectionDrone(beam);

            float weaponDisplacement = reflectionDrone.getWeaponGroupsCopy().get(0).getWeaponsCopy().get(0).getSprite().getHeight() / 2f;

            reflectionDrone.getLocation().set(
                    beam.getTo().x - weaponDisplacement * beam.getTo().x / reflectedBeamRange,
                    beam.getTo().y - weaponDisplacement * beam.getTo().y / reflectedBeamRange);

            Vector2f target = new Vector2f(reflectionDrone.getLocation().x + reflectionDirection.x, reflectionDrone.getLocation().y + reflectionDirection.y);

            reflectionDrone.giveCommand(ShipCommand.FIRE, target, 0);

            WeaponAPI beamDroneWeapon = reflectionDrone.getWeaponGroupsCopy().get(0).getWeaponsCopy().get(0);

            ((CombatEntityAPI)beam).setCollisionClass(CollisionClass.RAY_FIGHTER);

            StatBonus mutableWeaponRangeBonus = reflectionDrone.getMutableStats().getBeamWeaponRangeBonus();
            mutableWeaponRangeBonus.modifyFlat("Reflective Plate", reflectedBeamRange - beamDroneWeapon.getRange() + mutableWeaponRangeBonus.getFlatBonus());

            reflectionGeneration.put(beam, currentGeneration);

            return true;
        }

        private ShipAPI getReflectionDrone(BeamAPI beam) {

            if(!reflectionDrones.containsKey(beam)) {
                ShipVariantAPI beamDroneVariant = settings.createEmptyVariant("pmm_beam_drone", settings.getHullSpec("pmm_beam_drone"));

                // Setting up weapon
                WeaponGroupSpec weaponGroupSpec = new WeaponGroupSpec(WeaponGroupType.LINKED);
                weaponGroupSpec.addSlot("WS 001");
                beamDroneVariant.addWeapon("WS 001", beam.getWeapon().getId());
                beamDroneVariant.addWeaponGroup(weaponGroupSpec);

                ShipAPI beamDrone = combatEngine.createFXDrone(beamDroneVariant);
                beamDrone.setLayer(CombatEngineLayers.FIGHTERS_LAYER);
                beamDrone.setCollisionClass(CollisionClass.NONE);
                beamDrone.setOwner(ship.getOriginalOwner());

                //Apply stats
                beamDrone.getMutableStats().getArmorDamageTakenMult().modifyMult("Reflective Plate", 0f);
                beamDrone.getMutableStats().getHullDamageTakenMult().modifyMult("Reflective Plate", 0f);
                beamDrone.getMutableStats().getShieldDamageTakenMult().modifyMult("Reflective Plate", 0f);

                beamDrone.getMutableStats().getBeamWeaponDamageMult().modifyMult("Reflective Plate", 1f - BEAM_ABSORPTION);
                beamDrone.getMutableStats().getBeamWeaponFluxCostMult().modifyMult("Reflective Plate", 0);

    //            WeaponAPI beamDroneWeapon = beamDrone.getWeaponGroupsCopy().get(0).getWeaponsCopy().get(0);

    //            beamDroneWeapon.getSprite().setColor(TRANSPARENT);
    //            beamDroneWeapon.getGlowSpriteAPI().setColor(TRANSPARENT);

                combatEngine.addEntity(beamDrone);

                reflectionDrones.put(beam, beamDrone);
                logger.warn("getReflectionDrone: creating drone");
            } else {
                logger.warn("getReflectionDrone: reusing drone");
            }

            return reflectionDrones.get(beam);
        }

        public Vector2f getReflectDirection(BeamAPI beam, ShipAPI ship) { // direction vector
            BoundsAPI.SegmentAPI segment = getIntersectedSegment(beam, ship);
            if (segment != null) {
                Vector2f beamOrigin = beam.getFrom();
                Vector2f hitLocation = beam.getTo();

                Vector2f segmentPoint = segment.getP1(); // P2 should work also

                float standardizedBeamOriginX = beamOrigin.x - hitLocation.x;
                float standardizedBeamOriginY = beamOrigin.y - hitLocation.y;

                float standardizedSegmentPointX = segmentPoint.x - hitLocation.x;
                float standardizedSegmentPointY = segmentPoint.y - hitLocation.y;

                if (standardizedSegmentPointX == 0f) {
                    return new Vector2f(standardizedBeamOriginX, -standardizedBeamOriginY);
                }
                float perpendicularProjectionOfBeamOriginX = ((standardizedSegmentPointX * standardizedBeamOriginX + standardizedSegmentPointY * standardizedBeamOriginY) * standardizedSegmentPointX)
                        / (standardizedSegmentPointX * standardizedSegmentPointX + standardizedSegmentPointY * standardizedSegmentPointY);

                float perpendicularProjectionOfBeamOriginY = (standardizedSegmentPointY / standardizedSegmentPointX) * perpendicularProjectionOfBeamOriginX;

                float reflectedX = standardizedBeamOriginX - 2 * perpendicularProjectionOfBeamOriginX;
                float reflectedY = standardizedBeamOriginY - 2 * perpendicularProjectionOfBeamOriginY;

                return new Vector2f(reflectedX, reflectedY);
            }

            logger.error("ReflectionManager::getReflectDirection couldn't find a segment to reflect");
            return null;
        }

        private BoundsAPI.SegmentAPI getIntersectedSegment(BeamAPI beam, ShipAPI ship) {
            for (BoundsAPI.SegmentAPI segment : ship.getExactBounds().getSegments()) {
                if (isPointOnTheLine(segment.getP1(), segment.getP2(), beam.getTo())) {
                    return segment;
                }
            }
            logger.error("ReflectionManager::getIntersectedSegment couldn't find a segment to reflect");
            return null;
        }

        public boolean isPointOnTheLine(Vector2f lineStart, Vector2f lineEnd, Vector2f point) {
            // Adjust vectors relative to x1,y1

            logger.warn(String.format("Check if segment [%s, %s] contains point : %s", lineStart, lineEnd, point));

            float squaredDistanceToLineStart = (point.x - lineStart.x) * (point.x - lineStart.x)
                    + (point.y - lineStart.y) * (point.y - lineStart.y);

            if(squaredDistanceToLineStart < 10f) {
                logger.warn("true");
                return true;
            }

            float squaredDistanceToLineEnd = (point.x - lineEnd.x) * (point.x - lineEnd.x)
                    + (point.y - lineEnd.y) * (point.y - lineEnd.y);

            if(squaredDistanceToLineEnd < 10f) {
                logger.warn("true");
                return true;
            }

            float squaredLineLength = (lineEnd.x - lineStart.x) * (lineEnd.x - lineStart.x)
                    + (lineEnd.y - lineStart.y) * (lineEnd.y - lineStart.y);

            boolean result = Math.sqrt(squaredDistanceToLineStart)
                    + Math.sqrt(squaredDistanceToLineEnd)
                    - Math.sqrt(squaredLineLength) < squaredLineLength / 1000.0;
            logger.warn(result);

            return result;
        }

        void cleanup(){
            // place to clear caches remove entities and so on
            if(reflectionDrones.isEmpty() && borderDrones.isEmpty()) {
                return;
            }

            for(ShipAPI reflectDrone : reflectionDrones.values()) {
                combatEngine.removeEntity(reflectDrone);
            }
            reflectionDrones.clear();
            reflectionGeneration.clear();
            currentGeneration = 0;

            for(ShipAPI drone : borderDrones.values()){
                combatEngine.removeEntity(drone);
            }
            borderDrones.clear();
        }
    }
}