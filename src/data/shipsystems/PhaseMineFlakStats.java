package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;

import java.util.Iterator;
import java.util.Vector;

import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;


public class PhaseMineFlakStats extends BaseShipSystemScript {

    static float nummines = 0f;
    private IntervalUtil minespawninterval = new IntervalUtil(0.5f, 0.5f);

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        nummines = 16f;
    }

    public void advance(ShipAPI ship, float amount) {
        if (ship.getSystem().getState() == ShipSystemAPI.SystemState.ACTIVE){
            if ((nummines > 0) && (minespawninterval.intervalElapsed())){
                spawnMine(ship, MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius()));
            }
        }
        minespawninterval.advance(amount);
    }

    public void spawnMine(ShipAPI source, Vector2f mineLoc) {
        CombatEngineAPI engine = Global.getCombatEngine();
        Vector2f currLoc = Misc.getPointAtRadius(mineLoc, 30.0F + (float)Math.random() * 30.0F);
        float start = (float)Math.random() * 360.0F;

        for(float angle = start; angle < start + 390.0F; angle += 30.0F) {
            if (angle != start) {
                Vector2f loc = Misc.getUnitVectorAtDegreeAngle(angle);
                loc.scale(50.0F + (float)Math.random() * 30.0F);
                currLoc = Vector2f.add(mineLoc, loc, new Vector2f());
            }

            Iterator var8 = Global.getCombatEngine().getMissiles().iterator();

            while(var8.hasNext()) {
                MissileAPI other = (MissileAPI)var8.next();
                if (other.isMine()) {
                    float dist = Misc.getDistance(currLoc, other.getLocation());
                    if (dist < other.getCollisionRadius() + 40.0F) {
                        currLoc = null;
                        break;
                    }
                }
            }

            if (currLoc != null) {
                break;
            }
        }

        if (currLoc == null) {
            currLoc = Misc.getPointAtRadius(mineLoc, 30.0F + (float)Math.random() * 30.0F);
        }

        MissileAPI mine = (MissileAPI)engine.spawnProjectile(source, (WeaponAPI)null, "minelayer2", currLoc, (float)Math.random() * 360.0F, (Vector2f)null);
        if (source != null) {
            Global.getCombatEngine().applyDamageModifiersToSpawnedProjectileWithNullWeapon(source, WeaponAPI.WeaponType.MISSILE, false, mine.getDamage());
        }

        float fadeInTime = 0.5F;
        mine.getVelocity().scale(0.0F);
        mine.fadeOutThenIn(fadeInTime);
        float liveTime = 5.0F;
        mine.setFlightTime(mine.getMaxFlightTime() - liveTime);
        Global.getSoundPlayer().playSound("mine_teleport", 1.0F, 1.0F, mine.getLocation(), mine.getVelocity());
    }
}
