package data.weapons;

import com.fs.starfarer.api.combat.*;

import java.awt.*;

public class FonsiBeam implements BeamEffectPlugin { // Let's assume it works
    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        if (beam.getDamageTarget() != null
                && beam.getDamageTarget() instanceof ShipAPI
                && ((ShipAPI) beam.getDamageTarget()).hasTag("TargetAssistBeam")) {

            beam.getWeapon().setWeaponGlowWidthMult(2.0f);
            beam.getWeapon().setScaleBeamGlowBasedOnDamageEffectiveness(true);
            beam.setCoreColor(Color.CYAN);
            beam.setFringeColor(Color.MAGENTA);
            engine.applyDamage(beam.getDamageTarget(), beam.getTo(), 100 * amount, DamageType.ENERGY, 100 * amount, true, true, beam.getWeapon().getShip());
        } else {
            beam.getWeapon().setWeaponGlowWidthMult(1.0f);
            beam.getWeapon().setScaleBeamGlowBasedOnDamageEffectiveness(false);
        }
    }
}
