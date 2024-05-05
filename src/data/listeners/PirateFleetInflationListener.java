package data.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FleetInflater;
import com.fs.starfarer.api.campaign.listeners.FleetInflationListener;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import static com.fs.starfarer.api.campaign.AICoreOfficerPlugin.AUTOMATED_POINTS_MULT;

public class PirateFleetInflationListener implements FleetInflationListener {
    public static final String COMPMODS = "pmm_compmods"; //target hullmod ID. Oddly this needs to be defined here if using non-vanilla hullmod.
    public static final float COMPMODS_S_CHANCE = 0.35f; //chance for hullmod to spawn smod
    public static final float GAMMA_CHANCE = 0.50f; //chance for auto ship to spawn with core
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    @Override
    public void reportFleetInflated(CampaignFleetAPI fleet, FleetInflater inflater) {

        if(fleet.isPlayerFleet()) return; //if player fleet no effect
        if(fleet.getFleetData().getCommander().equals(Global.getSector().getPlayerPerson())) return; //if player in fleet no effect
        if(!fleet.getFaction().getId().contains("pirate")) return; //change the quote to the ID of target faction

        for (FleetMemberAPI fleetMemberAPI : fleet.getMembersWithFightersCopy()) {
            boolean pirate = fleetMemberAPI.getHullSpec().getManufacturer().contains("Pirate"); //change the quote to your target ships manufacturer or tech
            boolean smodchance = (Math.random() < COMPMODS_S_CHANCE); //sets up dice roll for smod chance
            boolean aicorechance = (Math.random() < GAMMA_CHANCE); //sets up dice roll for ai core chance

            if(fleetMemberAPI.isFighterWing())continue; //if fighter no effect
            if(fleetMemberAPI.isStation())continue; //if station no effect
            if(fleetMemberAPI.isMothballed())continue; //if mothballed no effect
            if(!pirate)continue; //if not specified tech then no effect

            if (fleetMemberAPI.getVariant().hasHullMod("automated")){
                if (aicorechance){
                    fleetMemberAPI.setCaptain(createPirateGammaCoreOfficer());
                }
            }
            if(!smodchance)continue; //if dice roll fails then no effect

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
        person.setRankId(Ranks.POST_GANGSTER);
        person.setPostId(null);

        person.getStats().setSkipRefresh(false);

        return person;
    }

}