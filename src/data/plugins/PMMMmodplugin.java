package data.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import data.hullmods.ShardSpawner;
import data.listeners.PirateFleetInflationListener;
import org.apache.log4j.Logger;
import scripts.PMMCrossmodScript;
import scripts.PMMLunaSettings;

import java.util.Iterator;

public class PMMMmodplugin extends BaseModPlugin {

    public String TRIQUETRA = "pmm_fury_omega";
    public String AEON = "pmm_shrike_omega";
    public String SATUS = "pmm_satus_shard";
    public String PERCEPT = "pmm_tempest_omega";

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
        log.info("Welcome to PMMM! Im MiniDeth3 and im in your logs now...");
    }

    /**
     * Adds Pirate Mods to all pirate ships.
     */
    @Override
    public void onGameLoad(boolean WasEnabledBefore){
        for (ShipHullSpecAPI hullSpec : Global.getSettings().getAllShipHullSpecs()) {
            if (hullSpec.getManufacturer().contains("Pirate") && !hullSpec.isBuiltInMod("compmods")) {
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
            FactionAPI omegafac = Global.getSector().getFaction(Factions.OMEGA);
                if (omega){
                    omegafac.addKnownShip(TRIQUETRA, false);
                    omegafac.addKnownShip(AEON, false);
                    omegafac.addKnownShip(SATUS, false);
                    omegafac.addKnownShip(PERCEPT, false);
                }
                if (!omega) {
                    omegafac.removeKnownShip(TRIQUETRA);
                    omegafac.removeKnownShip(AEON);
                    omegafac.removeKnownShip(SATUS);
                    omegafac.removeKnownShip(PERCEPT);
                }
            }
        }
