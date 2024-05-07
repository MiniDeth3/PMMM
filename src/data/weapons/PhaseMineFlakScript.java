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
        Global.getCombatEngine().addPlugin(this.createMissileJitterPlugin((MissileAPI) projectile, fadeInTime));

        projectile.getLocation().set(loc);
        Global.getSoundPlayer().playSound("mine_teleport", 1.0F, 1.0F, projectile.getLocation(), projectile.getVelocity());

    }

    protected EveryFrameCombatPlugin createMissileJitterPlugin(final MissileAPI mine, final float fadeInTime) {
        return new BaseEveryFrameCombatPlugin() {
            float elapsed = 0.0F;

            public void advance(float amount, List<InputEventAPI> events) {
                if (!Global.getCombatEngine().isPaused()) {
                    this.elapsed += amount;
                    float jitterLevel = mine.getCurrentBaseAlpha();
                    if (jitterLevel < 0.5F) {
                        jitterLevel *= 2.0F;
                    } else {
                        jitterLevel = (1.0F - jitterLevel) * 2.0F;
                    }

                    float jitterRange = 1.0F - mine.getCurrentBaseAlpha();
                    float maxRangeBonus = 50.0F;
                    float jitterRangeBonus = jitterRange * maxRangeBonus;
                    Color c = MineStrikeStats.JITTER_UNDER_COLOR;
                    c = Misc.setAlpha(c, 70);
                    mine.setJitter(this, c, jitterLevel, 15, jitterRangeBonus * 0.0F, jitterRangeBonus);
                    if (jitterLevel >= 1.0F || this.elapsed > fadeInTime) {
                        Global.getCombatEngine().removePlugin(this);
                    }

                }
            }
        };
    }
}
