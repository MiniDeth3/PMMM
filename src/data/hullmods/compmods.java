package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import scripts.PMMLunaSettings;

import java.awt.*;
import java.util.Iterator;

public class compmods extends BaseHullMod {
	public static final float PENALTY_MULT = 0.9f;
	public static final float BONUS_MULT = 1.1f;
	private static final float MALFUNCTION_PROB = 0.006f;
	private static final float ENGINE_MALFUNCTION_PROB = 0.001f;
	public static final float SMOD_MULT_BONUS = 0.05f;
	public static final float SMOD_SPEED_BONUS = 20f;
	public static final float SMOD_ENG_BONUS = 1.1f;
	public static final float SMOD_BAL_BONUS = 1.2f;
	public static final float SMOD_MIS_HEALTH_BONUS = 1.1f;
	public static Color BALLISTIC_GLOW = PMMLunaSettings.PirateGlowColorBallistic();
	public static Color ENERGY_GLOW = PMMLunaSettings.PirateGlowColorEnergy();


	public void advanceInCombat(ShipAPI ship, float amount) {
		CombatEngineAPI engine = Global.getCombatEngine();
		boolean sMod = isSMod(ship);
		if (sMod) {
			if (engine.isEntityInPlay(ship)) {
				Iterator weaponiter = ship.getAllWeapons().iterator();
				while (weaponiter.hasNext()) {
					WeaponAPI weapon = (WeaponAPI) weaponiter.next();
					if (weapon.getType().equals(WeaponAPI.WeaponType.BALLISTIC)) {
						weapon.setGlowAmount(0.8f, BALLISTIC_GLOW);
					}
					if (weapon.getType().equals(WeaponAPI.WeaponType.ENERGY)) {
						weapon.setGlowAmount(0.8f, ENERGY_GLOW);
					}
				}
			} else {
				Iterator weaponiter = ship.getAllWeapons().iterator();
				while (weaponiter.hasNext()) {
					WeaponAPI weapon = (WeaponAPI) weaponiter.next();
					if (weapon.getType().equals(WeaponAPI.WeaponType.BALLISTIC)) {
						weapon.setGlowAmount(0.4f, BALLISTIC_GLOW);
					}
					if (weapon.getType().equals(WeaponAPI.WeaponType.ENERGY)) {
						weapon.setGlowAmount(0.4f, ENERGY_GLOW);
					}
				}
			}
		}
	}


	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		boolean sMod = isSMod(stats);

		stats.getCriticalMalfunctionChance().modifyFlat(id, (MALFUNCTION_PROB * 0.5f));
		stats.getWeaponMalfunctionChance().modifyFlat(id, MALFUNCTION_PROB);
		stats.getEngineMalfunctionChance().modifyFlat(id, ENGINE_MALFUNCTION_PROB);

		stats.getHullBonus().modifyMult(id, (PENALTY_MULT - (sMod ? SMOD_MULT_BONUS : 0)));
		stats.getArmorBonus().modifyMult(id, (PENALTY_MULT - (sMod ? SMOD_MULT_BONUS : 0)));
		stats.getFluxDissipation().modifyMult(id, ((BONUS_MULT - 0.05f) + (sMod ? SMOD_MULT_BONUS : 0)));
		stats.getFluxCapacity().modifyMult(id, ((PENALTY_MULT + 0.05f) - (sMod ? SMOD_MULT_BONUS : 0)));

		if (sMod) {

			stats.getEnergyWeaponDamageMult().modifyMult(id, SMOD_ENG_BONUS);
			stats.getBallisticRoFMult().modifyMult(id, SMOD_BAL_BONUS);
			stats.getBallisticWeaponFluxCostMod().modifyMult(id, PENALTY_MULT);
			stats.getMaxSpeed().modifyFlat(id, SMOD_SPEED_BONUS);

		}
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) Math.round((1f - PENALTY_MULT) * 100f) + "%";
		if (index == 1) return "" + (int) Math.round((1f - (PENALTY_MULT + 0.05f)) * 100f) + "%";
		if (index == 2) return "" + (int) Math.round(((BONUS_MULT - 0.05f) - 1f) * 100f) + "%";
		return null;
	}
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) Math.round((1f - (PENALTY_MULT - SMOD_MULT_BONUS)) * 100f) + "%";
		if (index == 1) return "" + (int) Math.round((1f - ((PENALTY_MULT + 0.05f) - SMOD_MULT_BONUS)) * 100f) + "%";
		if (index == 2) return "" + (int) Math.round((((BONUS_MULT - 0.05f) + SMOD_MULT_BONUS) - 1f) * 100f) + "%";
		if (index == 3) return "" + 20;
		if (index == 4) return "Energy";
		if (index == 5) return "" + (int) Math.round((SMOD_ENG_BONUS - 1f) * 100f) + "%";
		if (index == 6) return "Ballistic";
		if (index == 7) return "" + (int) Math.round((1f - PENALTY_MULT) * 100f) + "%";
		if (index == 8) return "" + (int) Math.round((SMOD_BAL_BONUS - 1f) * 100f) + "%";

		return null;
	}
}
