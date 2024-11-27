package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SoundAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import org.lwjgl.util.vector.Vector2f;

public class PMM_PunisherJetsStats_Crossmod extends BaseShipSystemScript {

    // Original script from SWP (authored by DR) for crossmod use.
    private static final Vector2f ZERO = new Vector2f();

    private SoundAPI sound = null;
    private boolean started = false;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        float flatscale = 1.0f;
        ShipAPI ship = (ShipAPI) stats.getEntity();
        if (ship != null) {
            if (!started) {
                started = true;
                float pitch;
                switch (ship.getHullSize()) {
                    case FIGHTER:
                        pitch = 1.3f;
                        break;
                    case FRIGATE:
                        pitch = 1.25f;
                        break;
                    case DEFAULT:
                    case DESTROYER:
                    default:
                        pitch = 1.15f;
                        break;
                    case CRUISER:
                        pitch = 1f;
                        break;
                    case CAPITAL_SHIP:
                        pitch = 0.9f;
                        break;
                }
                sound = Global.getSoundPlayer().playSound("swp_punisherjets_activate", pitch, 1f, ship.getLocation(), ZERO);
            }
            if (sound != null) {
                sound.setLocation(ship.getLocation().x, ship.getLocation().y);
            }
        }
        if (state == ShipSystemStatsScript.State.OUT) {
            stats.getMaxSpeed().modifyFlat(id, 0f);
            stats.getMaxSpeed().modifyPercent(id, 100f * effectLevel); // to slow down ship to its regular top speed while powering drive down
            stats.getMaxTurnRate().modifyPercent(id, 100f * effectLevel);
            stats.getAcceleration().modifyPercent(id, 150f * effectLevel);
            stats.getDeceleration().modifyPercent(id, 200f);
        } else {
            stats.getMaxSpeed().modifyFlat(id, 100f * flatscale * effectLevel);
            stats.getMaxSpeed().modifyPercent(id, 100f * effectLevel);
            stats.getAcceleration().modifyFlat(id, 150f * flatscale * effectLevel);
            stats.getAcceleration().modifyPercent(id, 150f * effectLevel);
            stats.getDeceleration().modifyPercent(id, 100f * effectLevel);
            stats.getTurnAcceleration().modifyFlat(id, 50f * flatscale * effectLevel);
            stats.getTurnAcceleration().modifyPercent(id, 300f * effectLevel);
            stats.getMaxTurnRate().modifyFlat(id, 25f * flatscale * effectLevel);
            stats.getMaxTurnRate().modifyPercent(id, 100f * effectLevel);
        }

//        if (ship != null) {
//            String key = ship.getId() + "_" + id;
//            Object test = Global.getCombatEngine().getCustomData().get(key);
//            if (state == State.IN) {
//                if (test == null && effectLevel > 0.2f) {
//                    Global.getCombatEngine().getCustomData().put(key, new Object());
//                    ship.getEngineController().getExtendLengthFraction().advance(1f);
//                    for (ShipEngineAPI engine : ship.getEngineController().getShipEngines()) {
//                        if (engine.isSystemActivated()) {
//                            ship.getEngineController().setFlameLevel(engine.getEngineSlot(), 1f);
//                        }
//                    }
//                }
//            } else {
//                Global.getCombatEngine().getCustomData().remove(key);
//            }
//        }
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData("improved maneuverability", false);
        } else if (index == 1) {
            return new StatusData("increased top speed", false);
        }
        return null;
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        started = false;
        stats.getMaxSpeed().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
        sound = null;
    }

    @Override
    public float getRegenOverride(ShipAPI ship) {
        if (ship.getHullSize() == HullSize.CAPITAL_SHIP) {
            return 0.075f;
        }
        return -1;
    }
}
