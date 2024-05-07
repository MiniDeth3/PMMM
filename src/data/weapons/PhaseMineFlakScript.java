package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class PhaseMineFlakScript implements OnFireEffectPlugin {

    public static float RANGE = 600;
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {

        float minangle = weapon.getArcFacing() - 20;
        float maxangle = weapon.getArcFacing() + 20;

        Vector2f loc = MathUtils.getRandomPointInCone(weapon.getLocation(), RANGE, minangle, maxangle);

        projectile.getLocation().set(loc);
        Global.getSoundPlayer().playSound("mine_teleport", 1.0F, 1.0F, projectile.getLocation(), projectile.getVelocity());

    }
}
