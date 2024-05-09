package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.listeners.DamageListener;

public class ReducedSelfDmg extends BaseHullMod implements DamageListener {
    @Override
    public void reportDamageApplied(Object source, CombatEntityAPI target, ApplyDamageResultAPI result) {
        if (source == target){
            result.setDamageToHull(result.getDamageToHull()/10);
            result.setDamageToShields(result.getDamageToShields()/10);
            result.setTotalDamageToArmor(result.getTotalDamageToArmor()/10);
        }
    }
}
