package data.shipsystems;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.*;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

import static com.fs.starfarer.api.impl.combat.DamperFieldOmegaStats.KEY_SHIP;

public class PMM_EMP_Omega extends BaseShipSystemScript {
	public static float ARC_DAM = 250f;
	public static float ARC_EMP = 600f;
	public static float ARC_DAM_SELF = 0f;
	public static float ARC_EMP_SELF = 0f;

	public static Color LIGHTNING_CORE_COLOR = new Color(255,255,255,255);
	public static Color LIGHTNING_FRINGE_COLOR = new Color(255,50,50,255);

	private IntervalUtil zapInterval = new IntervalUtil(2f, 2.5f);
	private IntervalUtil selfzapInterval = new IntervalUtil (0.2f,0.4f);
	protected CombatEntityAPI chargeGlowEntity;
	protected RealityDisruptorChargeGlow chargeGlowPlugin;
	Vector2f shipvel = null;
	Color UNDERCOLOR = RiftCascadeEffect.EXPLOSION_UNDERCOLOR;

	public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
		//interval.advance(amount);

		boolean charging = weapon.getChargeLevel() > 0 && weapon.getCooldownRemaining() <= 0;
		if (charging && chargeGlowEntity == null) {
			chargeGlowPlugin = new RealityDisruptorChargeGlow(weapon);
			chargeGlowEntity = Global.getCombatEngine().addLayeredRenderingPlugin(chargeGlowPlugin);
		} else if (!charging && chargeGlowEntity != null) {
			chargeGlowEntity = null;
			chargeGlowPlugin = null;
		}
	}

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
        ship = (ShipAPI) stats.getEntity();
		CombatEngineAPI engine = Global.getCombatEngine();
		Vector2f pt = ship.getLocation();
		float shipsize = ship.getShieldRadiusEvenIfNoShield();
		pt = Misc.getPointWithinRadius(pt, 90 * 1.0F);
		ship.fadeToColor(KEY_SHIP, new Color(60, 50, 50, 255), 0.1F, 1F, effectLevel);
		ship.setWeaponGlow(effectLevel, new Color(125, 25, 50, 155), EnumSet.of(WeaponAPI.WeaponType.BALLISTIC, WeaponAPI.WeaponType.ENERGY, WeaponAPI.WeaponType.MISSILE));
		ship.getEngineController().fadeToOtherColor(KEY_SHIP, new Color(0, 0, 0, 0), new Color(0, 0, 0, 0), effectLevel, 0.75F * effectLevel);
		ship.setJitterUnder(KEY_SHIP, new Color(150, 50, 75, 155), effectLevel, 15, 0.0F, 15.0F);
		effectLevel = 1.0F;
		shipvel = new Vector2f(0,0);
		//engine.addSwirlyNebulaParticle(ship.getLocation(), shipvel, shipsize * 0.5f, 2f, 0.25F /1.5f + 1.5f * (float)Math.random(), 0.0F, 1.8f, new Color(0, 0, 125, 100), true);
		engine.addSwirlyNebulaParticle(ship.getLocation(), shipvel, shipsize * 1.1f, 1.5f, 0.25F /1.5f + 1.5f * (float)Math.random(), 0.0F, 2.5f, new Color(100, 0, 25, 100), true);
		engine.addSwirlyNebulaParticle(ship.getLocation(), shipvel, shipsize * 1.2f, 1.5f, 0.25F /1.5f + 1.5f * (float)Math.random(), 0.0F, 2.5f, new Color(100, 0, 25, 100), true);
		engine.addNegativeNebulaParticle(ship.getLocation(), shipvel, shipsize * 1.2f, 1.5f, 0.25F /1.5f + 1.5f * (float)Math.random(), 0.0F, 2.5f, new Color(100, 0, 25, 100));
		//engine.addNegativeNebulaParticle(ship.getLocation(), shipvel, shipsize * 0.5f, 1.5f, 0.25F /1.5f + 1.5f * (float)Math.random(), 0.0F, 1f, RiftLanceEffect.getColorForDarkening(UNDERCOLOR));

		selfzapInterval.advance(0.05f);
		if (selfzapInterval.intervalElapsed()) {
			selfzapInterval.setInterval(0.3f, 0.6f);

			List<WeaponSlotAPI> vents = new ArrayList<WeaponSlotAPI>();
			for (WeaponSlotAPI weaponSlotAPI : ship.getHullSpec().getAllWeaponSlotsCopy()) {
					if (weaponSlotAPI.isSystemSlot()) {
						vents.add(weaponSlotAPI);
					}
			}
			//If we have no vents, we can't do a dangerous overload this frame; ignore the rest of the code
			if (!vents.isEmpty()) {
				Vector2f sourcePoint = vents.get(MathUtils.getRandomNumberInRange(0, vents.size() - 1)).computePosition(ship);

				//And finally, fire at a random valid target
				CombatEntityAPI selftarget = ship;
				float thickness = 40f;
				float coreWidthMult = 0.3f;
						EmpArcEntityAPI arc = engine.spawnEmpArcPierceShields(ship, sourcePoint, ship, selftarget,
								DamageType.ENERGY, //Damage type
								 ARC_DAM_SELF, //Damage
								ARC_EMP_SELF, //Emp
								100000, //Max range
								"realitydisruptor_emp_impact", //Impact sound
								thickness, // thickness of the lightning bolt
								LIGHTNING_FRINGE_COLOR, //Central color
								LIGHTNING_CORE_COLOR//Fringe Color
								);
								arc.setCoreWidthOverride(thickness * coreWidthMult);
			}
		}

		Global.getSoundPlayer().playLoop("system_high_energy_focus_loop", ship, ship.getFluxLevel() * 0.5f, ship.getFluxLevel() * 1.1f + 0.2f, ship.getLocation(), ship.getVelocity());

		float EMP_arc_speed_level = (0.9f - ship.getFluxTracker().getFluxLevel());
		//        float EMP_arc_dmg_level = (1f + ship.getFluxTracker().getFluxLevel());

		//        IntervalUtil zapInterval = new IntervalUtil((EMP_arc_speed_level * 0.6f), (EMP_arc_speed_level * 0.8f));


		//Sets our hullsize-dependant variables
		float actualLightningRange = 600f;

		//Can't EMP while overloaded or Dead
		if (!ship.isHulk()); {


				//Choose a random vent port to send lightning from
				List<WeaponSlotAPI> vents = new ArrayList<WeaponSlotAPI>();
				for (WeaponSlotAPI weaponSlotAPI : ship.getHullSpec().getAllWeaponSlotsCopy()) {
					if (weaponSlotAPI.isSystemSlot()) {
						vents.add(weaponSlotAPI);
					}

				//If we have no vents, we can't do a dangerous overload this frame; ignore the rest of the code
				if (!vents.isEmpty()) {
					Vector2f sourcePoint = vents.get(MathUtils.getRandomNumberInRange(0, vents.size() - 1)).computePosition(ship);

					//Then, find all valid targets: we can only shoot missiles, ships and asteroids [including ourselves]
					List<CombatEntityAPI> validTargets = new ArrayList<CombatEntityAPI>();
					for (CombatEntityAPI entityToTest : CombatUtils.getEntitiesWithinRange(sourcePoint, actualLightningRange)) {
						if (entityToTest instanceof ShipAPI || entityToTest instanceof AsteroidAPI || entityToTest instanceof MissileAPI) {
							//Phased targets, and targets with no collision, are ignored
							if (entityToTest instanceof ShipAPI) {
								if (((ShipAPI) entityToTest).isPhased() || entityToTest == ship || (entityToTest.getOwner() == ship.getOwner())) {
									continue;
								}
							}
							//This Should mean it targets enemy missiles and not friendly ones...?
							if (entityToTest instanceof MissileAPI) {
								if ((entityToTest.getOwner() == ship.getOwner()) || (((MissileAPI) entityToTest).isFizzling())) {
									continue;
								}
							}
							if (entityToTest.getCollisionClass().equals(CollisionClass.NONE)) {
								continue;
							}
							validTargets.add(entityToTest);
						}
					}

					//If we have no valid targets, zap a random point near us
					if (validTargets.isEmpty()) {
						validTargets.add(new SimpleEntity(MathUtils.getRandomPointInCircle(sourcePoint, actualLightningRange)));
					}
					zapInterval.advance(0.075f);
					if (zapInterval.intervalElapsed()) {

						//And finally, fire at a random valid target
						CombatEntityAPI target = validTargets.get(MathUtils.getRandomNumberInRange(0, validTargets.size() - 1));
						Vector2f NearTarget = target.getLocation();

						float thickness = 40f;
						float coreWidthMult = 0.3f;
								EmpArcEntityAPI arc = engine.spawnEmpArc(ship, sourcePoint, ship, target,
										DamageType.ENERGY, //Damage type
										ARC_DAM, //Damage
										ARC_EMP, //Emp
										100000, //Max range
										"realitydisruptor_emp_impact", //Impact sound
										thickness, // thickness of the lightning bolt
										LIGHTNING_FRINGE_COLOR, //Central color
										LIGHTNING_CORE_COLOR//Fringe Color
								);
								arc.setCoreWidthOverride(thickness * coreWidthMult);

						zapInterval.setInterval(0f,2f);

						if (target instanceof ShipAPI) {
							ShipAPI target2 = (ShipAPI) target;
							engine.addSwirlyNebulaParticle(NearTarget, new Vector2f(0,0), (30f * 1.5f), 1.5f, 0.25F /1.5f + 1.5f * (float)Math.random(), 0.0F, 0.3f, new Color(0, 0, 125, 255), true);
							engine.addSwirlyNebulaParticle(NearTarget, new Vector2f(0,0), (40f * 1.5f), 1.5f, 0.25F /1.5f + 1.5f * (float)Math.random(), 0.0F, 0.1f, new Color(0, 0, 125, 255), true);
							engine.addSwirlyNebulaParticle(NearTarget, new Vector2f(0,0), (80f * 1.5f), 2f, 0.25F / 1.5f + 1.5f * (float) Math.random(), 0.0F, 1.8f, new Color(100, 0, 25, 255), true);
							engine.addSwirlyNebulaParticle(NearTarget, new Vector2f(0,0), (70f * 1.5f), 2f, 0.25F / 1.5f + 1.5f * (float) Math.random(), 0.0F, 2f, new Color(100, 0, 25, 255), true);
							engine.addSwirlyNebulaParticle(NearTarget, new Vector2f(0,0), (70f * 1.5f), 2f, 0.25F / 1.5f + 1.5f * (float) Math.random(), 0.0F, 2f, new Color(100, 0, 25, 255), true);
							engine.addNegativeNebulaParticle(NearTarget, new Vector2f(0,0), (85f * 1.5f), 1.5f, 0.25F / 1.5f + 1.5f * (float) Math.random(), 5F, 1.8f, new Color(100, 0, 25, 100));
							engine.addNegativeNebulaParticle(NearTarget, new Vector2f(0,0), (45f * 2f), 2F, 0.25F / 1.5f + 1.5f * (float) Math.random(), 0.0F, 2.5f, RiftLanceEffect.getColorForDarkening(UNDERCOLOR));

						} else if (target instanceof DamagingProjectileAPI) {
							DamagingProjectileAPI target2 = (DamagingProjectileAPI) target;
							engine.addSwirlyNebulaParticle(NearTarget, new Vector2f(0,0), (30f * 1.5f), 1.5f, 0.25F /1.5f + 1.5f * (float)Math.random(), 0.0F, 0.3f, new Color(0, 0, 125, 255), true);
							engine.addSwirlyNebulaParticle(NearTarget, new Vector2f(0,0), (40f * 1.5f), 1.5f, 0.25F /1.5f + 1.5f * (float)Math.random(), 0.0F, 0.1f, new Color(0, 0, 125, 255), true);
							engine.addSwirlyNebulaParticle(NearTarget, new Vector2f(0,0), (80f * 1.5f), 2f, 0.25F / 1.5f + 1.5f * (float) Math.random(), 0.0F, 1.8f, new Color(100, 0, 25, 255), true);
							engine.addSwirlyNebulaParticle(NearTarget, new Vector2f(0,0), (70f * 1.5f), 2f, 0.25F / 1.5f + 1.5f * (float) Math.random(), 0.0F, 2f, new Color(100, 0, 25, 255), true);
							engine.addSwirlyNebulaParticle(NearTarget, new Vector2f(0,0), (70f * 1.5f), 2f, 0.25F / 1.5f + 1.5f * (float) Math.random(), 0.0F, 2f, new Color(100, 0, 25, 255), true);
							engine.addNegativeNebulaParticle(NearTarget, new Vector2f(0,0), (85f * 1.5f), 1.5f, 0.25F / 1.5f + 1.5f * (float) Math.random(), 5F, 1.8f, new Color(100, 0, 25, 100));
							engine.addNegativeNebulaParticle(NearTarget, new Vector2f(0,0), (45f * 2f), 2F, 0.25F / 1.5f + 1.5f * (float) Math.random(), 0.0F, 2.5f, RiftLanceEffect.getColorForDarkening(UNDERCOLOR));

						} else if (target instanceof SimpleEntity){
							Global.getSoundPlayer().playSound("realitydisruptor_emp_impact", 1f, 1f, NearTarget, new Vector2f()); // Fixes sound not playing on hitting nothing (Thanks Vanilla!)
							engine.addSwirlyNebulaParticle(NearTarget, new Vector2f(0,0), 30f, 1.5f, 0.25F /1.5f + 1.5f * (float)Math.random(), 0.0F, 0.3f, new Color(0, 0, 125, 255), true);
							engine.addSwirlyNebulaParticle(NearTarget, new Vector2f(0,0), 40f, 1.5f, 0.25F /1.5f + 1.5f * (float)Math.random(), 0.0F, 0.1f, new Color(0, 0, 125, 255), true);
							engine.addSwirlyNebulaParticle(NearTarget, new Vector2f(0,0), 80f, 2f, 0.25F / 1.5f + 1.5f * (float) Math.random(), 0.0F, 1.8f, new Color(100, 0, 25, 255), true);
							engine.addSwirlyNebulaParticle(NearTarget, new Vector2f(0,0), 70f, 2f, 0.25F / 1.5f + 1.5f * (float) Math.random(), 0.0F, 2f, new Color(100, 0, 25, 255), true);
							engine.addSwirlyNebulaParticle(NearTarget, new Vector2f(0,0), 70f, 2f, 0.25F / 1.5f + 1.5f * (float) Math.random(), 0.0F, 2f, new Color(100, 0, 25, 255), true);
							engine.addNegativeNebulaParticle(NearTarget, new Vector2f(0,0), 85f, 1.5f, 0.25F / 1.5f + 1.5f * (float) Math.random(), 5F, 1.8f, new Color(100, 0, 25, 100));
							engine.addNegativeNebulaParticle(NearTarget, new Vector2f(0,0), 45f, 1.5F, 0.25F / 1.5f + 1.5f * (float) Math.random(), 0.0F, 2.5f, RiftLanceEffect.getColorForDarkening(UNDERCOLOR));

						}
					}

						validTargets.clear();

					vents.clear();
				}
			}
		}
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

	}
}








