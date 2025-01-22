package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class PMM_AblativeShield extends BaseHullMod {
    // Constants
    private static final float FLUX_THRESHOLD = 0.8f; // Flux level (80%) at which the effects trigger
    private static final float MAX_EFFICIENCY_DECREASE = 0.5f; // Shield efficiency decreases to 50% at max flux
    private static final float ARC_DAMAGE = 100f; // Damage dealt by EMP arcs
    private static final float ARC_EMP = 0f; // EMP damage dealt by arcs
    private static final float ARC_RANGE = 1000f; // Range of EMP arcs
    private static final float MIN_INTERVAL = 0.3f; // Minimum interval between EMP arcs
    private static final float MAX_INTERVAL = 0.6f; // Maximum interval between EMP arcs
    private static final Color ARC_CORE_COLOR = new Color(255, 150, 255, 255); // Core color of the EMP arc
    private static final Color ARC_FRINGE_COLOR = new Color(255, 50, 255, 255); // Fringe color of the EMP arc
    private static final Color SHIELD_BASE_COLOR = new Color(125,125,255,75); // Base shield color (cyan)
    private static final Color SHIELD_MAX_FLUX_COLOR = new Color(255,100,100,40); // Shield color at max flux (red)
    private static final Color SHIELD_WHITE = new Color(255, 255, 255, 200); // Shield color at max flux (red)
    private static final float JITTER_MAX = 30f; // Maximum jitter magnitude at full flux

    // Timer to control EMP arc spawning
    private float empArcTimer = MathUtils.getRandomNumberInRange(MIN_INTERVAL, MAX_INTERVAL);

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        // Exit early if the ship is null, destroyed, or not in combat
        if (ship == null || ship.isHulk() || !ship.isAlive()) return;

        // Get the current flux level as a percentage of the maximum
        float fluxLevel = ship.getFluxTracker().getFluxLevel();
        CombatEngineAPI engine = Global.getCombatEngine();

        // Adjust shield efficiency based on the current flux level
        float efficiencyMult = Math.min(1 + fluxLevel * (1 - MAX_EFFICIENCY_DECREASE), 1 / MAX_EFFICIENCY_DECREASE);
        ship.getMutableStats().getShieldDamageTakenMult().modifyMult("FluxOverdriveHullmod", efficiencyMult);

        // Gradually change the shield color based on the flux level
        if (ship.getShield() != null) {
            Color shieldColor = interpolateColor(SHIELD_BASE_COLOR, SHIELD_MAX_FLUX_COLOR, fluxLevel);
            ship.getShield().setRingColor(SHIELD_WHITE);
            ship.getShield().setInnerColor(new Color(shieldColor.getRed(), shieldColor.getGreen(), shieldColor.getBlue(), 50)); // Subdued inner color

            // Apply shield jitter based on flux level
            applyShieldJitter(ship, fluxLevel);
        }

        // If the flux level is 80% or higher, handle EMP arc spawning
        if (ship.getFluxTracker().isOverloaded() || fluxLevel >= FLUX_THRESHOLD) {
            // Decrease the EMP arc timer
            empArcTimer -= amount;

            // If the timer reaches 0, spawn an EMP arc and reset the timer
            if (empArcTimer <= 0f) {
                empArcTimer = MathUtils.getRandomNumberInRange(MIN_INTERVAL, MAX_INTERVAL);
                spawnEmpArc(ship, engine);
            }
        }
    }

    private void applyShieldJitter(ShipAPI ship, float fluxLevel) {
        float jitterMagnitude = JITTER_MAX * fluxLevel; // Jitter scales with flux level
        Color shieldColor = interpolateColor(SHIELD_BASE_COLOR, SHIELD_MAX_FLUX_COLOR, fluxLevel);
        // Apply jitter to the entire ship
        ship.setJitterUnder(
                this, // Source of the jitter
                (new Color(shieldColor.getRed(), shieldColor.getGreen(), shieldColor.getBlue(), 75)), // Jitter color
                (fluxLevel * 1.5f), // Intensity of the jitter (based on flux level)
                5, // Number of jitter segments
                jitterMagnitude // Maximum distance for jitter
        );
    }

    private void spawnEmpArc(ShipAPI ship, CombatEngineAPI engine) {
        // Randomly select a point on the ship's hull for the EMP arc
        Vector2f point = MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius());

        // Spawn an EMP arc at the chosen point, damaging the ship itself
        engine.spawnEmpArcPierceShields(
                ship,               // Source of the arc (this ship)
                point,              // Origin point of the arc
                ship,               // Arc's target (also this ship)
                ship,               // Damage dealer (this ship)
                DamageType.ENERGY,  // Damage type (ENERGY)
                ARC_DAMAGE,         // Regular damage
                ARC_EMP,            // EMP damage
                ARC_RANGE,          // Range of the arc
                "system_emp_emitter_impact", // Visual/sound effect ID
                20f,                // Thickness of the arc
                ARC_CORE_COLOR,     // Core color of the arc
                ARC_FRINGE_COLOR    // Fringe color of the arc
        );
    }

    private Color interpolateColor(Color start, Color end, float factor) {
        // Ensure factor is clamped between 0 and 1
        factor = MathUtils.clamp(factor, 0f, 1f);

        // Interpolate between the start and end colors
        int r = (int) (start.getRed() + factor * (end.getRed() - start.getRed()));
        int g = (int) (start.getGreen() + factor * (end.getGreen() - start.getGreen()));
        int b = (int) (start.getBlue() + factor * (end.getBlue() - start.getBlue()));
        int a = (int) (start.getAlpha() + factor * (end.getAlpha() - start.getAlpha()));

        return new Color(r, g, b, a);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        // Provide descriptions for tooltips in the hullmod interface
        if (index == 0) return "80%"; // Flux threshold
        if (index == 1) return "50%"; // Max shield efficiency reduction
        if (index == 2) return "200 Energy"; // Arc damage
        return null; // Fallback for undefined indices
    }
}
