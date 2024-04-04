package data.weapons;

import com.fs.starfarer.api.combat.*;

public class TargetAssistBeam implements EveryFrameWeaponEffectPlugin { // Let's assume it works

    private float beamHitDuration = 0f;

    ShipAPI savedTarget = null;
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        ShipAPI droneShip = weapon.getShip();
        ShipAPI fonsi = droneShip.getDroneSource();

        if(fonsi == null || !fonsi.isAlive()){
            // Self-destruction
            droneShip.setHulk(true);
        }

        ShipAPI target = null;
        if(!weapon.getBeams().isEmpty()){
            CombatEntityAPI targetEntity = weapon.getBeams().get(0).getDamageTarget();
            if(targetEntity instanceof ShipAPI) {
                target = (ShipAPI)targetEntity;
                savedTarget = target;
            }
        }

        if(target == null) {
            if(savedTarget != null) {
                beamHitDuration = 0f;
                updateLevel(savedTarget, levelBeamHitDuration(beamHitDuration));
            }
        } else if(target.hasTag("FonsiTargetingBeam")) {
            target.addTag(getTag(levelBeamHitDuration(beamHitDuration)));
            beamHitDuration += amount;
        } else {
            target.addTag("FonsiTargetingBeam");
        }
    }

    String getTag(int level){
        return String.format("FonsiTargetingBeam_Level_%s", level);
    }

    void updateLevel(ShipAPI target, int level) {
        switch (level) {
            case 1:
                target.getTags().remove(getTag(2));
                target.addTag(getTag(1));
                break;
            case 2:
                target.getTags().remove(getTag(3));
                target.addTag(getTag(2));
                break;
            case 3:
                target.addTag(getTag(3));
                break;
            default:
                target.getTags().remove(getTag(1));
                target.getTags().remove(getTag(2));
                target.getTags().remove(getTag(3));
                break;
        }
    }

    int levelBeamHitDuration(float beamHitDuration) {
        if(beamHitDuration < 3000f) {
            return 1;
        } else if (beamHitDuration < 6000f) {
            return 2;
        } else {
            return 3;
        }
    }
}
