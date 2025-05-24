package data.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import data.listeners.PMM_PirateFleetInflationListener;
import org.apache.log4j.Logger;
import org.dark.shaders.util.ShaderLib;
import org.dark.shaders.util.TextureData;

public class PMMModPlugin extends BaseModPlugin {

    private static final Logger log = Global.getLogger(PMMModPlugin.class);
    private static boolean hasGraphicsLib = false;

    @Override
    public void onApplicationLoad() {
        // Handle shaders if the graphics library is enabled
        hasGraphicsLib = Global.getSettings().getModManager().isModEnabled("shaderLib");

        if (hasGraphicsLib) {
            ShaderLib.init();

            if (ShaderLib.areShadersAllowed() && ShaderLib.areBuffersAllowed()) {
                TextureData.readTextureDataCSV("data/config/pmm_texture_data.csv");
                log.info("PMM shaders active");
            }
        }

        // Log the plugin initialization
        log.info("Welcome to PMMM! I'm MiniDeth3, and I'm in your logs now...");
    }

    @Override
    public void onGameLoad(boolean wasEnabledBefore) {
        addPirateMods();
        initializeCrossmods();
        setListenersIfNeeded();
        updateLunaSettings();
    }

    private void setListenersIfNeeded() {
        ListenerManagerAPI listenerManager = Global.getSector().getListenerManager();

        if (!listenerManager.hasListenerOfClass(PMM_PirateFleetInflationListener.class)) {
            listenerManager.addListener(new PMM_PirateFleetInflationListener(), true);
            log.info("Adding Pirate Listener");
        }
    }

    private void addPirateMods() {
        for (ShipHullSpecAPI hullSpec : Global.getSettings().getAllShipHullSpecs()) {
            if (hullSpec.getManufacturer().contains("Pirat")){// && !hullSpec.isBuiltInMod("pmm_compmods")) {
                hullSpec.addBuiltInMod("pmm_compmods");
                log.info("Added Pirate Modifications to " + hullSpec.getHullNameWithDashClass());
            }
        }
    }

    private void initializeCrossmods() {
        if (Global.getSettings().getModManager().isModEnabled("TouchOfVanilla_vri")) {
            PMMCrossmodScript.initVRICrossmod();
            log.info("So you're a teal enjoyer!");
        }

        if (Global.getSettings().getModManager().isModEnabled("aerialcombatsuit")) {
            PMMCrossmodScript.initIndiesCrossmod();
            log.info("Cool backwards ships!");
        }

        if (Global.getSettings().getModManager().isModEnabled("Scrapyard")) {
            PMMCrossmodScript.initScrapyardCrossmod();
            log.info("\"Where spacers see scrap metal, a salvager sees a fleet\"");
        }

        if (Global.getSettings().getModManager().isModEnabled("swp")) {
            PMMCrossmodScript.initSWPCrossmod();
            log.info("SWP is just the best pack out there");
        }
    }

    private void updateLunaSettings() {
        boolean omegaToggle = PMMLunaSettings.OmegaToggle();
        boolean masterRecover = PMMLunaSettings.MasterRecover();
        boolean pirateGlowToggle = PMMLunaSettings.PirateGlowToggle();

        PMMSettingsScript.initOmega();
        log.info(omegaToggle ? "Enabled PMM omega" : "Disabled PMM omega");

        PMMSettingsScript.initMasterRec();
        if (masterRecover) log.info("PMM Master can be recovered");

        PMMSettingsScript.initGlow();
        if (!pirateGlowToggle) log.info("PMM Glow disabled");
    }
}
