package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.impl.combat.MineStrikeStats;
import com.fs.starfarer.api.impl.combat.MineStrikeStatsAIInfoProvider;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI.SystemState;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript.State;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import org.lwjgl.util.vector.Vector2f;

    public class PhaseMineFlakScript2 extends BaseShipSystemScript implements MineStrikeStatsAIInfoProvider {
        protected static float MINE_RANGE = 1000.0F;
        public static final float MIN_SPAWN_DIST = 75.0F;
        public static final float MIN_SPAWN_DIST_FRIGATE = 110.0F;
        public static final float LIVE_TIME = 5.0F;
        public static final Color JITTER_COLOR = new Color(255, 155, 255, 75);
        public static final Color JITTER_UNDER_COLOR = new Color(255, 155, 255, 155);

        public PhaseMineFlakScript2() {
        }

        public static float getRange(ShipAPI ship) {
            return ship == null ? MINE_RANGE : ship.getMutableStats().getSystemRangeBonus().computeEffective(MINE_RANGE);
        }

        public void apply(MutableShipStatsAPI stats, String id, ShipSystemStatsScript.State state, float effectLevel) {
            ShipAPI ship = null;
            if (stats.getEntity() instanceof ShipAPI) {
                ship = (ShipAPI)stats.getEntity();
                float jitterLevel = effectLevel;
                if (state == State.OUT) {
                    jitterLevel = effectLevel * effectLevel;
                }

                float maxRangeBonus = 25.0F;
                float jitterRangeBonus = jitterLevel * maxRangeBonus;
                ShipSystemStatsScript.State var10000 = State.OUT;
                ship.setJitterUnder(this, JITTER_UNDER_COLOR, jitterLevel, 11, 0.0F, 3.0F + jitterRangeBonus);
                ship.setJitter(this, JITTER_COLOR, jitterLevel, 4, 0.0F, 0.0F + jitterRangeBonus);
                if (state != State.IN) {
                    if (effectLevel >= 1.0F) {
                        Vector2f target = ship.getMouseTarget();
                        if (ship.getShipAI() != null && ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.SYSTEM_TARGET_COORDS)) {
                            target = (Vector2f)ship.getAIFlags().getCustom(ShipwideAIFlags.AIFlags.SYSTEM_TARGET_COORDS);
                        }

                        if (target != null) {
                            float dist = Misc.getDistance(ship.getLocation(), target);
                            float max = this.getMaxRange(ship) + ship.getCollisionRadius();
                            if (dist > max) {
                                float dir = Misc.getAngleInDegrees(ship.getLocation(), target);
                                target = Misc.getUnitVectorAtDegreeAngle(dir);
                                target.scale(max);
                                Vector2f.add(target, ship.getLocation(), target);
                            }

                            target = this.findClearLocation(ship, target);
                            if (target != null) {
                                this.spawnMine(ship, target);
                            }
                        }
                    } else {
                        var10000 = State.OUT;
                    }
                }

            }
        }

        public void unapply(MutableShipStatsAPI stats, String id) {
        }

        public void spawnMine(ShipAPI source, Vector2f mineLoc) {
            CombatEngineAPI engine = Global.getCombatEngine();
            Vector2f currLoc = Misc.getPointAtRadius(mineLoc, 30.0F + (float)Math.random() * 30.0F);
            float start = (float)Math.random() * 360.0F;

            for(float angle = start; angle < start + 390.0F; angle += 30.0F) {
                if (angle != start) {
                    Vector2f loc = Misc.getUnitVectorAtDegreeAngle(angle);
                    loc.scale(50.0F + (float)Math.random() * 30.0F);
                    currLoc = Vector2f.add(mineLoc, loc, new Vector2f());
                }

                Iterator var8 = Global.getCombatEngine().getMissiles().iterator();

                while(var8.hasNext()) {
                    MissileAPI other = (MissileAPI)var8.next();
                    if (other.isMine()) {
                        float dist = Misc.getDistance(currLoc, other.getLocation());
                        if (dist < other.getCollisionRadius() + 40.0F) {
                            currLoc = null;
                            break;
                        }
                    }
                }

                if (currLoc != null) {
                    break;
                }
            }

            if (currLoc == null) {
                currLoc = Misc.getPointAtRadius(mineLoc, 30.0F + (float)Math.random() * 30.0F);
            }

            MissileAPI mine = (MissileAPI)engine.spawnProjectile(source, (WeaponAPI)null, "minelayer2", currLoc, (float)Math.random() * 360.0F, (Vector2f)null);
            if (source != null) {
                Global.getCombatEngine().applyDamageModifiersToSpawnedProjectileWithNullWeapon(source, WeaponAPI.WeaponType.MISSILE, false, mine.getDamage());
            }

            float fadeInTime = 0.5F;
            mine.getVelocity().scale(0.0F);
            mine.fadeOutThenIn(fadeInTime);
            Global.getCombatEngine().addPlugin(this.createMissileJitterPlugin(mine, fadeInTime));
            float liveTime = 5.0F;
            mine.setFlightTime(mine.getMaxFlightTime() - liveTime);
            Global.getSoundPlayer().playSound("mine_teleport", 1.0F, 1.0F, mine.getLocation(), mine.getVelocity());
        }

        protected EveryFrameCombatPlugin createMissileJitterPlugin(final MissileAPI mine, final float fadeInTime) {
            return new BaseEveryFrameCombatPlugin() {
                float elapsed = 0.0F;

                public void advance(float amount, List<InputEventAPI> events) {
                    if (!Global.getCombatEngine().isPaused()) {
                        this.elapsed += amount;
                        float jitterLevel = mine.getCurrentBaseAlpha();
                        if (jitterLevel < 0.5F) {
                            jitterLevel *= 2.0F;
                        } else {
                            jitterLevel = (1.0F - jitterLevel) * 2.0F;
                        }

                        float jitterRange = 1.0F - mine.getCurrentBaseAlpha();
                        float maxRangeBonus = 50.0F;
                        float jitterRangeBonus = jitterRange * maxRangeBonus;
                        Color c = com.fs.starfarer.api.impl.combat.MineStrikeStats.JITTER_UNDER_COLOR;
                        c = Misc.setAlpha(c, 70);
                        mine.setJitter(this, c, jitterLevel, 15, jitterRangeBonus * 0.0F, jitterRangeBonus);
                        if (jitterLevel >= 1.0F || this.elapsed > fadeInTime) {
                            Global.getCombatEngine().removePlugin(this);
                        }

                    }
                }
            };
        }

        protected float getMaxRange(ShipAPI ship) {
            return this.getMineRange(ship);
        }

        public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
            if (system.isOutOfAmmo()) {
                return null;
            } else if (system.getState() != ShipSystemAPI.SystemState.IDLE) {
                return null;
            } else {
                Vector2f target = ship.getMouseTarget();
                if (target != null) {
                    float dist = Misc.getDistance(ship.getLocation(), target);
                    float max = this.getMaxRange(ship) + ship.getCollisionRadius();
                    return dist > max ? "OUT OF RANGE" : "READY";
                } else {
                    return null;
                }
            }
        }

        public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
            return ship.getMouseTarget() != null;
        }

        private Vector2f findClearLocation(ShipAPI ship, Vector2f dest) {
            if (this.isLocationClear(dest)) {
                return dest;
            } else {
                float incr = 50.0F;
                WeightedRandomPicker<Vector2f> tested = new WeightedRandomPicker();

                for(float distIndex = 1.0F; distIndex <= 32.0F; distIndex *= 2.0F) {
                    float start = (float)Math.random() * 360.0F;

                    for(float angle = start; angle < start + 360.0F; angle += 60.0F) {
                        Vector2f loc = Misc.getUnitVectorAtDegreeAngle(angle);
                        loc.scale(incr * distIndex);
                        Vector2f.add(dest, loc, loc);
                        tested.add(loc);
                        if (this.isLocationClear(loc)) {
                            return loc;
                        }
                    }
                }

                if (tested.isEmpty()) {
                    return dest;
                } else {
                    return (Vector2f)tested.pick();
                }
            }
        }

        private boolean isLocationClear(Vector2f loc) {
            Iterator var3 = Global.getCombatEngine().getShips().iterator();

            while(var3.hasNext()) {
                ShipAPI other = (ShipAPI)var3.next();
                if (!other.isShuttlePod() && !other.isFighter()) {
                    Vector2f otherLoc = other.getShieldCenterEvenIfNoShield();
                    float otherR = other.getShieldRadiusEvenIfNoShield();
                    if (other.isPiece()) {
                        otherLoc = other.getLocation();
                        otherR = other.getCollisionRadius();
                    }

                    float dist = Misc.getDistance(loc, otherLoc);
                    float checkDist = 75.0F;
                    if (other.isFrigate()) {
                        checkDist = 110.0F;
                    }

                    if (dist < otherR + checkDist) {
                        return false;
                    }
                }
            }

            var3 = Global.getCombatEngine().getAsteroids().iterator();

            while(var3.hasNext()) {
                CombatEntityAPI other = (CombatEntityAPI)var3.next();
                float dist = Misc.getDistance(loc, other.getLocation());
                if (dist < other.getCollisionRadius() + 75.0F) {
                    return false;
                }
            }

            return true;
        }

        public float getFuseTime() {
            return 3.0F;
        }

        public float getMineRange(ShipAPI ship) {
            return getRange(ship);
        }
    }
