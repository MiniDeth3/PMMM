package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import scripts.PMMLunaSettings;

import java.awt.*;
import java.util.Iterator;

public class c2flux extends BaseHullMod {

	public void advanceInCombat(ShipAPI ship, float amount) {
		CombatEngineAPI engine = Global.getCombatEngine();
		if (ship.getFluxTracker().getFluxLevel() < 0.7f) {
			ship.getFluxTracker().setCurrFlux(9100f);
		}
	}
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getVentRateMult().modifyMult(id, 0f);
	}
}
