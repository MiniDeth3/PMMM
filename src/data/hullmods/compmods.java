package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import scripts.PMMLunaSettings;

import java.awt.*;

public class compmods extends BaseHullMod {

	public static final float PENALTY_MULT = 0.9f;
	public static final float BONUS_MULT = 1.1f;
	private static final float MALFUNCTION_PROB = 0.007f;
	private static final float ENGINE_MALFUNCTION_PROB = 0.0012f;
	public static final float SMOD_MULT_BONUS = 0.05f;
	public static final float SMOD_SPEED_BONUS = 20f;
	public static final float SMOD_ENG_BONUS = 1.1f;
	public static final float SMOD_BAL_BONUS = 1.2f;

	// Glow Colors for Ballistic and Energy Weapons
	public static Color BALLISTIC_GLOW = PMMLunaSettings.PirateGlowColorBallistic();
	public static Color ENERGY_GLOW = PMMLunaSettings.PirateGlowColorEnergy();

	// Glow toggle flag
	public static boolean GLOW;

	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		CombatEngineAPI engine = Global.getCombatEngine();
		boolean sMod = isSMod(ship);

		// Apply glow effect to weapons if conditions are met
		if (sMod && GLOW) {
			if (engine.isEntityInPlay(ship)) {
				setWeaponGlow(ship, 0.5f); // Bright glow when in play
			} else {
				setWeaponGlow(ship, 0.3f); // Dim glow when not in play
			}
		}
	}

	private void setWeaponGlow(ShipAPI ship, float glowAmount) {
		for (WeaponAPI weapon : ship.getAllWeapons()) {
			if (weapon.getType().equals(WeaponAPI.WeaponType.BALLISTIC)) {
				weapon.setGlowAmount(glowAmount, BALLISTIC_GLOW);
			} else if (weapon.getType().equals(WeaponAPI.WeaponType.ENERGY)) {
				weapon.setGlowAmount(glowAmount, ENERGY_GLOW);
			}
		}
	}

	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		boolean sMod = isSMod(stats);

		// Apply malfunction probabilities
		stats.getCriticalMalfunctionChance().modifyFlat(id, MALFUNCTION_PROB * 0.3f);
		stats.getWeaponMalfunctionChance().modifyFlat(id, MALFUNCTION_PROB);
		stats.getEngineMalfunctionChance().modifyFlat(id, ENGINE_MALFUNCTION_PROB);

		// Apply stat modifications based on whether it's an S-mod
		stats.getHullBonus().modifyMult(id, PENALTY_MULT - (sMod ? SMOD_MULT_BONUS : 0));
		stats.getArmorBonus().modifyMult(id, PENALTY_MULT - (sMod ? SMOD_MULT_BONUS : 0));
		stats.getFluxDissipation().modifyMult(id, (BONUS_MULT - 0.05f) + (sMod ? SMOD_MULT_BONUS : 0));
		stats.getFluxCapacity().modifyMult(id, (PENALTY_MULT + 0.05f) - (sMod ? SMOD_MULT_BONUS : 0));

		if (sMod) {
			stats.getEnergyWeaponDamageMult().modifyMult(id, SMOD_ENG_BONUS);
			stats.getBallisticRoFMult().modifyMult(id, SMOD_BAL_BONUS);
			stats.getBallisticWeaponFluxCostMod().modifyMult(id, PENALTY_MULT);
			stats.getMaxSpeed().modifyFlat(id, SMOD_SPEED_BONUS);
		}
	}

	@Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		switch (index) {
			case 0: return Math.round((1f - PENALTY_MULT) * 100f) + "%";
			case 1: return Math.round((1f - (PENALTY_MULT + 0.05f)) * 100f) + "%";
			case 2: return Math.round(((BONUS_MULT - 0.05f) - 1f) * 100f) + "%";
			default: return null;
		}
	}

	@Override
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		switch (index) {
			case 0: return Math.round((1f - (PENALTY_MULT - SMOD_MULT_BONUS)) * 100f) + "%";
			case 1: return Math.round((1f - ((PENALTY_MULT + 0.05f) - SMOD_MULT_BONUS)) * 100f) + "%";
			case 2: return Math.round((((BONUS_MULT - 0.05f) + SMOD_MULT_BONUS) - 1f) * 100f) + "%";
			case 3: return String.valueOf(SMOD_SPEED_BONUS);
			case 4: return "Energy";
			case 5: return Math.round((SMOD_ENG_BONUS - 1f) * 100f) + "%";
			case 6: return "Ballistic";
			case 7: return Math.round((1f - PENALTY_MULT) * 100f) + "%";
			case 8: return Math.round((SMOD_BAL_BONUS - 1f) * 100f) + "%";
			default: return null;
		}
	}
}
