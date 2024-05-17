package data.weapons;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.listeners.DamageListener;
import org.lwjgl.util.vector.Vector2f;

public class ReducedSelfDmg implements OnHitEffectPlugin {
    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if (target.getOwner() == 0){
            damageResult.setDamageToShields(damageResult.getDamageToShields()/10);
            damageResult.setDamageToHull(damageResult.getDamageToHull()/10);
            damageResult.setTotalDamageToArmor(damageResult.getTotalDamageToArmor()/10);
        }
    }
}
