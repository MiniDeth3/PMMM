package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;

public class SniperMortar implements EveryFrameWeaponEffectPlugin {
    float currentChargePercent;
    boolean chargingPewPew = false;
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if(weapon.isFiring()) {
            chargingPewPew = true;
        } else {

            if(chargingPewPew) {
                currentChargePercent = 0.0f;
                chargingPewPew = false;
            } else {
                currentChargePercent = Math.min(currentChargePercent + amount * 25f, 100f);
            }
        }

        applyCharge(weapon, currentChargePercent);

        Global.getLogger(SniperMortar.class).warn("currentChargePercent: " + currentChargePercent);
    }

    void applyCharge(WeaponAPI weapon, float chargePercent) {
        weapon.getSpec().setMaxRange(420f + (1_000f * chargePercent) / 100f);
        weapon.getDamage().getModifier().modifyFlat("SniperMortar", 10_000f * chargePercent);
        weapon.getSpec().setMaxSpread(50f - (48f * chargePercent)/100f);
    }
}
