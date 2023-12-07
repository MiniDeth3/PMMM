package data.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FleetInflater;
import com.fs.starfarer.api.campaign.listeners.FleetInflationListener;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import java.util.ArrayList;

public class PirateFleetInflationListener implements FleetInflationListener {
    public static final String COMPMODS = "compmods"; //target hullmod ID
    public static final float CHANCE = 0.15f; //chance for hullmod to spawn smod
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    @Override
    public void reportFleetInflated(CampaignFleetAPI fleet, FleetInflater inflater) {

        if(fleet.isPlayerFleet()) return;
        if(fleet.getFleetData().getCommander().equals(Global.getSector().getPlayerPerson())) return;
        if(!fleet.getFaction().getId().contains("pirate")) return; //change the quote to the ID of target faction

        for (FleetMemberAPI fleetMemberAPI : fleet.getMembersWithFightersCopy()) {
            boolean pirate = fleetMemberAPI.getHullSpec().getManufacturer().contains("Pirate"); //change the quote to your target ships manufacturer/tech
            boolean chance = (Math.random() < CHANCE);

            if(fleetMemberAPI.isFighterWing())continue;
            if(fleetMemberAPI.isStation())continue;
            if(fleetMemberAPI.isMothballed())continue;
            if(!pirate)continue;
            if(!chance)continue;

            int SModsAmount = (determineAmountofSmods(fleet.getFaction()));
            fleetMemberAPI.getStats().getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlat("Modifications", (SModsAmount));
            fleetMemberAPI.getVariant().getSModdedBuiltIns().add(COMPMODS);
            fleetMemberAPI.updateStats();
        }
    }

    public int determineAmountofSmods(FactionAPI factionAPI) {
        return getRandomNumber(0,1);
    }

}