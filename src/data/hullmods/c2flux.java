package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class c2flux extends BaseHullMod {

	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		CombatEngineAPI engine = Global.getCombatEngine();
		// Ensure flux level is below 70% before applying the flux set
		if (ship.getFluxTracker().getFluxLevel() < 0.7f) {
			ship.getFluxTracker().setCurrFlux(9100f); // Set the flux to a specific value (adjust as needed)
		}
	}

	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		// Modify the vent rate multiplier to 0, effectively disabling venting
		stats.getVentRateMult().modifyMult(id, 0f);
	}
}
