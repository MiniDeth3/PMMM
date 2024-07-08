package data.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import data.listeners.PirateFleetInflationListener;
import org.apache.log4j.Logger;
import org.dark.shaders.util.ShaderLib;
import org.dark.shaders.util.TextureData;
import scripts.PMMCrossmodScript;
import scripts.PMMLunaSettings;
import scripts.PMMSettingsScript;

public class PMMModPlugin extends BaseModPlugin {
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
            //PMM toggle settings
            Boolean omega = PMMLunaSettings.OmegaToggle();
            Boolean mastrec = PMMLunaSettings.MasterRecover();
            Boolean glow = PMMLunaSettings.PirateGlowToggle();

                PMMSettingsScript.initOmega();
                if (omega)
                    log.info("Enabled PMM omega");
                else
                    log.info("Disabled PMM omega");

                PMMSettingsScript.initMasterRec();
                if (mastrec)
                    log.info("PMM Master can be recovered");

                PMMSettingsScript.initGlow();
                if (!glow)
                    log.info("PMM Glow disabled");
        }
        }
