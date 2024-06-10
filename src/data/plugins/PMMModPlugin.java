package data.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import data.listeners.PirateFleetInflationListener;
import org.apache.log4j.Logger;
import org.dark.shaders.util.ShaderLib;
import org.dark.shaders.util.TextureData;
import scripts.PMMCrossmodScript;
import scripts.PMMLunaSettings;
import scripts.PMMShipTagsScript;

public class PMMModPlugin extends BaseModPlugin {

    public String TRIQUETRA = "pmm_fury_omega";
    public String AEON = "pmm_shrike_omega";
    public String SATUS = "pmm_satus_shard";
    public String PERCEPT = "pmm_tempest_omega";
    public static boolean HAS_GRAPHICSLIB = false;
    public Logger log = Logger.getLogger(this.getClass());
    public void setListenersIfNeeded() {
        ListenerManagerAPI l = Global.getSector().getListenerManager();

        if (!l.hasListenerOfClass(PirateFleetInflationListener.class)) {
            l.addListener(new PirateFleetInflationListener(), true);
            log.info("Adding Pirate Listener");
        }
    }

    @Override
    public void onApplicationLoad() throws Exception {
        boolean hasGraphicsLib = Global.getSettings ().getModManager ().isModEnabled ( "shaderLib" );
        if ( hasGraphicsLib ) {
            HAS_GRAPHICSLIB = true;
            ShaderLib.init();
            // LightData.readLightDataCSV((String) "data/config/example_lights_data.csv");
            TextureData.readTextureDataCSV((String) "data/config/pmm_texture_data.csv");
            log.info("PMM shaders active");
        }
        log.info("Welcome to PMMM! Im MiniDeth3 and im in your logs now...");

    }

    /**
     * Adds Pirate Mods to all pirate ships.
     */
    @Override
    public void onGameLoad(boolean WasEnabledBefore){
        for (ShipHullSpecAPI hullSpec : Global.getSettings().getAllShipHullSpecs()) {
            if (hullSpec.getManufacturer().contains("Pirate") && !hullSpec.isBuiltInMod("pmm_compmods")) {
                hullSpec.addBuiltInMod("pmm_compmods");
                log.info("Added Pirate Modifications to " + hullSpec.getHullNameWithDashClass());
            }
        }

        if (Global.getSettings().getModManager().isModEnabled("TouchOfVanilla_vri")){
            PMMCrossmodScript.initVRICrossmod();
            log.info("So you're a teal enjoyer!");
        }
        if (Global.getSettings().getModManager().isModEnabled("aerialcombatsuit")){
            PMMCrossmodScript.initIndiesCrossmod();
            log.info("Cool backwards ships!");
        }
        if (Global.getSettings().getModManager().isModEnabled("Scrapyard")){
            PMMCrossmodScript.initScrapyardCrossmod();
            log.info("\"Where spacers sees scrap metal, a salvager sees a fleet\"");
        }

        setListenersIfNeeded();
        ListenerManagerAPI l = Global.getSector().getListenerManager();
            if (!l.hasListenerOfClass(PirateFleetInflationListener .class)) {
                l.addListener(new PirateFleetInflationListener());
            }
            updateLunaSettings();
        }

        public void updateLunaSettings() {
            //Omega toggle settings
            Boolean omega = PMMLunaSettings.OmegaToggle();
            Boolean mastrec = PMMLunaSettings.OmegaToggle();

            FactionAPI omegafac = Global.getSector().getFaction(Factions.OMEGA);
                if (omega){
                    com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardTypeVariants fighters = com.fs.starfarer.api.impl.hullmods.ShardSpawner.variantData.get(ShipAPI.HullSize.FIGHTER);

                    com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardTypeVariants small = com.fs.starfarer.api.impl.hullmods.ShardSpawner.variantData.get(ShipAPI.HullSize.FRIGATE);
                    small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).add("pmm_satus_shard_Attack", 10f);
                    small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_ARMOR).add("pmm_satus_shard_Armorbreaker", 10f);
                    small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_SHIELD).add("pmm_satus_shard_Shieldbreaker", 10f);
                    small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.POINT_DEFENSE).add("pmm_satus_shard_Defense", 10f);
                    small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.MISSILE).add("pmm_satus_shard_Missile", 4f);
                    small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).add("pmm_tempest_omega_Attack", 4f);
                    small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_ARMOR).add("pmm_tempest_omega_Armorbreaker", 4f);
                    small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_SHIELD).add("pmm_tempest_omega_Shieldbreaker", 4f);
                    small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.POINT_DEFENSE).add("pmm_tempest_omega_Defense", 4f);
                    small.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.MISSILE).add("pmm_tempest_omega_Missile", 4f);

                    com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardTypeVariants medium = com.fs.starfarer.api.impl.hullmods.ShardSpawner.variantData.get(ShipAPI.HullSize.DESTROYER);
                    medium.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).add("pmm_shrike_omega_Attack", 10f);
                    medium.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_ARMOR).add("pmm_shrike_omega_Armorbreaker", 10f);
                    medium.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_SHIELD).add("pmm_shrike_omega_Shieldbreaker", 10f);
                    medium.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.POINT_DEFENSE).add("pmm_shrike_omega_Defense", 10f);
                    medium.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.MISSILE).add("pmm_shrike_omega_Missile", 4f);

                    com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardTypeVariants large = com.fs.starfarer.api.impl.hullmods.ShardSpawner.variantData.get(ShipAPI.HullSize.CRUISER);
                    large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).add("pmm_fury_omega_Attack", 10f);
                    large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.GENERAL).add("pmm_fury_omega_Attack2", 10f);
                    large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_ARMOR).add("pmm_fury_omega_Armorbreaker", 10f);
                    large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.ANTI_SHIELD).add("pmm_fury_omega_Shieldbreaker", 10f);
                    large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.POINT_DEFENSE).add("pmm_fury_omega_Defense", 10f);
                    large.get(com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardType.MISSILE).add("pmm_fury_omega_Missile", 4f);

                    omegafac.addKnownShip(TRIQUETRA, false);
                    omegafac.addKnownShip(AEON, false);
                    omegafac.addKnownShip(SATUS, false);
                    omegafac.addKnownShip(PERCEPT, false);

                    log.info("Enabled PMM omega");
                }
                if (!omega) {
                    com.fs.starfarer.api.impl.hullmods.ShardSpawner.ShardTypeVariants fighters = com.fs.starfarer.api.impl.hullmods.ShardSpawner.variantData.get(ShipAPI.HullSize.FIGHTER);

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

                    log.info("Disabled PMM omega");
                }
            if (mastrec){
                PMMShipTagsScript.initMasterRec();
                log.info("PMM Master can be recovered");
                }
            }
        }
