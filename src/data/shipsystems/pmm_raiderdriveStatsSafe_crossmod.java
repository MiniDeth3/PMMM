package data.shipsystems;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import java.util.HashMap;
import java.util.Map;

public class PMM_RaiderDriveStatsSafe_Crossmod extends BaseShipSystemScript {
    //start damper
    private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FIGHTER, 0.33f);
		mag.put(HullSize.FRIGATE, 0.33f);
		mag.put(HullSize.DESTROYER, 0.33f);
		mag.put(HullSize.CRUISER, 0.5f);
		mag.put(HullSize.CAPITAL_SHIP, 0.5f);
	}

    // private static float disabledTimer = 2f;

    // public static class WeaponDisabledTimerData {
    //     IntervalUtil interval = new IntervalUtil(100f, 100f);
    //     public WeaponDisabledTimerData(float interval) {
    //         this.interval=new IntervalUtil(interval,interval);
    //     }

    //     //boolean runOnces = false;
    // }
	//end damper

	public boolean isActive = false;
	public static final float ROF_BONUS = 2f;
	public static final float FLUX_REDUCTION = 50f;
	public static float SPEED_BONUS = 200f;
	public static float TURN_BONUS = 10f;
        
	private Color color = new Color(220, 50, 0,255);

	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

		//start damper
		//super.apply(amount);
		float effectLeveld = 1f;

            float multd = (Float) mag.get(HullSize.CRUISER);
            // float mult = 0.5f;
            if (stats.getVariant() != null) {
                multd = (Float) mag.get(stats.getVariant().getHullSize());
            }
            stats.getHullDamageTakenMult().modifyMult(id, 1f - (1f - multd) * effectLeveld);
            stats.getArmorDamageTakenMult().modifyMult(id, 1f - (1f - multd) * effectLeveld);
            stats.getEmpDamageTakenMult().modifyMult(id, 1f - (1f - multd) * effectLeveld);


            //ship.setJitter(stats, new Color(185,20,30,100), 1f, 1, 0, 7);
            //ship.setJitterUnder(stats, new Color(185,20,30,100), 1f, 2, 0, 5);
            //Global.getSoundPlayer().playSound("system_damper", 1.1f, 0.3f, ship.getLocation(), ship.getVelocity());

            //dataTimer.runOnces = true;

			// ShipAPI shipd;

			// CombatEngineAPI engine = Global.getCombatEngine();

			// String key = "acs_dampervent" + "_" + shipd.getId();

			// WeaponDisabledTimerData dataTimer = (WeaponDisabledTimerData) engine.getCustomData().get(key);
		//end damper
		float mult = 1f + ROF_BONUS * effectLevel;
		stats.getBallisticRoFMult().modifyMult(id, mult);
        stats.getEnergyRoFMult().modifyMult(id, mult);
        stats.getMissileRoFMult().modifyMult(id, mult);
		stats.getBallisticWeaponFluxCostMod().modifyPercent(id, -FLUX_REDUCTION);
                
		if (state == ShipSystemStatsScript.State.OUT) {
			stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
		} else {
			stats.getMaxSpeed().modifyFlat(id, 200f * effectLevel);
			stats.getAcceleration().modifyFlat(id, 200f * effectLevel);
			//stats.getAcceleration().modifyPercent(id, 200f * effectLevel);
		}
                
		if (stats.getEntity() instanceof ShipAPI) {
			ShipAPI ship = (ShipAPI) stats.getEntity();

			float amount = 1f;

			// ShipAPI shipd;

			CombatEngineAPI engine = Global.getCombatEngine();

			String key = "acs_raiderdriveStats" + "_" + ship.getId();

			// WeaponDisabledTimerData dataTimer = (WeaponDisabledTimerData) engine.getCustomData().get(key);

			// if (dataTimer == null) {
            
			// 	dataTimer = new WeaponDisabledTimerData((float) disabledTimer);
			// 	engine.getCustomData().put(key, dataTimer);
	
			// }
			
			ship.getEngineController().fadeToOtherColor(this, color, new Color(0,0,0,0), effectLevel, 0.67f);
			//ship.getEngineController().fadeToOtherColor(this, Color.white, new Color(0,0,0,0), effectLevel, 0.67f);
			ship.getEngineController().extendFlame(this, 2f * effectLevel, 0f * effectLevel, 0f * effectLevel);
			
                            //     if (state == State.OUT) {
                            //     //once
                            //     if (!isActive) {
                            // // ShipAPI empTarget = ship;
                            // // for (int x = 0; x < 30; x++) {
                            // //     Global.getCombatEngine().spawnEmpArc(ship, ship.getLocation(),
                            // //                        empTarget,
                            // //                        empTarget, DamageType.ENERGY, 0, 200,
                            // //                        2000, null, 30f, new Color(230,40,40,0),
                            // //                        new Color(255,255,255,0));
                            // // }	

							// // start damper
							// for (WeaponAPI weapon : ship.getAllWeapons()) {

							// 	weapon.disable();
				
							// }

							// // if (dataTimer.interval.getElapsed() != 0) {
							// // 	for (WeaponAPI weapon : ship.getAllWeapons()) {
				
							// // 		weapon.setRemainingCooldownTo(5f * 2);
					
							// // 	}
							// // } else {
							// // 	for (WeaponAPI weapon : ship.getAllWeapons()) {
				
							// // 		weapon.setRemainingCooldownTo(0f);
					
							// // 	}
							// // }

							
				
				
							// dataTimer.interval.advance(amount);
				
							// // if (dataTimer.interval.getElapsed() != 0) {
							// // 	for (WeaponAPI weapon : ship.getAllWeapons()) {

							// // 		weapon.setRemainingCooldownTo(0.1f);
					
							// // 	}
							// // }
				
							// if (dataTimer.interval.intervalElapsed()) {
							// 	for (WeaponAPI weapon : ship.getAllWeapons()) {
				
							// 		weapon.repair();
									
							// 		ship.syncWeaponDecalsWithArmorDamage();
					
							// 	}
				
				
							// }
							// //end damper
                               
                            // }
                            
							// 	  dataTimer.interval.setElapsed(0f); //damper part
                            //       isActive = true;
                            //     }//end once
                                }
	}
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getBallisticRoFMult().unmodify(id);
		stats.getBallisticWeaponFluxCostMod().unmodify(id);
		stats.getMaxSpeed().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
		stats.getEnergyRoFMult().unmodify(id);
		stats.getMissileRoFMult().unmodify(id);
		
		//start damper
		stats.getHullDamageTakenMult().unmodify(id);
		stats.getArmorDamageTakenMult().unmodify(id);
		stats.getEmpDamageTakenMult().unmodify(id);
		//end damper

                // isActive = false;
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		float mult = 1f + ROF_BONUS * effectLevel;
		float bonusPercent = (int) ((mult - 1f) * 100f);
		if (index == 0) {
			return new StatusData("increased engine power", false);
		}
		if (index == 1) {
			return new StatusData("ballistic/missile/energy rate of fire +" + (int) bonusPercent + "%", false);
		}
		if (index == 2) {
			return new StatusData("ballistic flux use -" + (int) FLUX_REDUCTION + "%", false);
		}
		return null;
	}
}
