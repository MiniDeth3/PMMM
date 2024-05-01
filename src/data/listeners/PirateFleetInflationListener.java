package data.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FleetInflater;
import com.fs.starfarer.api.campaign.listeners.FleetInflationListener;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.AICoreOfficerPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.AICores;
import org.lazywizard.lazylib.MathUtils;

import java.util.ArrayList;

import static com.fs.starfarer.api.campaign.AICoreOfficerPlugin.AUTOMATED_POINTS_MULT;
import static com.fs.starfarer.api.campaign.AICoreOfficerPlugin.AUTOMATED_POINTS_VALUE;

public class PirateFleetInflationListener implements FleetInflationListener {
    public static final String COMPMODS = "pmm_compmods"; //target hullmod ID
    public static final float CHANCE = 0.30f; //chance for hullmod to spawn smod
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

            if (fleetMemberAPI.getVariant().hasHullMod("automated")){
                float prob = MathUtils.getRandomNumberInRange(1,100);
                if (prob > 75){
                    fleetMemberAPI.setCaptain(createPirateGammaCoreOfficer());
                }
            }
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

    public static PersonAPI createPirateGammaCoreOfficer(){

        PersonAPI person = Global.getFactory().createPerson();
        person.setFaction("pirates");
        person.setAICoreId("gamma_core");

        person.getStats().setSkipRefresh(true);

        person.setName(new FullName("Gamma", "", FullName.Gender.ANY));

        person.setPortraitSprite("graphics/portraits/portrait_ai1b.png");
        person.getStats().setLevel(3);
        person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
        person.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
        //person.getStats().setSkillLevel(Skills.RELIABILITY_ENGINEERING, 2);
        person.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);

        person.getMemoryWithoutUpdate().set(AUTOMATED_POINTS_MULT, 2);

        person.setPersonality(Personalities.RECKLESS);
        person.setRankId(Ranks.SPACE_CAPTAIN);
        person.setPostId(null);

        person.getStats().setSkipRefresh(false);

        return person;
    }

}