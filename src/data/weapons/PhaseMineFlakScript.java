package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import org.lazywizard.lazylib.MathUtils;

public class PhaseMineFlakScript implements OnFireEffectPlugin {
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        projectile.getLocation().set(MathUtils.getRandomPointInCircle(projectile.getSource().getLocation(), projectile.getSource().getCollisionRadius()));
        Global.getSoundPlayer().playSound("mine_teleport", 1.0F, 1.0F, projectile.getLocation(), projectile.getVelocity());

    }
}
