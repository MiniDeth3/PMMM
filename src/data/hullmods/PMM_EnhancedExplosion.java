package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;
import java.awt.*;

public class PMM_EnhancedExplosion extends BaseHullMod {
	public static final float ARC_DAM = 200f;
	public static final float ARC_EMP = 50f;

	public static final Color LIGHTNING_CORE_COLOR = new Color(255, 150, 255, 255);
	public static final Color LIGHTNING_FRINGE_COLOR = new Color(255, 50, 255, 255);

	private boolean hasZapped = false; // Flag to ensure the zap only happens once after death

	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		CombatEngineAPI engine = Global.getCombatEngine();

		// Trigger the EMP arc only once when the ship dies
		if (ship.getHitpoints() <= 0f && !hasZapped) {
			// Randomize the EMP arc range between 1000 and 2000 units
			float arcRange = MathUtils.getRandomNumberInRange(1000f, 1500f);

			// Spawn the EMP arcs from the ship's location
			Vector2f sourcePoint = ship.getLocation();

			// Fire 15 arcs at the same time in a random direction within the range
			for (int i = 0; i < 50; i++) {
				// Get a random point within the arc range to simulate multiple directions
				Vector2f randomPoint = MathUtils.getRandomPointInCircle(sourcePoint, arcRange);

				// Spawn the EMP arc
				engine.spawnEmpArc(ship, sourcePoint, ship, new SimpleEntity(randomPoint),
						DamageType.ENERGY, ARC_DAM, ARC_EMP, arcRange, "", 20f,
						LIGHTNING_CORE_COLOR, LIGHTNING_FRINGE_COLOR);
			}

			// Set the flag so the arc only spawns once after the ship's destruction
			hasZapped = true;
		}
	}

	@Override
	public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
		return null; // Empty description as this is a hidden hullmod
	}
}
