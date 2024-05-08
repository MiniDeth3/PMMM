package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.MineStrikeStats;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;

public class PhaseMineFlakScript implements OnFireEffectPlugin {

    public static float RANGE = 600;
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {

        float minangle = weapon.getArcFacing() - 20;
        float maxangle = weapon.getArcFacing() + 20;
        float fadeInTime = 0.5F;

        Vector2f loc = MathUtils.getRandomPointInCone(weapon.getLocation(), RANGE, minangle, maxangle);
        projectile.getLocation().set(loc);
        engine.addNebulaSmokeParticle(projectile.getLocation(), projectile.getVelocity(), projectile.getCollisionRadius(), 1.5f, 0.7f, 1,1, Color.MAGENTA);

        Global.getSoundPlayer().playSound("mine_teleport", 1.0F, 1.0F, projectile.getLocation(), projectile.getVelocity());

    }
}
