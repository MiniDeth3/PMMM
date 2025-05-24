package data.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.hullmods.ShardSpawner;
import data.hullmods.PMM_CompMods;

public class PMMSettingsScript {

    //Settings
    public static boolean IsMastRec = false;
    public static boolean GLOW = true;
    public static boolean OMEGA = true;

    //Ships
    public static String MASTER = "pmm_derelict_master";
    public static String TRIQUETRA = "pmm_fury_omega";
    public static String AEON = "pmm_shrike_omega";
    public static String SATUS = "pmm_satus_shard";
    public static String PERCEPT = "pmm_tempest_omega";

    //Tags
    public static String AUTOREC = "auto_rec";

    public static void initMasterRec(){
        Boolean masrec = PMMLunaSettings.MasterRecover();
        if(masrec){
            IsMastRec = true;
            Global.getSettings().getHullSpec(MASTER).addTag(AUTOREC);
        } else {
            IsMastRec = false;
            Global.getSettings().getHullSpec(MASTER).getTags().remove(AUTOREC);
        }
    }

    public static void initGlow(){
        Boolean glow = PMMLunaSettings.PirateGlowToggle();

        PMM_CompMods.BALLISTIC_GLOW = PMMLunaSettings.PirateGlowColorBallistic();
        PMM_CompMods.ENERGY_GLOW = PMMLunaSettings.PirateGlowColorEnergy();
        if(glow){
            GLOW = true;
            PMM_CompMods.GLOW = true;
        } else {
            GLOW = false;
            PMM_CompMods.GLOW = false;
        }
    }

    public static void initOmega(){
        Boolean omega = PMMLunaSettings.OmegaToggle();
        FactionAPI omegafac = Global.getSector().getFaction(Factions.OMEGA);

        if (omega){
            OMEGA = true;
            com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardTypeVariants fighters = com.fs.starfarer.api.impl.hullmods.ShardSpawner.variantData.get(ShipAPI.HullSize.FIGHTER);
            fighters.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).add("pmm_legacy_attack_wing", 5f);
            fighters.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).add("pmm_legacy_missile_wing", 1f);

