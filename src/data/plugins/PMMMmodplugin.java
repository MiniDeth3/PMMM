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
import data.listeners.PirateFleetInflationListener;
import org.apache.log4j.Logger;
import scripts.PMMLunaSettings;

import java.util.Iterator;

public class PMMMmodplugin extends BaseModPlugin {

    public String TRIQUETRA = "fury_omega";
    public String AEON = "shrike_omega";
    public String STATUS = "status_shard";
    public String PERCEPT = "tempest_omega";

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
                hullSpec.addBuiltInMod("compmods");
                log.info("Added Pirate Modifications to " + hullSpec.getHullNameWithDashClass());
            }
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
                        omegafac.addKnownShip(STATUS, false);
                        omegafac.addKnownShip(PERCEPT, false);
                    }
                if (!omega) {
                    omegafac.removeKnownShip(TRIQUETRA);
                    omegafac.removeKnownShip(AEON);
                    omegafac.removeKnownShip(STATUS);
                    omegafac.removeKnownShip(PERCEPT);
                }
            }
        }
