package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.WeaponGroupSpec;
import com.fs.starfarer.api.loading.WeaponGroupType;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class BeamReflector extends BaseHullMod {
    float BEAM_ABSORPTION = 0.1f;
    Map<String, ShipAPI> beamdroneMap = new HashMap<>();
    Map<String, ShipAPI> updatedDronesMap = new HashMap<>();

    Map<String, Integer> removeCandidates = new HashMap<>();

    final Color transparent = new Color(0,0,0,0);

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getBeamWeaponDamageMult().modifyMult(id, BEAM_ABSORPTION);
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {

        CombatEngineAPI engine = Global.getCombatEngine();

        for (BeamAPI beam : engine.getBeams()) {

            float reflectedBeamRange = beam.getWeapon().getRange() - beam.getLength();

            if (beam.getDamageTarget() == ship && 100f < reflectedBeamRange) {
                Vector2f reflectionVector = getReflectDirection(beam, ship);

                if (reflectionVector != null) {
                    // look for beam drone
                    ShipAPI beamDrone = beamdroneMap.get(getBeamId(beam));

                    if (beamDrone == null) {
                        beamDrone = createBeamDrone(ship, beam, reflectionVector);

                        beamdroneMap.put(getBeamId(beam), beamDrone);
                        engine.addEntity(beamDrone);
                    }

                    float weaponDisplacement = (float)Math.sqrt((beam.getFrom().x - beam.getWeapon().getLocation().x) * (beam.getFrom().x - beam.getWeapon().getLocation().x)
                            + (beam.getFrom().y - beam.getWeapon().getLocation().y) * (beam.getFrom().y - beam.getWeapon().getLocation().y));

                    // configure beam drone
                    updateBeamDrone(
                            beamDrone,
                            beam.getTo(),
                            reflectionVector,
                            reflectedBeamRange,
                            weaponDisplacement
                    );

                    Global.getLogger(BeamReflector.class).info(String.format("Beam drone was updated for beam %s", getBeamId(beam)));

                    updatedDronesMap.put(getBeamId(beam), beamDrone);
                }
            }
        }

        ArrayList<String> removedIds = new ArrayList<>();

        for(String beamId : removeCandidates.keySet()) {
            if(4 < removeCandidates.get(beamId)) {
                Global.getLogger(BeamReflector.class).info(String.format("Beam drone for beam %s was removed", beamId));
                engine.removeEntity(beamdroneMap.get(beamId));
                beamdroneMap.remove(beamId);
                removedIds.add(beamId);
            }
        }

        for (String beamId : removedIds) {
            removeCandidates.remove(beamId);
        }

        // Clean up
        for (String beamId : beamdroneMap.keySet()) {
            if (!updatedDronesMap.containsKey(beamId)) {
                if(removeCandidates.containsKey(beamId)) {
                    removeCandidates.put(beamId, removeCandidates.get(beamId) + 1);
                } else {
                    removeCandidates.put(beamId, 1);
                }
            } else {
                removeCandidates.remove(beamId);
            }
        }

        updatedDronesMap.clear();
    }

    String getBeamId(BeamAPI beam){
        return beam.getSource().getName() + "_" + beam.getWeapon().getSlot().getId();
    }

    private void updateBeamDrone(ShipAPI beamDrone, Vector2f hitLocation, Vector2f direction, float reflectedBeamRange, float weaponDisplacement) {
        Vector2f target = new Vector2f(beamDrone.getLocation().x + direction.x, beamDrone.getLocation().y + direction.y);

        float directionLength = direction.length();

        beamDrone.getLocation().set(
                hitLocation.x - weaponDisplacement * direction.x / directionLength,
                hitLocation.y - weaponDisplacement * direction.y / directionLength);
        beamDrone.giveCommand(ShipCommand.FIRE, target, 0);

        WeaponAPI beamDroneWeapon = beamDrone.getWeaponGroupsCopy().get(0).getWeaponsCopy().get(0);
        for (BeamAPI beam : beamDroneWeapon.getBeams()){
            if(beam instanceof CombatEntityAPI) {
                ((CombatEntityAPI)beam).setCollisionClass(CollisionClass.RAY_FIGHTER);
            }
        }

        StatBonus mutableWeaponRangeBonus = beamDrone.getMutableStats().getBeamWeaponRangeBonus();
        mutableWeaponRangeBonus.modifyFlat("beam_reflector", reflectedBeamRange - beamDroneWeapon.getRange() + mutableWeaponRangeBonus.getFlatBonus());
    }

    private ShipAPI createBeamDrone(ShipAPI owner, BeamAPI beam, Vector2f reflectionVector) {
        CombatEngineAPI engine = Global.getCombatEngine();
        SettingsAPI settingsAPI = Global.getSettings();

        //Get ship variant
        ShipVariantAPI beamDroneVariant = Global.getSettings().createEmptyVariant("beam_drone", settingsAPI.getHullSpec("beam_drone"));

        // Setting up weapon
        WeaponGroupSpec weaponGroupSpec = new WeaponGroupSpec(WeaponGroupType.LINKED);
        weaponGroupSpec.addSlot("WS 001");
        beamDroneVariant.addWeapon("WS 001", beam.getWeapon().getId());
        beamDroneVariant.addWeaponGroup(weaponGroupSpec);

        ShipAPI beamDrone = engine.createFXDrone(beamDroneVariant);
        beamDrone.setLayer(CombatEngineLayers.FIGHTERS_LAYER);
        beamDrone.setCollisionClass(CollisionClass.NONE);
        beamDrone.setOwner(owner.getOriginalOwner());

        //Apply stats
        beamDrone.getMutableStats().getArmorDamageTakenMult().modifyMult("beamdrone", 0f);
        beamDrone.getMutableStats().getHullDamageTakenMult().modifyMult("beamdrone", 0f);
        beamDrone.getMutableStats().getShieldDamageTakenMult().modifyMult("beamdrone", 0f);

        beamDrone.getMutableStats().getBeamWeaponDamageMult().modifyMult("beamdrone", 1f - BEAM_ABSORPTION);
        beamDrone.getMutableStats().getBeamWeaponFluxCostMult().modifyMult("beamdrone", 0);


        WeaponAPI beamDroneWeapon = beamDrone.getWeaponGroupsCopy().get(0).getWeaponsCopy().get(0);
        beamDroneWeapon.setTurnRateOverride(1000f);

        float beamStartDispositionX = beam.getFrom().x - beam.getWeapon().getLocation().x;
        float beamStartDispositionY = beam.getFrom().y - beam.getWeapon().getLocation().y;


        float beamLength = beam.getLength();
        float beamX = beam.getTo().x - beam.getFrom().x;
        float beamY = beam.getTo().y - beam.getFrom().y;

        float reflectionVectorLength = reflectionVector.length();

        float cosTheta = (reflectionVector.x * beamX + reflectionVector.y * beamY) / (beamLength * reflectionVectorLength);
        float sinTheta = (beamX * reflectionVector.y) - (beamY * reflectionVector.x) / (beamLength * reflectionVectorLength);

        float rotatedBeamStartDispositionX = beamStartDispositionX * cosTheta + beamStartDispositionY * sinTheta;
        float rotatedBeamStartDispositionY = - beamStartDispositionX * sinTheta + beamStartDispositionY * cosTheta;

        beamDroneWeapon.getLocation().set(100f, -100f);

        beamDroneWeapon.getSprite().setColor(transparent);
        beamDroneWeapon.getGlowSpriteAPI().setColor(transparent);

        Global.getLogger(BeamReflector.class).info(String.format("Beam drone was created for beam %s", getBeamId(beam)));

        return beamDrone;
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

        return null;
    }
    //VFX drones are the way to do this apparently
    //pain and suffering ; (


    private BoundsAPI.SegmentAPI getIntersectedSegment(BeamAPI beam, ShipAPI ship) {
        if (beam.getDamageTarget() != ship) {
            return null;
        }
        for (BoundsAPI.SegmentAPI segment : ship.getExactBounds().getSegments()) {
            if (MathUtils.isPointOnLine(beam.getTo(), segment.getP1(), segment.getP2())) {
                return segment;
            }
        }
        return null;
    }
}