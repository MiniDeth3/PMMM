package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.impl.combat.EntropyAmplifierStats;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.List;
    import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipSystemAPI.SystemState;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript.State;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.List;

    public class EntropicCollapse extends BaseShipSystemScript {
        public static Object KEY_SHIP = new Object();
        public static Object KEY_TARGET = new Object();
        public static float DAM_MULT = 1.5F;
        protected static float RANGE = 1500.0F;
        public static Color TEXT_COLOR = new Color(255, 55, 55, 255);
        public static Color JITTER_COLOR = new Color(255, 50, 50, 75);
        public static Color JITTER_UNDER_COLOR = new Color(255, 100, 100, 155);

        public EntropicCollapse() {
        }

        public void apply(MutableShipStatsAPI stats, final String id, ShipSystemStatsScript.State state, float effectLevel) {
            ShipAPI ship = null;
            if (stats.getEntity() instanceof ShipAPI) {
                ship = (ShipAPI)stats.getEntity();
                String targetDataKey = ship.getId() + "_entropy_target_data";
                Object targetDataObj = Global.getCombatEngine().getCustomData().get(targetDataKey);
                if (state == State.IN && targetDataObj == null) {
                    ShipAPI target = this.findTarget(ship);
                    Global.getCombatEngine().getCustomData().put(targetDataKey, new com.fs.starfarer.api.impl.combat.EntropyAmplifierStats.TargetData(ship, target));
                    if (target != null && (target.getFluxTracker().showFloaty() || ship == Global.getCombatEngine().getPlayerShip() || target == Global.getCombatEngine().getPlayerShip())) {
                        target.getFluxTracker().showOverloadFloatyIfNeeded("Amplified Entropy!", TEXT_COLOR, 4.0F, true);
                    }
                } else if (state == State.IDLE && targetDataObj != null) {
                    Global.getCombatEngine().getCustomData().remove(targetDataKey);
                    ((com.fs.starfarer.api.impl.combat.EntropyAmplifierStats.TargetData)targetDataObj).currDamMult = 1.0F;
                    targetDataObj = null;
                }

                if (targetDataObj != null && ((com.fs.starfarer.api.impl.combat.EntropyAmplifierStats.TargetData)targetDataObj).target != null) {
                    final com.fs.starfarer.api.impl.combat.EntropyAmplifierStats.TargetData targetData = (com.fs.starfarer.api.impl.combat.EntropyAmplifierStats.TargetData)targetDataObj;
                    targetData.currDamMult = 1.0F + (DAM_MULT - 1.0F) * effectLevel;
                    if (targetData.targetEffectPlugin == null) {
                        targetData.targetEffectPlugin = new BaseEveryFrameCombatPlugin() {
                            public void advance(float amount, List<InputEventAPI> events) {
                                if (!Global.getCombatEngine().isPaused()) {
                                    if (targetData.target == Global.getCombatEngine().getPlayerShip()) {
                                        Global.getCombatEngine().maintainStatusForPlayerShip(com.fs.starfarer.api.impl.combat.EntropyAmplifierStats.KEY_TARGET, targetData.ship.getSystem().getSpecAPI().getIconSpriteName(), targetData.ship.getSystem().getDisplayName(), (int)((targetData.currDamMult - 1.0F) * 100.0F) + "% more damage taken", true);
                                    }
                                    float thresh = 0f;
                                    if(targetData.target.isFrigate()) thresh = 0.2f;
                                    if(targetData.target.isDestroyer()) thresh = 0.15f;
                                    if(targetData.target.isCruiser()) thresh = 0.1f;
                                    if(targetData.target.isCapital()) thresh = 0.05f;



                                                if (!(targetData.currDamMult <= 1.0F) && targetData.ship.isAlive()) {
                                        targetData.target.getMutableStats().getHullDamageTakenMult().modifyMult(id, targetData.currDamMult);
                                        targetData.target.getMutableStats().getArmorDamageTakenMult().modifyMult(id, targetData.currDamMult);
                                        targetData.target.getMutableStats().getShieldDamageTakenMult().modifyMult(id, targetData.currDamMult);
                                        targetData.target.getMutableStats().getEmpDamageTakenMult().modifyMult(id, targetData.currDamMult);
                                        if(targetData.target.getHullLevel() < thresh){
                                            targetData.target.setCurrentCR(0);
                                        }
                                    } else {
                                        targetData.target.getMutableStats().getHullDamageTakenMult().unmodify(id);
                                        targetData.target.getMutableStats().getArmorDamageTakenMult().unmodify(id);
                                        targetData.target.getMutableStats().getShieldDamageTakenMult().unmodify(id);
                                        targetData.target.getMutableStats().getEmpDamageTakenMult().unmodify(id);
                                        Global.getCombatEngine().removePlugin(targetData.targetEffectPlugin);
                                    }

                                }
                            }
                        };
                        Global.getCombatEngine().addPlugin(targetData.targetEffectPlugin);
                    }

                    if (effectLevel > 0.0F) {
                        if (state != State.IN) {
                            targetData.elaspedAfterInState += Global.getCombatEngine().getElapsedInLastFrame();
                        }

                        float shipJitterLevel = 0.0F;
                        if (state == State.IN) {
                            shipJitterLevel = effectLevel;
                        } else {
                            float durOut = 0.5F;
                            shipJitterLevel = Math.max(0.0F, durOut - targetData.elaspedAfterInState) / durOut;
                        }

                        float maxRangeBonus = 50.0F;
                        float jitterRangeBonus = shipJitterLevel * maxRangeBonus;
                        Color color = JITTER_COLOR;
                        if (shipJitterLevel > 0.0F) {
                            ship.setJitter(KEY_SHIP, color, shipJitterLevel, 4, 0.0F, 0.0F + jitterRangeBonus * 1.0F);
                        }

                        if (effectLevel > 0.0F) {
                            targetData.target.setJitter(KEY_TARGET, color, effectLevel, 3, 0.0F, 5.0F);
                        }
                    }

                }
            }
        }

        public void unapply(MutableShipStatsAPI stats, String id) {
        }

        protected ShipAPI findTarget(ShipAPI ship) {
            float range = getMaxRange(ship);
            boolean player = ship == Global.getCombatEngine().getPlayerShip();
            ShipAPI target = ship.getShipTarget();
            if (ship.getShipAI() != null && ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.TARGET_FOR_SHIP_SYSTEM)) {
                target = (ShipAPI)ship.getAIFlags().getCustom(ShipwideAIFlags.AIFlags.TARGET_FOR_SHIP_SYSTEM);
            }

            float dist;
            if (target != null) {
                dist = Misc.getDistance(ship.getLocation(), target.getLocation());
                dist = ship.getCollisionRadius() + target.getCollisionRadius();
                if (dist > range + dist) {
                    target = null;
                }
            } else {
                if (target == null || target.getOwner() == ship.getOwner()) {
                    if (player) {
                        target = Misc.findClosestShipEnemyOf(ship, ship.getMouseTarget(), ShipAPI.HullSize.FIGHTER, range, true);
                    } else {
                        Object test = ship.getAIFlags().getCustom(ShipwideAIFlags.AIFlags.MANEUVER_TARGET);
                        if (test instanceof ShipAPI) {
                            target = (ShipAPI)test;
                            dist = Misc.getDistance(ship.getLocation(), target.getLocation());
                            float radSum = ship.getCollisionRadius() + target.getCollisionRadius();
                            if (dist > range + radSum) {
                                target = null;
                            }
                        }
                    }
                }

                if (target == null) {
                    target = Misc.findClosestShipEnemyOf(ship, ship.getLocation(), ShipAPI.HullSize.FIGHTER, range, true);
                }
            }

            return target;
        }

        public static float getMaxRange(ShipAPI ship) {
            return ship.getMutableStats().getSystemRangeBonus().computeEffective(RANGE);
        }

        public ShipSystemStatsScript.StatusData getStatusData(int index, ShipSystemStatsScript.State state, float effectLevel) {
            if (effectLevel > 0.0F && index == 0) {
                float damMult = 1.0F + (DAM_MULT - 1.0F) * effectLevel;
                return new ShipSystemStatsScript.StatusData((int)((damMult - 1.0F) * 100.0F) + "% more damage to target", false);
            } else {
                return null;
            }
        }

        public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
            if (system.isOutOfAmmo()) {
                return null;
            } else if (system.getState() != ShipSystemAPI.SystemState.IDLE) {
                return null;
            } else {
                ShipAPI target = this.findTarget(ship);
                if (target != null && target != ship) {
                    return "READY";
                } else {
                    return target == null && ship.getShipTarget() != null ? "OUT OF RANGE" : "NO TARGET";
                }
            }
        }

        public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
            ShipAPI target = this.findTarget(ship);
            return target != null && target != ship;
        }

        public static class TargetData {
            public ShipAPI ship;
            public ShipAPI target;
            public EveryFrameCombatPlugin targetEffectPlugin;
            public float currDamMult;
            public float elaspedAfterInState;

            public TargetData(ShipAPI ship, ShipAPI target) {
                this.ship = ship;
                this.target = target;
            }
        }
    }
