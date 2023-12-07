package data.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import data.listeners.PirateFleetInflationListener;
import org.apache.log4j.Logger;

public class PMMMmodplugin extends BaseModPlugin {
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
        }
    }
