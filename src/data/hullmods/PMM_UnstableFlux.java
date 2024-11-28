package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PMM_UnstableFlux extends BaseHullMod {
    public static float ARC_DAM_SELF = 200f;
    public static float ARC_EMP_SELF = 0f;
    public static float HARD_DISS = 100f;
    public static Color LIGHTNING_CORE_COLOR = new Color(255, 150, 255, 255);
    public static Color LIGHTNING_FRINGE_COLOR = new Color(255, 50, 255, 255);
    private IntervalUtil selfzapInterval = new IntervalUtil (0.2f,0.4f);



    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        float fluxpercent = ship.getFluxTracker().getCurrFlux()/ship.getFluxTracker().getMaxFlux();
        Color targetcolor = new Color(255,125,125,75);
        Color startcolor = new Color(125,125,255,75);
        Color gradientcolor = Misc.interpolateColor(startcolor, targetcolor ,fluxpercent);
        ship.getShield().setInnerColor(gradientcolor);

        if (ship.getFluxTracker().isOverloaded() || (ship.getFluxTracker().getFluxLevel() > 0.9f)) {
            ship.getMutableStats().getHardFluxDissipationFraction().modifyFlat(ship.getId(), HARD_DISS * 0.01f);
            Global.getSoundPlayer().playLoop("emp_loop", ship, 1f,.6f, ship.getLocation(), ship.getVelocity());
            selfzapInterval.advance(amount);
            if (selfzapInterval.intervalElapsed()) {
                selfzapInterval.setInterval(0.3f, 0.6f);

                List<WeaponSlotAPI> vents = new ArrayList<>();
                for (WeaponSlotAPI weaponSlotAPI : ship.getHullSpec().getAllWeaponSlotsCopy()) {
                    if (weaponSlotAPI.isSystemSlot()) {
                        vents.add(weaponSlotAPI);
                    }
                }
                //If we have no vents, we can't do a dangerous overload this frame; ignore the rest of the code
                if (!vents.isEmpty()) {
                    Vector2f sourcePoint = vents.get(MathUtils.getRandomNumberInRange(0, vents.size() - 1)).computePosition(ship);

                    //And finally, fire at a random valid target
                    Global.getCombatEngine().spawnEmpArcPierceShields(ship, sourcePoint, ship, ship,
                            DamageType.ENERGY, //Damage type
                            MathUtils.getRandomNumberInRange(0.8f, 1.2f) * ARC_DAM_SELF, //Damage
                            MathUtils.getRandomNumberInRange(0.8f, 1.2f) * ARC_EMP_SELF, //Emp
                            100000, //Max range
                            "system_emp_emitter_impact", //Impact sound
                            20f, // thickness of the lightning bolt
                            LIGHTNING_CORE_COLOR, //Central color
                            LIGHTNING_FRINGE_COLOR //Fringe Color
                    );
                }
            }
        } else if (ship.getFluxTracker().isOverloaded() || (ship.getFluxTracker().getFluxLevel() < 0.8f)){
            ship.getMutableStats().getHardFluxDissipationFraction().unmodifyFlat(ship.getId());
        }
    }
}