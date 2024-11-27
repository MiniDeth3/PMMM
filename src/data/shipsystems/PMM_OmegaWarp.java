package data.shipsystems;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class PMM_OmegaWarp extends BaseShipSystemScript {
	public static final float MAX_TIME_MULT = 55f;

	public static float SPEED_BONUS = 100f;
	public static float TURN_BONUS = 30f;
	
	public static final Color JITTER_COLOR = new Color(100,100,255,150);
	public static final Color JITTER_UNDER_COLOR = new Color(100,100,255,150);

	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
			id = id + "_" + ship.getId();
		} else {
			return;
		}

		float jitterLevel = effectLevel;
		float jitterRangeBonus = 0;
		float maxRangeBonus = 10f;
		if (state == State.IN) {
			jitterLevel = effectLevel / (1f / ship.getSystem().getChargeUpDur());
			if (jitterLevel > 1) {
				jitterLevel = 10f;
			}
			jitterRangeBonus = jitterLevel * maxRangeBonus;
		} else if (state == State.ACTIVE) {
			stats.getMaxSpeed().modifyFlat(id, SPEED_BONUS);
			stats.getAcceleration().modifyPercent(id, SPEED_BONUS * 3f * effectLevel);
			stats.getDeceleration().modifyPercent(id, SPEED_BONUS * 3f * effectLevel);
			stats.getTurnAcceleration().modifyFlat(id, TURN_BONUS * effectLevel);
			stats.getTurnAcceleration().modifyPercent(id, TURN_BONUS * 5f * effectLevel);
			stats.getMaxTurnRate().modifyFlat(id, 15f);
			stats.getMaxTurnRate().modifyPercent(id, 100f);

			if (player) {
				Global.getSoundPlayer().playSound("mote_attractor_targeted_empty_space", 1f, 0.15f, ship.getLocation(), ship.getVelocity());
				Global.getSoundPlayer().playSound("mote_attractor_system_activated", 2f, 0.3f, ship.getLocation(), ship.getVelocity());
			} else {
				Global.getSoundPlayer().playSound("mote_attractor_targeted_empty_space", 5f, 0.8f, ship.getLocation(), ship.getVelocity());
				Global.getSoundPlayer().playSound("mote_attractor_system_activated", 6f, 1f, ship.getLocation(), ship.getVelocity());
			}

			jitterLevel = 5f;
			jitterRangeBonus = maxRangeBonus;
		} else if (state == State.OUT) {
			stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
			stats.getMaxTurnRate().unmodify(id);
			stats.getDeceleration().unmodify(id);
			stats.getAcceleration().unmodify(id);
			stats.getTurnAcceleration().unmodify(id);

			jitterRangeBonus = jitterLevel * maxRangeBonus;
		}
		jitterLevel = (float) Math.sqrt(jitterLevel);
		effectLevel *= effectLevel;
		
		ship.setJitter(this, JITTER_UNDER_COLOR, jitterLevel, 16, 50f, 110f + jitterRangeBonus);
		ship.setJitterUnder(this, JITTER_COLOR, jitterLevel, 16, 50f, 110f + jitterRangeBonus);

		float shipTimeMult = 1f + (MAX_TIME_MULT - 1f) * effectLevel;
		stats.getTimeMult().modifyMult(id, shipTimeMult);
		if (player) {
			Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / shipTimeMult);
//			if (ship.areAnyEnemiesInRange()) {
//				Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / shipTimeMult);
//			} else {
//				Global.getCombatEngine().getTimeMult().modifyMult(id, 2f / shipTimeMult);
//			}
		} else {
			Global.getCombatEngine().getTimeMult().unmodify(id);
		}
		
		ship.getEngineController().fadeToOtherColor(this, JITTER_COLOR, new Color(0,0,0,0), effectLevel, 0.5f);
		ship.getEngineController().extendFlame(this, 3f, 1f, 3f);
	}
	
	
	public void unapply(MutableShipStatsAPI stats, String id) {
		ShipAPI ship = null;
		boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
			id = id + "_" + ship.getId();
		} else {
			return;
		}

		Global.getCombatEngine().getTimeMult().unmodify(id);
		stats.getTimeMult().unmodify(id);

		stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
		stats.getMaxTurnRate().unmodify(id);
		stats.getDeceleration().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
		
//		stats.getHullDamageTakenMult().unmodify(id);
//		stats.getArmorDamageTakenMult().unmodify(id);
//		stats.getEmpDamageTakenMult().unmodify(id);
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		float shipTimeMult = 1f + (MAX_TIME_MULT - 1f) * effectLevel;
		if (index == 0) {
			return new StatusData("time flow halted", false);
		}
//		if (index == ) {
//			return new StatusData("increased speed", false);
//		}
//		if (index == 1) {
//			return new StatusData("increased acceleration", false);
//		}
		return null;
	}
}








