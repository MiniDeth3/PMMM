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
    public static final float BEAM_ABSORPTION = 0.01f;
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


            BoundsAPI.SegmentAPI segment = reflectionManager.processBeams(engine.getBeams());
            reflectionManager.drawBorders(segment);
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

        ShipAPI reflectionNormalDrone;
        int reflectionNormalDroneGeneration = 0;

        public ReflectionManager(CombatEngineAPI combatEngine, ShipAPI ship, Logger logger) {
            this.combatEngine = combatEngine;
            this.ship = ship;
            this.logger = logger;
            this.settings = Global.getSettings();
            this.reflectionNormalDrone = createReflectDrone("lrpdlaser");
            logger.warn(String.format("ReflectionManager::init for %s", ship.getName()));
        }

        void drawBorders(BoundsAPI.SegmentAPI givenSegment){
            logger.warn(String.format("Updated ship location: %s", ship.getLocation()));
            logger.warn(String.format("updateBorderDrones segment count == %s", ship.getExactBounds().getSegments().size()));

            int segmentIdx = 0;
            for(BoundsAPI.SegmentAPI segment: ship.getExactBounds().getSegments()) {
                ShipAPI borderDrone = borderDrones.get(segmentIdx);
                if(borderDrone == null){
                    borderDrone = createBorderDrone();
                    borderDrones.put(segmentIdx, borderDrone);
                }
                if(givenSegment != null
                        && (segment == givenSegment || segment.getP1().equals(givenSegment.getP2()))) {
                    borderDrone.setOverloadColor(Color.black);
                }
                borderDrone.getLocation().set(segment.getP1());
                logger.warn(String.format("Updated drone for point %s", segment.getP1()));

                segmentIdx++;
            }
        }

        private ShipAPI createBorderDrone () {
            SettingsAPI settings = Global.getSettings();

            ShipVariantAPI dotDroneVariant = Global.getSettings().createEmptyVariant("pmm_dot_green_drone", settings.getHullSpec("pmm_dot_green_drone"));
            ShipAPI dotDrone = combatEngine.createFXDrone(dotDroneVariant);

            dotDrone.setLayer(CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER);
            dotDrone.setCollisionClass(CollisionClass.NONE);
            combatEngine.addEntity(dotDrone);
            dotDrone.setOwner(ship.getOwner());

            return dotDrone;
        }

        BoundsAPI.SegmentAPI processBeams(Collection<BeamAPI> beams) {
            currentGeneration = (currentGeneration + 1) % GENERATION_COUNT;
            BoundsAPI.SegmentAPI segment = null;
            for (BeamAPI beam : beams) {
                segment = reflect(beam);
                if(segment != null) {
                    logger.warn(String.format("Beam [%s, %s] was reflected", beam.getFrom(), beam.getTo()));
                }
            }
            treatCaches();
            return segment;
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

            BoundsAPI.SegmentAPI segment = getIntersectedSegment(beam, ship);
            Vector2f reflectionDirection = getReflectDirection(beam, segment);

            if(reflectionDirection == null) {
                logger.error("ReflectionManager::reflect reflectionDirection is null for some reson");
                return null;
            }

            logger.error(String.format("Reflecting beam reflectionDirection is %s", reflectionDirection));

            ShipAPI reflectionDrone =  getReflectionDrone(beam);

            float weaponDisplacement = reflectionDrone.getWeaponGroupsCopy().get(0).getWeaponsCopy().get(0).getSprite().getHeight() / 2f;

            reflectionNormalDrone.getLocation().set(beam.getTo());

            reflectionNormalDrone.giveCommand(ShipCommand.FIRE, segment.getP1(), 0);

            reflectionDrone.getLocation().set(beam.getTo());

            Vector2f target = new Vector2f(reflectionDrone.getLocation().x + reflectionDirection.x, reflectionDrone.getLocation().y + reflectionDirection.y);


            reflectionDrone.giveCommand(ShipCommand.FIRE, target, 0);

            WeaponAPI beamDroneWeapon = reflectionDrone.getWeaponGroupsCopy().get(0).getWeaponsCopy().get(0);

            ((CombatEntityAPI)beam).setCollisionClass(CollisionClass.RAY_FIGHTER);

            StatBonus mutableWeaponRangeBonus = reflectionDrone.getMutableStats().getBeamWeaponRangeBonus();
            mutableWeaponRangeBonus.modifyFlat("Reflective Plate", reflectedBeamRange - beamDroneWeapon.getRange() + mutableWeaponRangeBonus.getFlatBonus());

            reflectionGeneration.put(beam, currentGeneration);

            return segment;
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
            beamDrone.getMutableStats().getArmorDamageTakenMult().modifyMult("Reflective Plate", 0f);
            beamDrone.getMutableStats().getHullDamageTakenMult().modifyMult("Reflective Plate", 0f);
            beamDrone.getMutableStats().getShieldDamageTakenMult().modifyMult("Reflective Plate", 0f);

            beamDrone.getMutableStats().getBeamWeaponDamageMult().modifyMult("Reflective Plate", 1f - BEAM_ABSORPTION);
            beamDrone.getMutableStats().getBeamWeaponFluxCostMult().modifyMult("Reflective Plate", 0);

            WeaponAPI beamDroneWeapon = beamDrone.getWeaponGroupsCopy().get(0).getWeaponsCopy().get(0);

            beamDroneWeapon.getSprite().setColor(TRANSPARENT);
            beamDroneWeapon.getGlowSpriteAPI().setColor(TRANSPARENT);

            combatEngine.addEntity(beamDrone);

            return beamDrone;
        }
        public Vector2f getReflectDirection(BeamAPI beam, BoundsAPI.SegmentAPI segment) { // direction vector

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

//                float reflectedX = standardizedBeamOriginX - 2 * perpendicularProjectionOfBeamOriginX;
//                float reflectedY = standardizedBeamOriginY - 2 * perpendicularProjectionOfBeamOriginY;

                return new Vector2f(perpendicularProjectionOfBeamOriginX, perpendicularProjectionOfBeamOriginY);
            }

            logger.error("ReflectionManager::getReflectDirection couldn't find a segment to reflect");
            return null;
        }

        private BoundsAPI.SegmentAPI getIntersectedSegment(BeamAPI beam, ShipAPI ship) {
            BoundsAPI.SegmentAPI bestCandidate = null;
            float bestCandidateDistance = 10f;

            for (BoundsAPI.SegmentAPI segment : ship.getExactBounds().getSegments()) {
                float distanceToTheSegmentSquared = calcSquaredDistanceToTheSegment(segment.getP1(), segment.getP2(), beam.getTo());
                if(distanceToTheSegmentSquared < bestCandidateDistance){
                    bestCandidateDistance = distanceToTheSegmentSquared;
                    bestCandidate = segment;
                }
            }
            logger.error("ReflectionManager::getIntersectedSegment couldn't find a segment to reflect");
            return bestCandidate;
        }

        public float calcSquaredDistanceToTheSegment(Vector2f lineStart, Vector2f lineEnd, Vector2f point) {
            float px = lineEnd.x - lineStart.x;
            float py = lineEnd.y - lineStart.y;
            float norm = px * px + py * py;

            float u =  ((point.x - lineStart.y) * px + (point.y - lineStart.y) * py) / norm;

            if (u > 1) {
                u = 1;
            } else if (u < 0) {
                u = 0;
            }

            float xClosest = lineStart.x + u * px;
            float yClosest = lineStart.y + u * py;

            float dx = point.x - xClosest;
            float dy = point.y - yClosest;

            return dx * dx + dy * dy;
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