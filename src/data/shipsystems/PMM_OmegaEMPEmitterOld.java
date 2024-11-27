package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.EnumSet;

import static com.fs.starfarer.api.impl.combat.DamperFieldOmegaStats.KEY_SHIP;

public class PMM_OmegaEMPEmitterOld extends BaseShipSystemScript {
    Color UNDERCOLOR = RiftCascadeEffect.EXPLOSION_UNDERCOLOR;
    IntervalUtil projtimer = new IntervalUtil(0,2);
    Vector2f shipvel = null;




    public void advance(float amount, CombatEngineAPI engine, ShipAPI ship) {

    }


    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = (ShipAPI) stats.getEntity();
        CombatEngineAPI engine = Global.getCombatEngine();
        Vector2f pt = ship.getLocation();
        float shipsize = ship.getShieldRadiusEvenIfNoShield();
        pt = Misc.getPointWithinRadius(pt, 90 * 1.0F);
        ship.fadeToColor(KEY_SHIP, new Color(75, 75, 75, 255), 0.1F, 0.1F, effectLevel);
        ship.setWeaponGlow(effectLevel, new Color(125, 25, 50, 155), EnumSet.of(WeaponAPI.WeaponType.BALLISTIC, WeaponAPI.WeaponType.ENERGY, WeaponAPI.WeaponType.MISSILE));
        ship.getEngineController().fadeToOtherColor(KEY_SHIP, new Color(0, 0, 0, 0), new Color(0, 0, 0, 0), effectLevel, 0.75F * effectLevel);
        ship.setJitterUnder(KEY_SHIP, new Color(125, 25, 50, 155), effectLevel, 15, 0.0F, 15.0F);
        effectLevel = 1.0F;
        shipvel = ship.getVelocity();
        engine.addSwirlyNebulaParticle(ship.getLocation(), shipvel, shipsize + 60f, 1.5f, 0.25F /1.5f + 1.5f * (float)Math.random(), 0.0F, 1f, new Color(100, 0, 25, 100), true);
        engine.addNegativeNebulaParticle(ship.getLocation(), shipvel, shipsize + 20f, 1.5f, 0.25F /1.5f + 1.5f * (float)Math.random(), 0.0F, 1f, new Color(100, 0, 25, 100));
        engine.addNegativeNebulaParticle(ship.getLocation(), shipvel, shipsize, 1.5F, 0.25F /1.5f + 1.5f * (float)Math.random(), 0.0F, 1f, RiftLanceEffect.getColorForDarkening(UNDERCOLOR));



    }

    public void unapply(MutableShipStatsAPI stats, String id) {

    }
}
