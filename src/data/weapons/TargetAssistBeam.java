package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.listeners.DamageListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TargetAssistBeam implements BeamEffectPlugin { // Let's assume it works

    // Edit this stuff to get effect you need
    private static final float beamEffectProgressionIncreasePerSecond = 20f;
    private static final float beamEffectProgressionDecreasePerSecond = 40f;

    private final Map<ShipAPI, Float> affectedShipsToProgression = new HashMap<>();


    public TargetAssistBeam() {
        Global.getLogger(TargetAssistBeam.class).warn("create TargetAssistBeam");
    }

    ShipAPI fonsi;

    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        final ShipAPI targetAssistDrone = beam.getWeapon().getShip();

        // Having several fonsi drones wouldn't work
        if(fonsi == null) {
            for (ShipAPI ship : engine.getShips()) {
                if (ship.getHullSpec().getHullId().equals("pmmm_derelict_fonsi")) {
                    fonsi = ship;
                    fonsi.addListener(new DamageListener() {
                        @Override
                        public void reportDamageApplied(Object source, CombatEntityAPI target, ApplyDamageResultAPI result) {
                            // We can implement threshold here something like "more than 100 damage per second"
                            if(target == fonsi) {
                                targetAssistDrone.setHoldFireOneFrame(true);
                            }
                        }
                    });
                }
            }
        }

        if(fonsi == null || !fonsi.isAlive()){
            for(ShipAPI affectedShip : affectedShipsToProgression.keySet()){
                applyBeamEffect(affectedShip, 0.0f);
            }
            affectedShipsToProgression.clear();
            beam.getWeapon().getShip().setHulk(true);
        } else {
            if(fonsi.getShipTarget() != targetAssistDrone.getShipTarget()) {
                targetAssistDrone.setShipTarget(fonsi.getShipTarget());
            }
        }

        CombatEntityAPI targetEntity = beam.getDamageTarget();
        if(targetEntity instanceof ShipAPI && !affectedShipsToProgression.containsKey(targetEntity)) {
            affectedShipsToProgression.put((ShipAPI)targetEntity, 0.0f);
        }

        List<ShipAPI> noLongerAffectedShips = new ArrayList<>();
        for(ShipAPI affectedShip : affectedShipsToProgression.keySet()){
            float updatedEffectPercent = affectedShipsToProgression.get(affectedShip);
            if(affectedShip == targetEntity) {
                updatedEffectPercent = Math.min(updatedEffectPercent + beamEffectProgressionIncreasePerSecond * amount, 100f);
                beam.setWidth(2f + 0.1f * updatedEffectPercent);
            } else {
                updatedEffectPercent = Math.max(updatedEffectPercent - beamEffectProgressionDecreasePerSecond * amount, 0.0f);
                if(updatedEffectPercent == 0.0f) {
                    noLongerAffectedShips.add(affectedShip);
                }
            }
            affectedShipsToProgression.put(affectedShip, updatedEffectPercent);
            applyBeamEffect(affectedShip, updatedEffectPercent);
        }

        for(ShipAPI noLongerAffectedShip: noLongerAffectedShips) {
            affectedShipsToProgression.remove(noLongerAffectedShip);
        }
    }

    public void applyBeamEffect(ShipAPI affectedShip, float beamEffectProgressionPercent) {
        if (affectedShip != null) {
            affectedShip.getMutableStats().getArmorDamageTakenMult().modifyMult("FonsiTargetingBeam", 1.0f + (0.5f * beamEffectProgressionPercent) / 100f);
            affectedShip.getMutableStats().getShieldDamageTakenMult().modifyMult("FonsiTargetingBeam", 1.0f + (0.5f * beamEffectProgressionPercent) / 100f);
            affectedShip.getMutableStats().getArmorDamageTakenMult().modifyMult("FonsiTargetingBeam", 1.0f + (0.5f * beamEffectProgressionPercent) / 100f);
            affectedShip.getMutableStats().getMaxSpeed().modifyMult("FonsiTargetingBeam", 1.0f - (0.5f * beamEffectProgressionPercent) / 100f);
            // Just an indicator
            affectedShip.setAlphaMult(1f - (0.8f * beamEffectProgressionPercent) / 100f);
            Global.getLogger(TargetAssistBeam.class).warn("affectedShip: " + affectedShip.getId() + " percent: " + beamEffectProgressionPercent);
        }
    }
}


