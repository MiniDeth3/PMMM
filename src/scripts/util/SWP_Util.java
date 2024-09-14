package scripts.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI.SkillLevelAPI;
import com.fs.starfarer.api.combat.BattleObjectiveAPI;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI.ShipTypeHints;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.dark.shaders.util.ShaderLib;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class SWP_Util {

    public static final Set<String> SPECIAL_SHIPS = new HashSet<>();
    public static final Set<String> IBB_NO_AUTODEPLOY = new HashSet<>();

    public static boolean OFFSCREEN = false;
    public static final float OFFSCREEN_GRACE_CONSTANT = 500f;
    public static final float OFFSCREEN_GRACE_FACTOR = 2f;

    static {
        SPECIAL_SHIPS.add("ii_boss_dominus");
        SPECIAL_SHIPS.add("ii_boss_titanx");
        SPECIAL_SHIPS.add("msp_boss_potniaBis");
        SPECIAL_SHIPS.add("ms_boss_charybdis");
        SPECIAL_SHIPS.add("ms_boss_mimir");
        SPECIAL_SHIPS.add("tem_boss_paladin");
        SPECIAL_SHIPS.add("tem_boss_archbishop");
        SPECIAL_SHIPS.add("swp_boss_phaeton");
        SPECIAL_SHIPS.add("swp_boss_hammerhead");
        SPECIAL_SHIPS.add("swp_boss_sunder");
        SPECIAL_SHIPS.add("swp_boss_tarsus");
        SPECIAL_SHIPS.add("swp_boss_medusa");
        SPECIAL_SHIPS.add("swp_boss_falcon");
        SPECIAL_SHIPS.add("swp_boss_paragon");
        SPECIAL_SHIPS.add("swp_boss_mule");
        SPECIAL_SHIPS.add("swp_boss_aurora");
        SPECIAL_SHIPS.add("swp_boss_odyssey");
        SPECIAL_SHIPS.add("swp_boss_atlas");
        SPECIAL_SHIPS.add("swp_boss_afflictor");
        SPECIAL_SHIPS.add("swp_boss_brawler");
        SPECIAL_SHIPS.add("swp_boss_cerberus");
        SPECIAL_SHIPS.add("swp_boss_dominator");
        SPECIAL_SHIPS.add("swp_boss_doom");
        SPECIAL_SHIPS.add("swp_boss_euryale");
        SPECIAL_SHIPS.add("swp_boss_lasher_b");
        SPECIAL_SHIPS.add("swp_boss_lasher_r");
        SPECIAL_SHIPS.add("swp_boss_onslaught");
        SPECIAL_SHIPS.add("swp_boss_shade");
        SPECIAL_SHIPS.add("swp_boss_eagle");
        SPECIAL_SHIPS.add("swp_boss_beholder");
        SPECIAL_SHIPS.add("swp_boss_dominator_luddic_path");
        SPECIAL_SHIPS.add("swp_boss_onslaught_luddic_path");
        SPECIAL_SHIPS.add("swp_boss_conquest");
        SPECIAL_SHIPS.add("swp_boss_frankenstein");
        SPECIAL_SHIPS.add("swp_boss_sporeship");
        SPECIAL_SHIPS.add("swp_boss_excelsior");
        SPECIAL_SHIPS.add("uw_boss_astral");
        SPECIAL_SHIPS.add("uw_boss_cancer");
        SPECIAL_SHIPS.add("uw_boss_corruption");
        SPECIAL_SHIPS.add("uw_boss_cyst");
        SPECIAL_SHIPS.add("uw_boss_disease");
        SPECIAL_SHIPS.add("uw_boss_malignancy");
        SPECIAL_SHIPS.add("uw_boss_metastasis");
        SPECIAL_SHIPS.add("uw_boss_pustule");
        SPECIAL_SHIPS.add("uw_boss_tumor");
        SPECIAL_SHIPS.add("uw_boss_ulcer");
        SPECIAL_SHIPS.add("tiandong_boss_wuzhang");
        SPECIAL_SHIPS.add("pack_bulldog_bullseye");
        SPECIAL_SHIPS.add("pack_pitbull_bullseye");
        SPECIAL_SHIPS.add("pack_komondor_bullseye");
        SPECIAL_SHIPS.add("pack_schnauzer_bullseye");
        SPECIAL_SHIPS.add("diableavionics_IBBgulf");
        SPECIAL_SHIPS.add("sun_ice_ihs");
        SPECIAL_SHIPS.add("FOB_boss_rast");
        SPECIAL_SHIPS.add("tahlan_vestige");
        SPECIAL_SHIPS.add("loamtp_macnamara_boss");

        IBB_NO_AUTODEPLOY.add("msp_boss_potniaBis");
        IBB_NO_AUTODEPLOY.add("swp_boss_afflictor");
        IBB_NO_AUTODEPLOY.add("swp_boss_euryale");
        IBB_NO_AUTODEPLOY.add("swp_boss_shade");
        IBB_NO_AUTODEPLOY.add("uw_boss_corruption");
        IBB_NO_AUTODEPLOY.add("uw_boss_cyst");
        IBB_NO_AUTODEPLOY.add("uw_boss_disease");
        IBB_NO_AUTODEPLOY.add("uw_boss_malignancy");
        IBB_NO_AUTODEPLOY.add("uw_boss_metastasis");
        IBB_NO_AUTODEPLOY.add("uw_boss_pustule");
        IBB_NO_AUTODEPLOY.add("uw_boss_tumor");
        IBB_NO_AUTODEPLOY.add("uw_boss_ulcer");
        IBB_NO_AUTODEPLOY.add("pack_pitbull_bullseye");
        IBB_NO_AUTODEPLOY.add("pack_komondor_bullseye");
        IBB_NO_AUTODEPLOY.add("pack_schnauzer_bullseye");
    }

    public static void filterObscuredTargets(CombatEntityAPI primaryTarget, Vector2f originPoint, List nearbyTargets,
            boolean filterModules, boolean filterShielded, boolean filterBlocked) {
        Iterator<CombatEntityAPI> iter = nearbyTargets.iterator();
        while (iter.hasNext()) {
            CombatEntityAPI nearbyTarget = iter.next();
            if (nearbyTarget.getCollisionClass() == CollisionClass.NONE) {
                iter.remove();
                continue;
            }

            if (filterModules && (nearbyTarget instanceof ShipAPI)) {
                ShipAPI ship = (ShipAPI) nearbyTarget;
                if (ship.getParentStation() == primaryTarget) {
                    iter.remove();
                    continue;
                }
            }

            Vector2f nearestPoint = getNearestPointForDamage(originPoint, nearbyTarget);

            boolean remove = false;
            for (Object otherTarget : nearbyTargets) {
                if (!(otherTarget instanceof CombatEntityAPI)) {
                    continue;
                }
                CombatEntityAPI otherEntity = (CombatEntityAPI) otherTarget;

                if ((nearbyTarget.getOwner() != otherEntity.getOwner()) || (otherEntity == nearbyTarget)) {
                    continue;
                }

                if (filterShielded && (otherEntity.getShield() != null) && (otherEntity.getShield().isWithinArc(nearestPoint) && otherEntity.getShield().isOn()
                        && MathUtils.isWithinRange(nearestPoint, otherEntity.getShield().getLocation(), otherEntity.getShield().getRadius()))) {
                    remove = true;
                    break;
                }

                if (filterBlocked && CollisionUtils.getCollides(originPoint, nearestPoint, otherEntity.getLocation(), otherEntity.getCollisionRadius())) {
                    if (CollisionUtils.getCollisionPoint(nearestPoint, originPoint, otherEntity) != null) {
                        remove = true;
                        break;
                    }
                }
            }

            if (remove) {
                iter.remove();
            }
        }
    }

    public static Vector2f getNearestPointForDamage(Vector2f source, CombatEntityAPI entity) {
        if (entity instanceof DamagingProjectileAPI) {
            return entity.getLocation();
        }

        return CollisionUtils.getNearestPointOnBounds(source, entity);
    }

    public static String getNonDHullId(ShipHullSpecAPI spec) {
        if (spec == null) {
            return null;
        }
        if (spec.getDParentHullId() != null && !spec.getDParentHullId().isEmpty()) {
            return spec.getDParentHullId();
        } else {
            return spec.getHullId();
        }
    }

    /* LazyLib 2.4b revert */
    public static List<DamagingProjectileAPI> getProjectilesWithinRange(Vector2f location, float range) {
        List<DamagingProjectileAPI> projectiles = new ArrayList<>();

        for (DamagingProjectileAPI tmp : Global.getCombatEngine().getProjectiles()) {
            if ((tmp instanceof MissileAPI) || (tmp == null)) {
                continue;
            }

            if (MathUtils.isWithinRange(tmp.getLocation(), location, range)) {
                projectiles.add(tmp);
            }
        }

        return projectiles;
    }

    /* LazyLib 2.4b revert */
    public static List<MissileAPI> getMissilesWithinRange(Vector2f location, float range) {
        List<MissileAPI> missiles = new ArrayList<>();

        for (MissileAPI tmp : Global.getCombatEngine().getMissiles()) {
            if (MathUtils.isWithinRange(tmp.getLocation(), location, range)) {
                missiles.add(tmp);
            }
        }

        return missiles;
    }

    /* LazyLib 2.4b revert */
    public static List<ShipAPI> getShipsWithinRange(Vector2f location, float range) {
        List<ShipAPI> ships = new ArrayList<>();

        for (ShipAPI tmp : Global.getCombatEngine().getShips()) {
            if (tmp.isShuttlePod()) {
                continue;
            }

            if (MathUtils.isWithinRange(tmp, location, range)) {
                ships.add(tmp);
            }
        }

        return ships;
    }

    /* LazyLib 2.4b revert */
    public static List<CombatEntityAPI> getAsteroidsWithinRange(Vector2f location, float range) {
        List<CombatEntityAPI> asteroids = new ArrayList<>();

        for (CombatEntityAPI tmp : Global.getCombatEngine().getAsteroids()) {
            if (MathUtils.isWithinRange(tmp, location, range)) {
                asteroids.add(tmp);
            }
        }

        return asteroids;
    }

    /* LazyLib 2.4b revert */
    public static List<BattleObjectiveAPI> getObjectivesWithinRange(Vector2f location,
            float range) {
        List<BattleObjectiveAPI> objectives = new ArrayList<>();

        for (BattleObjectiveAPI tmp : Global.getCombatEngine().getObjectives()) {
            if (MathUtils.isWithinRange(tmp.getLocation(), location, range)) {
                objectives.add(tmp);
            }
        }

        return objectives;
    }

    /* LazyLib 2.4b revert */
    public static List<CombatEntityAPI> getEntitiesWithinRange(Vector2f location, float range) {
        List<CombatEntityAPI> entities = new ArrayList<>();

        for (CombatEntityAPI tmp : Global.getCombatEngine().getShips()) {
            if (MathUtils.isWithinRange(tmp, location, range)) {
                entities.add(tmp);
            }
        }

        // This also includes missiles
        for (CombatEntityAPI tmp : Global.getCombatEngine().getProjectiles()) {
            if (MathUtils.isWithinRange(tmp, location, range)) {
                entities.add(tmp);
            }
        }

        for (CombatEntityAPI tmp : Global.getCombatEngine().getAsteroids()) {
            if (MathUtils.isWithinRange(tmp, location, range)) {
                entities.add(tmp);
            }
        }

        return entities;
    }

    public static void applyForce(CombatEntityAPI target, Vector2f dir, float force) {
        if (target instanceof ShipAPI) {
            ShipAPI root = SWP_Multi.getRoot((ShipAPI) target);
            float forceRatio = root.getMass() / root.getMassWithModules();
            CombatUtils.applyForce(root, dir, force * forceRatio);
        } else {
            CombatUtils.applyForce(target, dir, force);
        }
    }

    public static int calculatePowerLevel(CampaignFleetAPI fleet) {
        if (fleet == null) {
            return 0;
        }

        float power = 0f;
        float totalDP = 0f;
        for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
            float dmods = 0f;
            float smods = 0f;
            float dmodPenalty = 0.06f;
            float smodBonus = 0.12f;
            if (member.getVariant() != null) {
                dmods = Math.min(5, DModManager.getNumDMods(member.getVariant()));
                smods = Misc.getCurrSpecialMods(member.getVariant());
            }
            if (member.getHullSpec().getHints().contains(ShipTypeHints.CARRIER)) {
                dmodPenalty = 0.04f;
            }
            float offPwr = 0f;
            if (member.getCaptain() != null) {
                for (SkillLevelAPI skill : member.getCaptain().getStats().getSkillsCopy()) {
                    if (skill.getSkill().isCombatOfficerSkill() && (skill.getLevel() > 0)) {
                        offPwr += 0.15f;
                        if (Math.round(skill.getLevel()) >= 2) {
                            offPwr += 0.05f;
                        }
                    }
                }
            }
            float civPenalty = 1f;
            if (member.isCivilian()) {
                civPenalty = 0.5f;
            } else {
                totalDP += member.getDeploymentPointsCost();
            }
            power += Math.max(1, member.getFleetPointCost()) * (1f - (dmods * dmodPenalty)) * (1f + (smods * smodBonus)) * (1f + offPwr) * civPenalty;
        }
        if (fleet.getCommander() != null) {
            float cdrPwr = 0f;
            float atten = 1f;
            if (totalDP > 240f) {
                atten = 240f / totalDP;
            }
            for (SkillLevelAPI skill : fleet.getCommander().getStats().getSkillsCopy()) {
                if (skill.getSkill().isAdmiralSkill() && (skill.getLevel() > 0)) {
                    cdrPwr += 0.1f;
                }
            }
            power *= 1f + (cdrPwr * atten);
        }
        return Math.round(power);
    }

    public static int calculatePowerLevel(List<ShipAPI> ships, CombatEngineAPI engine, int owner) {
        float power = 0f;
        float totalDP = 0f;
        for (ShipAPI ship : ships) {
            if ((ship.getOwner() != owner) || !ship.isAlive() || ship.isHulk() || ship.isFighter() || ship.isDrone() || ship.isShuttlePod()) {
                continue;
            }

            float dmods = 0f;
            float smods = 0f;
            float dmodPenalty = 0.06f;
            float smodBonus = 0.12f;
            if (ship.getVariant() != null) {
                dmods = Math.min(5, DModManager.getNumDMods(ship.getVariant()));
                smods = Misc.getCurrSpecialMods(ship.getVariant());
            }
            if (ship.getHullSpec().getHints().contains(ShipTypeHints.CARRIER)) {
                dmodPenalty = 0.04f;
            }
            float offPwr = 0f;
            if (ship.getCaptain() != null) {
                for (SkillLevelAPI skill : ship.getCaptain().getStats().getSkillsCopy()) {
                    if (skill.getSkill().isCombatOfficerSkill() && (skill.getLevel() > 0)) {
                        offPwr += 0.15f;
                        if (Math.round(skill.getLevel()) >= 2) {
                            offPwr += 0.05f;
                        }
                    }
                }
            }
            float civPenalty = 1f;
            if (ship.getHullSpec().getHints().contains(ShipTypeHints.CIVILIAN)) {
                civPenalty = 0.5f;
            } else {
                totalDP += ship.getHullSpec().getSuppliesToRecover();
            }
            power += Math.max(1, ship.getHullSpec().getFleetPoints()) * (1f - (dmods * dmodPenalty)) * (1f + (smods * smodBonus)) * (1f + offPwr) * civPenalty;
        }
        if (engine.getFleetManager(owner).getFleetCommander() != null) {
            float cdrPwr = 0f;
            float atten = 1f;
            if (totalDP > 240f) {
                atten = 240f / totalDP;
            }
            for (SkillLevelAPI skill : engine.getFleetManager(owner).getFleetCommander().getStats().getSkillsCopy()) {
                if (skill.getSkill().isAdmiralSkill() && (skill.getLevel() > 0)) {
                    cdrPwr += 0.1f;
                }
            }
            power *= 1f + (cdrPwr * atten);
        }
        return Math.round(power);
    }

    public static boolean isOnscreen(Vector2f point, float radius) {
        return OFFSCREEN || ShaderLib.isOnScreen(point, radius * OFFSCREEN_GRACE_FACTOR + OFFSCREEN_GRACE_CONSTANT);
    }

    public static int clamp255(int x) {
        return Math.max(0, Math.min(255, x));
    }

    public static float effectiveRadius(ShipAPI ship) {
        if (ship.getSpriteAPI() == null || ship.isPiece()) {
            return ship.getCollisionRadius();
        } else {
            float fudgeFactor = 1.5f;
            return ((ship.getSpriteAPI().getWidth() / 2f) + (ship.getSpriteAPI().getHeight() / 2f)) * 0.5f * fudgeFactor;
        }
    }

    public static Collection<String> getBuiltInHullMods(ShipAPI ship) {
        ShipVariantAPI tmp = ship.getVariant().clone();
        tmp.clearHullMods();
        return tmp.getHullMods();
    }

    public static float getMemberRadiusEstimate(FleetMemberAPI member) {
        float radius = 500f;
        if (member.getHullSpec().getShieldSpec() != null) {
            radius = member.getHullSpec().getShieldSpec().getRadius();
        } else if (!member.getHullSpec().getSpriteName().isEmpty()) {
            SpriteAPI sprite = Global.getSettings().getSprite(member.getHullSpec().getSpriteName());
            float fudgeFactor = 1.5f;
            radius = ((sprite.getWidth() / 2f) + (sprite.getHeight() / 2f)) * 0.5f * fudgeFactor;
        }
        return radius;
    }

    public static String getOrJoined(String... strings) {
        if (strings.length == 1) {
            return strings[0];
        }

        String result = "";
        for (int i = 0; i < strings.length - 1; i++) {
            result += strings[i] + ", ";
        }
        if (!result.isEmpty()) {
            result = result.substring(0, result.length() - 2);
        }
        if (strings.length >= 2) {
            result += " or " + strings[strings.length - 1];
        }
        return result;
    }

    public static Vector2f getSafeSpawn(float collisionRadius, FleetSide side, float mapX, float mapY, boolean pursuit) {
        Vector2f spawnLocation = new Vector2f();
        float searchRadius = collisionRadius * 1.25f;
        while (searchRadius > 0) {
            searchRadius -= collisionRadius * 0.05f;
            spawnLocation.x = MathUtils.getRandomNumberInRange(-mapX / 2f, mapX / 2f);
            spawnLocation.y = mapY / 2f;
            if (side == FleetSide.PLAYER) {
                spawnLocation.y *= -1f;
            }
            if (pursuit) {
                spawnLocation.y *= -1f;
            }

            boolean collision = false;
            List<ShipAPI> ships = SWP_Util.getShipsWithinRange(spawnLocation, searchRadius);
            for (ShipAPI ship : ships) {
                if (ship.isFighter() || ship.isDrone() || ship.isShuttlePod() || ship.isPiece()) {
                    continue;
                }
                collision = true;
            }

            if (!collision) {
                break;
            }
        }

        return spawnLocation;
    }

    public static Vector2f getCollisionRayCircle(Vector2f start, Vector2f end, Vector2f circle, float radius, boolean getNear) {
        float x1 = start.x - circle.x;
        float x2 = end.x - circle.x;
        float y1 = start.y - circle.y;
        float y2 = end.y - circle.y;
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dr_sqrd = (dx * dx) + (dy * dy);
        float D = (x1 * y2) - (x2 * y1);
        float delta = (radius * radius * dr_sqrd) - (D * D);
        if (delta < 0f) {
            return null;
        } else if (delta > 0f) {
            float x_sub = Math.signum(dy) * dx * (float) Math.sqrt(delta);
            float x_a = ((D * dy) + x_sub) / dr_sqrd;
            float x_b = ((D * dy) - x_sub) / dr_sqrd;
            float y_sub = Math.abs(dy) * (float) Math.sqrt(delta);
            float y_a = ((-D * dx) + y_sub) / dr_sqrd;
            float y_b = ((-D * dx) - y_sub) / dr_sqrd;
            float dax = x_a - x1;
            float dbx = x_b - x1;
            float day = y_a - y1;
            float dby = y_b - y1;
            float dist_a_sqrt = (dax * dax) + (day * day);
            float dist_b_sqrt = (dbx * dbx) + (dby * dby);
            if ((dist_a_sqrt < dist_b_sqrt) ^ !getNear) {
                return new Vector2f(x_a + circle.x, y_a + circle.y);
            } else {
                return new Vector2f(x_b + circle.x, y_b + circle.y);
            }
        } else {
            float x = (D * dy) / dr_sqrd;
            float y = (-D * dx) / dr_sqrd;
            return new Vector2f(x + circle.x, y + circle.y);
        }
    }

    public static Color interpolateColor(Color old, Color dest, float progress) {
        final float clampedProgress = Math.max(0f, Math.min(1f, progress));
        final float antiProgress = 1f - clampedProgress;
        final float[] ccOld = old.getComponents(null), ccNew = dest.getComponents(null);
        return new Color(clamp255((int) ((ccOld[0] * antiProgress) + (ccNew[0] * clampedProgress))),
                clamp255((int) ((ccOld[1] * antiProgress) + (ccNew[1] * clampedProgress))),
                clamp255((int) ((ccOld[2] * antiProgress) + (ccNew[2] * clampedProgress))),
                clamp255((int) ((ccOld[3] * antiProgress) + (ccNew[3] * clampedProgress))));
    }

    public static Color interpolateColor255(Color old, Color dest, float progress) {
        final float clampedProgress = Math.max(0f, Math.min(1f, progress));
        final float antiProgress = 1f - clampedProgress;
        final float[] ccOld = old.getComponents(null), ccNew = dest.getComponents(null);
        return new Color(clamp255((int) ((ccOld[0] * 255f * antiProgress) + (ccNew[0] * 255f * clampedProgress))),
                clamp255((int) ((ccOld[1] * 255f * antiProgress) + (ccNew[1] * 255f * clampedProgress))),
                clamp255((int) ((ccOld[2] * 255f * antiProgress) + (ccNew[2] * 255f * clampedProgress))),
                clamp255((int) ((ccOld[3] * 255f * antiProgress) + (ccNew[3] * 255f * clampedProgress))));
    }

    public static Color fadeColor255(Color color, float mult) {
        final float[] cc = color.getComponents(null);
        return new Color(clamp255((int) (cc[0] * 255f)),
                clamp255((int) (cc[1] * 255f)),
                clamp255((int) (cc[2] * 255f)),
                clamp255((int) (cc[3] * 255f * mult)));
    }

    // Algorithm by broofa @ stackoverflow.com
    // Returns position of where the projectile should head towards to hit the target
    // Returns null if the projectile can never hit the target
    public static Vector2f intercept(Vector2f point, float speed, Vector2f target, Vector2f targetVel) {
        final Vector2f difference = new Vector2f(target.x - point.x, target.y - point.y);

        final float a = targetVel.x * targetVel.x + targetVel.y * targetVel.y - speed * speed;
        final float b = 2 * (targetVel.x * difference.x + targetVel.y * difference.y);
        final float c = difference.x * difference.x + difference.y * difference.y;

        final Vector2f solutionSet = quad(a, b, c);

        Vector2f intercept = null;
        if (solutionSet != null) {
            float bestFit = Math.min(solutionSet.x, solutionSet.y);
            if (bestFit < 0) {
                bestFit = Math.max(solutionSet.x, solutionSet.y);
            }
            if (bestFit > 0) {
                intercept = new Vector2f(target.x + targetVel.x * bestFit, target.y + targetVel.y * bestFit);
            }
        }

        return intercept;
    }

    public static Vector2f intercept(Vector2f point, float speed, float acceleration, float maxspeed, Vector2f target,
            Vector2f targetVel) {
        final Vector2f difference = new Vector2f(target.x - point.x, target.y - point.y);

        float s = speed;
        float a = acceleration / 2f;
        float b = speed;
        float c = difference.length();
        Vector2f solutionSet = quad(a, b, c);
        if (solutionSet != null) {
            float t = Math.min(solutionSet.x, solutionSet.y);
            if (t < 0) {
                t = Math.max(solutionSet.x, solutionSet.y);
            }
            if (t > 0) {
                s = acceleration * t;
                s = s / 2f + speed;
                s = Math.min(s, maxspeed);
            }
        }

        a = targetVel.x * targetVel.x + targetVel.y * targetVel.y - s * s;
        b = 2 * (targetVel.x * difference.x + targetVel.y * difference.y);
        c = difference.x * difference.x + difference.y * difference.y;

        solutionSet = quad(a, b, c);

        Vector2f intercept = null;
        if (solutionSet != null) {
            float bestFit = Math.min(solutionSet.x, solutionSet.y);
            if (bestFit < 0) {
                bestFit = Math.max(solutionSet.x, solutionSet.y);
            }
            if (bestFit > 0) {
                intercept = new Vector2f(target.x + targetVel.x * bestFit, target.y + targetVel.y * bestFit);
            }
        }

        return intercept;
    }

    public static float lerp(float x, float y, float alpha) {
        return (1f - alpha) * x + alpha * y;
    }

    public static Vector2f quad(float a, float b, float c) {
        Vector2f solution = null;
        if (Float.compare(Math.abs(a), 0) == 0) {
            if (Float.compare(Math.abs(b), 0) == 0) {
                solution = (Float.compare(Math.abs(c), 0) == 0) ? new Vector2f(0, 0) : null;
            } else {
                solution = new Vector2f(-c / b, -c / b);
            }
        } else {
            float d = b * b - 4 * a * c;
            if (d >= 0) {
                d = (float) Math.sqrt(d);
                float e = 2 * a;
                solution = new Vector2f((-b - d) / e, (-b + d) / e);
            }
        }
        return solution;
    }

    public static String getLessAggressivePersonality(FleetMemberAPI member, ShipAPI ship) {
        if (ship == null) {
            return Personalities.CAUTIOUS;
        }

        boolean player = false;
        if ((member != null) && (member.getFleetData() != null) && (member.getFleetData().getFleet() != null)
                && member.getFleetData().getFleet().isPlayerFleet()) {
            player = true;
        }

        String personality = null;
        if (member != null) {
            if (member.getCaptain() != null) {
                /* Skip the player's ship or any player officer ships */
                if (player && (!member.getCaptain().isDefault() || member.getCaptain().isPlayer())) {
                    return null;
                }

                personality = member.getCaptain().getPersonalityAPI().getId();
            }
        } else {
            if (ship.getCaptain() != null) {
                personality = ship.getCaptain().getPersonalityAPI().getId();
            }
        }

        if ((ship.getShipAI() != null) && (ship.getShipAI().getConfig() != null)) {
            if (ship.getShipAI().getConfig().personalityOverride != null) {
                personality = ship.getShipAI().getConfig().personalityOverride;
            }
        }

        String newPersonality;
        if (personality == null) {
            newPersonality = Personalities.CAUTIOUS;
        } else {
            switch (personality) {
                case Personalities.TIMID:
                case Personalities.CAUTIOUS:
                    newPersonality = Personalities.TIMID;
                    break;
                default:
                case Personalities.STEADY:
                    newPersonality = Personalities.CAUTIOUS;
                    break;
                case Personalities.AGGRESSIVE:
                    newPersonality = Personalities.STEADY;
                    break;
                case Personalities.RECKLESS:
                    newPersonality = Personalities.AGGRESSIVE;
                    break;
            }
        }

        return newPersonality;
    }

    public static String getMoreAggressivePersonality(FleetMemberAPI member, ShipAPI ship) {
        if (ship == null) {
            return Personalities.AGGRESSIVE;
        }

        boolean player = false;
        if ((member != null) && (member.getFleetData() != null) && (member.getFleetData().getFleet() != null)
                && member.getFleetData().getFleet().isPlayerFleet()) {
            player = true;
        }

        String personality = null;
        if (member != null) {
            if (member.getCaptain() != null) {
                /* Skip the player's ship or any player officer ships */
                if (player && (!member.getCaptain().isDefault() || member.getCaptain().isPlayer())) {
                    return null;
                }

                personality = member.getCaptain().getPersonalityAPI().getId();
            }
        } else {
            if (ship.getCaptain() != null) {
                personality = ship.getCaptain().getPersonalityAPI().getId();
            }
        }

        if ((ship.getShipAI() != null) && (ship.getShipAI().getConfig() != null)) {
            if (ship.getShipAI().getConfig().personalityOverride != null) {
                personality = ship.getShipAI().getConfig().personalityOverride;
            }
        }

        String newPersonality;
        if (personality == null) {
            newPersonality = Personalities.AGGRESSIVE;
        } else {
            switch (personality) {
                case Personalities.TIMID:
                    newPersonality = Personalities.CAUTIOUS;
                    break;
                case Personalities.CAUTIOUS:
                    newPersonality = Personalities.STEADY;
                    break;
                default:
                case Personalities.STEADY:
                    newPersonality = Personalities.AGGRESSIVE;
                    break;
                case Personalities.AGGRESSIVE:
                case Personalities.RECKLESS:
                    newPersonality = Personalities.RECKLESS;
                    break;
            }
        }

        return newPersonality;
    }

    public static void initExtraFactions() {
        if (Global.getSector() == null) {
            return;
        }

        Map<String, Float> domainFactions = new HashMap<>();
        domainFactions.put(Factions.HEGEMONY, 1f);
        domainFactions.put(Factions.INDEPENDENT, 0.5f);
        domainFactions.put(Factions.LUDDIC_CHURCH, 0.5f);
        domainFactions.put(Factions.PERSEAN, 1f);
        domainFactions.put(Factions.PIRATES, 1f);
        domainFactions.put(Factions.SCAVENGERS, 0.25f);
        domainFactions.put(Factions.DIKTAT, 0.5f);
        domainFactions.put(Factions.TRITACHYON, 1f);
        domainFactions.put(Factions.DERELICT, 0.25f);
        domainFactions.put("tiandong", 0.75f);
        domainFactions.put("mayasura", 0.5f);

        Map<String, Float> sectorFactions = new HashMap<>();
        sectorFactions.putAll(domainFactions);
        sectorFactions.put(Factions.LIONS_GUARD, 0.5f);
        sectorFactions.put(Factions.LUDDIC_PATH, 0.5f);
        sectorFactions.put(Factions.REMNANTS, 1f);
        sectorFactions.put("ORA", 0.75f);
        sectorFactions.put("interstellarimperium", 0.75f);
        sectorFactions.put("ii_imperial_guard", 0.25f);
        sectorFactions.put("blackrock_driveyards", 0.75f);
        sectorFactions.put("br_consortium", 0.25f);
        sectorFactions.put("syndicate_asp", 0.25f);
        sectorFactions.put("shadow_industry", 1f);
        sectorFactions.put("junk_pirates", 0.75f);
        sectorFactions.put("pack", 0.5f);
        sectorFactions.put("exipirated", 0.5f);
        sectorFactions.put("SCY", 1f);
        sectorFactions.put("diableavionics", 1f);
        sectorFactions.put("Coalition", 1f);
        sectorFactions.put("dassault_mikoyan", 0.75f);
        sectorFactions.put("sun_ice", 0.5f);
        sectorFactions.put("kadur_remnant", 0.75f);
        sectorFactions.put("al_ars", 1f);
        sectorFactions.put("mayasuran_guard", 0.25f);
        sectorFactions.put("metelson", 0.75f);
        sectorFactions.put("communist_clouds", 0.5f);
        sectorFactions.put("xhanempire", 0.75f);
        sectorFactions.put("tahlan_legioinfernalis", 0.5f);
        sectorFactions.put("scalartech", 0.5f);

        Map<String, Float> everythingFactions = new HashMap<>();
        everythingFactions.putAll(sectorFactions);
        everythingFactions.put(Factions.OMEGA, 1f);
        everythingFactions.put("templars", 0.5f);
        everythingFactions.put("cabal", 0.5f);
        everythingFactions.put("exigency", 0.75f);
        everythingFactions.put("approlight", 0.75f);
        everythingFactions.put("immortallight", 0.25f);
        everythingFactions.put("fob", 0.75f);
        everythingFactions.put("blade_breakers", 0.5f);
        everythingFactions.put("OCI", 0.5f);
        everythingFactions.put("kingdom_of_terra", 0.5f);
        everythingFactions.put("sylphon", 1f);
        everythingFactions.put("tahlan_greathouses", 0.75f);

        List<FactionAPI> extraFactionAPIs = new ArrayList<>();
        extraFactionAPIs.add(Global.getSector().getFaction("domain"));
        extraFactionAPIs.add(Global.getSector().getFaction("sector"));
        extraFactionAPIs.add(Global.getSector().getFaction("everything"));

        for (FactionAPI extraFactionAPI : extraFactionAPIs) {
            Map<String, Float> factions;
            switch (extraFactionAPI.getId()) {
                case "domain":
                    factions = domainFactions;
                    break;
                case "sector":
                    factions = sectorFactions;
                    break;
                case "everything":
                    factions = everythingFactions;
                    break;
                default:
                    continue;
            }

            Set<String> setCopy;
            setCopy = new LinkedHashSet<>(extraFactionAPI.getAlwaysKnownShips());
            for (String alwaysKnownShip : setCopy) {
                extraFactionAPI.removeUseWhenImportingShip(alwaysKnownShip);
            }
            setCopy = new LinkedHashSet<>(extraFactionAPI.getKnownShips());
            for (String knownShip : setCopy) {
                extraFactionAPI.removeKnownShip(knownShip);
            }
            setCopy = new LinkedHashSet<>(extraFactionAPI.getKnownFighters());
            for (String knownFighter : setCopy) {
                extraFactionAPI.removeKnownFighter(knownFighter);
            }
            setCopy = new LinkedHashSet<>(extraFactionAPI.getKnownWeapons());
            for (String knownWeapon : setCopy) {
                extraFactionAPI.removeKnownWeapon(knownWeapon);
            }
            setCopy = new LinkedHashSet<>(extraFactionAPI.getKnownHullMods());
            for (String knownHullMod : setCopy) {
                extraFactionAPI.removeKnownHullMod(knownHullMod);
            }
            setCopy = new LinkedHashSet<>(extraFactionAPI.getPriorityShips());
            for (String priorityShip : setCopy) {
                extraFactionAPI.removePriorityShip(priorityShip);
            }
            setCopy = new LinkedHashSet<>(extraFactionAPI.getPriorityFighters());
            for (String priorityFighter : setCopy) {
                extraFactionAPI.removePriorityFighter(priorityFighter);
            }
            setCopy = new LinkedHashSet<>(extraFactionAPI.getPriorityWeapons());
            for (String priorityWeapon : setCopy) {
                extraFactionAPI.removePriorityWeapon(priorityWeapon);
            }
            setCopy = new LinkedHashSet<>(extraFactionAPI.getPriorityHullMods());
            for (String priorityHullMod : setCopy) {
                extraFactionAPI.removePriorityHullMod(priorityHullMod);
            }

            extraFactionAPI.clearShipRoleCache();

            for (Entry<String, Float> faction : factions.entrySet()) {
                FactionAPI factionAPI;
                try {
                    factionAPI = Global.getSector().getFaction(faction.getKey());
                } catch (RuntimeException e) {
                    continue;
                }
                if (factionAPI == null) {
                    try {
                        factionAPI = Global.getSettings().createBaseFaction(faction.getKey());
                    } catch (RuntimeException e) {
                        continue;
                    }
                }
                if (factionAPI == null) {
                    continue;
                }

                for (String alwaysKnownShip : factionAPI.getAlwaysKnownShips()) {
                    if (!extraFactionAPI.getAlwaysKnownShips().contains(alwaysKnownShip)) {
                        extraFactionAPI.addUseWhenImportingShip(alwaysKnownShip);
                    }
                }
                for (String knownShip : factionAPI.getKnownShips()) {
                    if (!extraFactionAPI.getKnownShips().contains(knownShip)) {
                        extraFactionAPI.addKnownShip(knownShip, false);
                        extraFactionAPI.getHullFrequency().put(knownShip, faction.getValue());
                    } else {
                        Float freq = extraFactionAPI.getHullFrequency().get(knownShip);
                        if (freq == null) {
                            freq = faction.getValue();
                        } else {
                            freq += faction.getValue();
                        }
                        extraFactionAPI.getHullFrequency().put(knownShip, freq);
                    }
                }
                for (String knownFighter : factionAPI.getKnownFighters()) {
                    if (!extraFactionAPI.getKnownFighters().contains(knownFighter)) {
                        extraFactionAPI.addKnownFighter(knownFighter, false);
                    }
                }
                for (String knownWeapon : factionAPI.getKnownWeapons()) {
                    if (!extraFactionAPI.getKnownWeapons().contains(knownWeapon)) {
                        extraFactionAPI.addKnownWeapon(knownWeapon, false);
                    }
                }
                for (String knownHullMod : factionAPI.getKnownHullMods()) {
                    if (!extraFactionAPI.getKnownHullMods().contains(knownHullMod)) {
                        extraFactionAPI.addKnownHullMod(knownHullMod);
                    }
                }
            }

            extraFactionAPI.clearShipRoleCache();
        }
    }

    public static enum RequiredFaction {

        NONE, JUNK_PIRATES, TIANDONG, SHADOWYARDS, IMPERIUM, TEMPLARS, DIABLE, BLACKROCK, EXIGENCY, SCY, CABAL, CURSED, ORA, TYRADOR, DME, ICE, BORKEN, SCALARTECH, ARKGNEISIS;

        public boolean isLoaded() {
            switch (this) {
                case NONE:
                default:
                    return true;
            }
        }
    }
}
