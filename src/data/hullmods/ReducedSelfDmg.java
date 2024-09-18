package data.hullmods;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.listeners.DamageListener;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class ReducedSelfDmg implements OnHitEffectPlugin {
    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if (projectile.getSource() == target){
        target.getShield().setInnerColor(Color.GREEN);


        if (target.getOwner() == 0){
            damageResult.setDamageToShields(damageResult.getDamageToShields()/10);
            damageResult.setDamageToHull(damageResult.getDamageToHull()/10);
            damageResult.setTotalDamageToArmor(damageResult.getTotalDamageToArmor()/10);
            target.getShield().setInnerColor(Color.PINK);
            }
        }
    }
}
