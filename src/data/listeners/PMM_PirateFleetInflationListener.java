package data.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
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

public class PMM_PirateFleetInflationListener implements FleetInflationListener {

    public static final String COMPMODS = "pmm_compmods"; // Target hullmod ID
    public static final float COMPMODS_S_CHANCE = 0.50f; // Chance for hullmod to spawn smod
    public static final float GAMMA_CHANCE = 0.50f; // Chance for auto ship to spawn with core

    // Random number generator
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @Override
    public void reportFleetInflated(CampaignFleetAPI fleet, FleetInflater inflater) {

        // Ignore if player fleet or player in fleet
        if (fleet.isPlayerFleet() || fleet.getFleetData().getCommander().equals(Global.getSector().getPlayerPerson())) {
            return;
        }

        // Only apply to pirate factions
        if (!fleet.getFaction().getId().contains("pirate")) {
            return;
        }

        // Process each fleet member
        for (FleetMemberAPI fleetMemberAPI : fleet.getMembersWithFightersCopy()) {
            boolean pirate = fleetMemberAPI.getHullSpec().getManufacturer().contains("Pirate");
            boolean smodChance = (Math.random() < COMPMODS_S_CHANCE); // Dice roll for smod chance
            boolean aiCoreChance = (Math.random() < GAMMA_CHANCE); // Dice roll for AI core chance

            // Skip if fighter, station, mothballed, or not pirate tech
            if (fleetMemberAPI.isFighterWing() || fleetMemberAPI.isStation() || fleetMemberAPI.isMothballed() || !pirate) {
                continue;
            }

            // Assign AI core to automated ships
            if (fleetMemberAPI.getVariant().hasHullMod("automated") && aiCoreChance) {
                fleetMemberAPI.setCaptain(createPirateGammaCoreOfficer());
            }

            // If smod chance succeeds, add hullmod
            if (smodChance) {
                int smodsAmount = determineAmountOfSmods();
                fleetMemberAPI.getStats().getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlat("Modifications", smodsAmount);
                fleetMemberAPI.getVariant().getSModdedBuiltIns().add(COMPMODS);
                fleetMemberAPI.updateStats();
            }
        }
    }

    // Determine the amount of smods (no need for factionAPI now)
    public int determineAmountOfSmods() {
        return getRandomNumber(0, 1);
    }

    // Create a pirate gamma core officer
    public static PersonAPI createPirateGammaCoreOfficer() {
        PersonAPI person = Global.getFactory().createPerson();
        person.setFaction("pirates");
        person.setAICoreId("gamma_core");
        person.setName(new FullName("Gamma", "", FullName.Gender.ANY));
        person.setPortraitSprite("graphics/portraits/portrait_ai1b.png");

        person.getStats().setSkipRefresh(true);
        person.getStats().setLevel(3);
        person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
        person.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
        person.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);

        person.getMemoryWithoutUpdate().set(AUTOMATED_POINTS_MULT, 2);

        person.setPersonality(Personalities.RECKLESS);
        person.setRankId(Ranks.POST_GANGSTER);
        person.setPostId(null);
        person.getStats().setSkipRefresh(false);

        return person;
    }
}