            fighters.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.MISSILE).add("pmm_legacy_missile_wing", 5f);
            fighters.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_ARMOR).add("pmm_legacy_attack_wing", 5f);
            fighters.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_SHIELD).add("pmm_legacy_shieldbreaker_wing", 5f);
            fighters.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.POINT_DEFENSE).add("pmm_legacy_shock_wing", 5f);

            fighters.get(ShardSpawner.ShardType.GENERAL).add("aspect_attack_wing", 10f);
            fighters.get(ShardSpawner.ShardType.GENERAL).add("aspect_missile_wing", 1f);

            fighters.get(ShardSpawner.ShardType.MISSILE).add("aspect_missile_wing", 10f);

            fighters.get(ShardSpawner.ShardType.ANTI_ARMOR).add("aspect_attack_wing", 10f);

            fighters.get(ShardSpawner.ShardType.ANTI_SHIELD).add("aspect_shieldbreaker_wing", 10f);

            fighters.get(ShardSpawner.ShardType.POINT_DEFENSE).add("aspect_shock_wing", 10f);

            com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardTypeVariants small = com.fs.starfarer.api.impl.hullmods.ShardSpawner.variantData.get(ShipAPI.HullSize.FRIGATE);
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).add("pmm_satus_shard_Attack", 7f);
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_ARMOR).add("pmm_satus_shard_Armorbreaker", 7f);
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_SHIELD).add("pmm_satus_shard_Shieldbreaker", 7f);
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.POINT_DEFENSE).add("pmm_satus_shard_Defense", 7f);
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.MISSILE).add("pmm_satus_shard_Missile", 3f);
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).add("pmm_tempest_omega_Attack", 3f);
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_ARMOR).add("pmm_tempest_omega_Armorbreaker", 3f);
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_SHIELD).add("pmm_tempest_omega_Shieldbreaker", 3f);
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.POINT_DEFENSE).add("pmm_tempest_omega_Defense", 3f);
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.MISSILE).add("pmm_tempest_omega_Missile", 3f);

            small.get(ShardSpawner.ShardType.GENERAL).add("shard_left_Attack", 10f);
            small.get(ShardSpawner.ShardType.GENERAL).add("shard_left_Attack2", 10f);
            small.get(ShardSpawner.ShardType.GENERAL).add("shard_right_Attack", 10f);
            small.get(ShardSpawner.ShardType.GENERAL).add("aspect_attack_wing", 10f);
            small.get(ShardSpawner.ShardType.GENERAL).add("aspect_missile_wing", 1f);

            small.get(ShardSpawner.ShardType.ANTI_ARMOR).add("shard_left_Armorbreaker", 10f);

            small.get(ShardSpawner.ShardType.ANTI_SHIELD).add("shard_left_Shieldbreaker", 10f);
            small.get(ShardSpawner.ShardType.ANTI_SHIELD).add("shard_right_Shieldbreaker", 10f);
            //small.get(ShardType.ANTI_SHIELD).add("aspect_shieldbreaker_wing", 10f);

            small.get(ShardSpawner.ShardType.POINT_DEFENSE).add("shard_left_Defense", 10f);
            small.get(ShardSpawner.ShardType.POINT_DEFENSE).add("shard_right_Shock", 10f);
            //small.get(ShardType.POINT_DEFENSE).add("aspect_shock_wing", 10f);

            small.get(ShardSpawner.ShardType.MISSILE).add("shard_left_Missile", 10f);
            small.get(ShardSpawner.ShardType.MISSILE).add("shard_right_Missile", 10f);
            //small.get(ShardType.MISSILE).add("aspect_missile_wing", 10f);

            com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardTypeVariants medium = com.fs.starfarer.api.impl.hullmods.ShardSpawner.variantData.get(ShipAPI.HullSize.DESTROYER);
            medium.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).add("pmm_shrike_omega_Attack");
            medium.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_ARMOR).add("pmm_shrike_omega_Armorbreaker");
            medium.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_SHIELD).add("pmm_shrike_omega_Shieldbreaker");
            medium.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.POINT_DEFENSE).add("pmm_shrike_omega_Defense");
            medium.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.MISSILE).add("pmm_shrike_omega_Missile");

            medium.get(ShardSpawner.ShardType.GENERAL).add("facet_Attack");
            medium.get(ShardSpawner.ShardType.GENERAL).add("facet_Attack2");

            medium.get(ShardSpawner.ShardType.ANTI_ARMOR).add("facet_Armorbreaker");

            medium.get(ShardSpawner.ShardType.ANTI_SHIELD).add("facet_Shieldbreaker");

            medium.get(ShardSpawner.ShardType.POINT_DEFENSE).add("facet_Defense");

            medium.get(ShardSpawner.ShardType.MISSILE).add("facet_Missile");

            com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardTypeVariants large = com.fs.starfarer.api.impl.hullmods.ShardSpawner.variantData.get(ShipAPI.HullSize.CRUISER);
            large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).add("pmm_fury_omega_Attack");
            large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).add("pmm_fury_omega_Attack2");
            large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_ARMOR).add("pmm_fury_omega_Armorbreaker");
            large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_SHIELD).add("pmm_fury_omega_Shieldbreaker");
            large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.POINT_DEFENSE).add("pmm_fury_omega_Defense");
            large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.MISSILE).add("pmm_fury_omega_Missile");

            large.get(ShardSpawner.ShardType.GENERAL).add("tesseract_Attack");
            large.get(ShardSpawner.ShardType.GENERAL).add("tesseract_Attack2");
            large.get(ShardSpawner.ShardType.GENERAL).add("tesseract_Strike");
            large.get(ShardSpawner.ShardType.GENERAL).add("tesseract_Disruptor");

            large.get(ShardSpawner.ShardType.ANTI_ARMOR).add("tesseract_Disruptor");
            large.get(ShardSpawner.ShardType.ANTI_ARMOR).add("tesseract_Strike");

            large.get(ShardSpawner.ShardType.ANTI_SHIELD).add("tesseract_Shieldbreaker");

            large.get(ShardSpawner.ShardType.POINT_DEFENSE).add("tesseract_Defense");

            large.get(ShardSpawner.ShardType.MISSILE).add("tesseract_Strike");

            omegafac.addKnownShip(TRIQUETRA, false);
            omegafac.addKnownShip(AEON, false);
            omegafac.addKnownShip(SATUS, false);
            omegafac.addKnownShip(PERCEPT, false);
        } else {
            OMEGA = false;
            com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardTypeVariants fighters = com.fs.starfarer.api.impl.hullmods.ShardSpawner.variantData.get(ShipAPI.HullSize.FIGHTER);
            fighters.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).remove("pmm_legacy_attack_wing");
            fighters.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).remove("pmm_legacy_missile_wing");

            fighters.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.MISSILE).remove("pmm_legacy_missile_wing");
            fighters.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_ARMOR).remove("pmm_legacy_attack_wing");
            fighters.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_SHIELD).remove("pmm_legacy_shieldbreaker_wing");
            fighters.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.POINT_DEFENSE).remove("pmm_legacy_shock_wing");

            com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardTypeVariants small = com.fs.starfarer.api.impl.hullmods.ShardSpawner.variantData.get(ShipAPI.HullSize.FRIGATE);
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).remove("pmm_satus_shard_Attack");
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_ARMOR).remove("pmm_satus_shard_Armorbreaker");
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_SHIELD).remove("pmm_satus_shard_Shieldbreaker");
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.POINT_DEFENSE).remove("pmm_satus_shard_Defense");
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.MISSILE).remove("pmm_satus_shard_Missile");
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).remove("pmm_tempest_omega_Attack");
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_ARMOR).remove("pmm_tempest_omega_Armorbreaker");
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_SHIELD).remove("pmm_tempest_omega_Shieldbreaker");
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.POINT_DEFENSE).remove("pmm_tempest_omega_Defense");
            small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.MISSILE).remove("pmm_tempest_omega_Missile");

            com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardTypeVariants medium = com.fs.starfarer.api.impl.hullmods.ShardSpawner.variantData.get(ShipAPI.HullSize.DESTROYER);
            medium.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).remove("pmm_shrike_omega_Attack");
            medium.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_ARMOR).remove("pmm_shrike_omega_Armorbreaker");
            medium.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_SHIELD).remove("pmm_shrike_omega_Shieldbreaker");
            medium.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.POINT_DEFENSE).remove("pmm_shrike_omega_Defense");
            medium.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.MISSILE).remove("pmm_shrike_omega_Missile");

            com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardTypeVariants large = com.fs.starfarer.api.impl.hullmods.ShardSpawner.variantData.get(ShipAPI.HullSize.CRUISER);
            large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).remove("pmm_fury_omega_Attack");
            large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).remove("pmm_fury_omega_Attack2");
            large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_ARMOR).remove("pmm_fury_omega_Armorbreaker");
            large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_SHIELD).remove("pmm_fury_omega_Shieldbreaker");
            large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.POINT_DEFENSE).remove("pmm_fury_omega_Defense");
            large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.MISSILE).remove("pmm_fury_omega_Missile");

            omegafac.removeKnownShip(TRIQUETRA);
            omegafac.removeKnownShip(AEON);
            omegafac.removeKnownShip(SATUS);
            omegafac.removeKnownShip(PERCEPT);
        }
    }
}
