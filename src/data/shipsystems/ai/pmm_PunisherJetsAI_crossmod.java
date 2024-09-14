package data.shipsystems.ai;

import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatAssignmentType;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.CombatFleetManagerAPI.AssignmentInfo;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.util.IntervalUtil;
import scripts.util.SWP_Util;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import org.lazywizard.lazylib.CollectionUtils;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

public class pmm_PunisherJetsAI_crossmod implements ShipSystemAIScript {

    private CombatEngineAPI engine;
    private ShipwideAIFlags flags;
    private ShipAPI ship;

    private final IntervalUtil tracker = new IntervalUtil(0.75f, 1.5f);

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        if (engine.isPaused()) {
            return;
        }

        tracker.advance(amount);

        if (tracker.intervalElapsed()) {
            if (!AIUtils.canUseSystemThisFrame(ship)) {
                return;
            }

            float decisionLevel = 0f;

            AssignmentInfo assignment = engine.getFleetManager(ship.getOwner()).getTaskManager(ship.isAlly()).getAssignmentFor(ship);
            Vector2f targetSpot;
            if (assignment != null && assignment.getTarget() != null) {
                targetSpot = assignment.getTarget().getLocation();
            } else {
                targetSpot = null;
            }
            boolean pointedAtTargetSpot = false;
            if (targetSpot != null) {
                float angleToTargetSpot = VectorUtils.getAngleStrict(ship.getLocation(), targetSpot);
                if (MathUtils.getShortestRotation(angleToTargetSpot, ship.getFacing()) <= 45f) {
                    pointedAtTargetSpot = true;
                }
            }
            boolean pointedAtTarget = false;
            boolean pointedAwayFromTarget = false;
            CombatEntityAPI immediateTarget;
            if (flags.getCustom(AIFlags.MANEUVER_TARGET) instanceof CombatEntityAPI) {
                immediateTarget = (CombatEntityAPI) flags.getCustom(AIFlags.MANEUVER_TARGET);
            } else if (target != null) {
                immediateTarget = target;
            } else {
                immediateTarget = ship.getShipTarget();
            }
            if (immediateTarget != null) {
                float angleToTarget = VectorUtils.getAngleStrict(ship.getLocation(), immediateTarget.getLocation());
                if (MathUtils.getShortestRotation(angleToTarget, ship.getFacing()) <= 45f) {
                    pointedAtTarget = true;
                } else if (MathUtils.getShortestRotation(angleToTarget, ship.getFacing()) >= 90f) {
                    pointedAwayFromTarget = true;
                }
            } else {
                pointedAwayFromTarget = true;
            }

            if (assignment != null && assignment.getType() == CombatAssignmentType.RETREAT) {
                float retreatDirection = (ship.getOwner() == 0) ? 270f : 90f;
                if (Math.abs(MathUtils.getShortestRotation(ship.getFacing(), retreatDirection)) <= 60f) {
                    decisionLevel += 15f;
                } else if (Math.abs(MathUtils.getShortestRotation(ship.getFacing(), retreatDirection)) > 90f) {
                    decisionLevel -= 15f;
                }
            }

            if (flags.hasFlag(AIFlags.RUN_QUICKLY)) {
                if (pointedAwayFromTarget) {
                    decisionLevel += 10f;
                } else {
                    decisionLevel -= 10f;
                }
            } else if (flags.hasFlag(AIFlags.PURSUING) || flags.hasFlag(AIFlags.HARASS_MOVE_IN)) {
                if (pointedAtTarget) {
                    decisionLevel += 10f;
                }
            } else if (immediateTarget != null) {
                if (pointedAtTarget) {
                    decisionLevel += 8f;
                }
            } else if (targetSpot != null) {
                if (pointedAtTargetSpot && !pointedAtTarget) {
                    decisionLevel += 12f;
                }
            }

            if (flags.hasFlag(AIFlags.TURN_QUICKLY)) {
                if (flags.hasFlag(AIFlags.HAS_INCOMING_DAMAGE)) {
                    decisionLevel += 10f;
                } else {
                    decisionLevel += 5f;
                }
            }

            if (flags.hasFlag(AIFlags.DO_NOT_PURSUE) && !pointedAwayFromTarget) {
                decisionLevel -= 10f;
            }
            if ((flags.hasFlag(AIFlags.DO_NOT_USE_FLUX) || flags.hasFlag(AIFlags.DO_NOT_USE_SHIELDS)) && !pointedAwayFromTarget) {
                decisionLevel -= 10f;
            }
            if (flags.hasFlag(AIFlags.NEEDS_HELP) && !pointedAwayFromTarget) {
                decisionLevel -= 5f;
            }
            if (flags.hasFlag(AIFlags.BACK_OFF) || flags.hasFlag(AIFlags.BACKING_OFF)) {
                if (pointedAwayFromTarget) {
                    decisionLevel += 2f;
                } else if (pointedAtTarget) {
                    decisionLevel *= 0.1f;
                } else {
                    decisionLevel *= 0.5f;
                }
            }

            Vector2f direction = VectorUtils.rotate(new Vector2f(1f, 0f), ship.getFacing());

            float range = 600f;
            List<ShipAPI> directTargets = SWP_Util.getShipsWithinRange(ship.getLocation(), range);
            if (!directTargets.isEmpty()) {
                Vector2f endpoint = new Vector2f(direction);
                endpoint.scale(range);
                Vector2f.add(endpoint, ship.getLocation(), endpoint);

                Collections.sort(directTargets, new CollectionUtils.SortEntitiesByDistance(ship.getLocation()));
                ListIterator<ShipAPI> iter = directTargets.listIterator();
                while (iter.hasNext()) {
                    ShipAPI tmp = iter.next();
                    if (tmp != ship && ship.getCollisionClass() != CollisionClass.NONE
                            && !tmp.isFighter() && !tmp.isDrone()) {
                        Vector2f loc = tmp.getLocation();
                        float areaChange = 1f;
                        if (tmp.getOwner() == ship.getOwner()) {
                            areaChange *= 1.5f;
                        }
                        if (CollisionUtils.getCollides(ship.getLocation(), endpoint, loc, tmp.getCollisionRadius()
                                * 0.5f + ship.getCollisionRadius() * 0.75f * areaChange)) {
                            if (ship.isFrigate()) {
                                if (tmp.isFrigate()) {
                                    decisionLevel -= 2.5f;
                                } else if (tmp.isDestroyer()) {
                                    decisionLevel -= 5f;
                                } else if (tmp.isCruiser()) {
                                    decisionLevel -= 7.5f;
                                } else {
                                    decisionLevel -= 10f;
                                }
                            } else if (ship.isDestroyer()) {
                                if (tmp.isFrigate() && !tmp.isHulk()) {
                                    decisionLevel -= 6f;
                                } else if (tmp.isDestroyer()) {
                                    decisionLevel -= 3f;
                                } else if (tmp.isCruiser()) {
                                    decisionLevel -= 6f;
                                } else {
                                    decisionLevel -= 9f;
                                }
                            } else if (ship.isCruiser()) {
                                if (tmp.isFrigate() && !tmp.isHulk()) {
                                    decisionLevel -= 10.5f;
                                } else if (tmp.isDestroyer() && !tmp.isHulk()) {
                                    decisionLevel -= 7f;
                                } else if (tmp.isCruiser()) {
                                    decisionLevel -= 3.5f;
                                } else {
                                    decisionLevel -= 7f;
                                }
                            } else {
                                if (tmp.isFrigate() && !tmp.isHulk()) {
                                    decisionLevel -= 16f;
                                } else if (tmp.isDestroyer() && !tmp.isHulk()) {
                                    decisionLevel -= 12f;
                                } else if (tmp.isCruiser() && !tmp.isHulk()) {
                                    decisionLevel -= 8f;
                                } else {
                                    decisionLevel -= 4f;
                                }
                            }
                        }
                    }
                }
            }

            if (decisionLevel >= 7f) {
                ship.useSystem();
            }
        }
    }

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.flags = flags;
        this.engine = engine;
    }
}
