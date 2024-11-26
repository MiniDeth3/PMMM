package scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Factions;

public class PMMCrossmodScript {

    // Mods
    public static boolean IsVRIEnabled = false;
    public static boolean IsScrapyardEnabled = false;
    public static boolean IsIndiesEnabled = false;
    public static boolean IsSWPEnabled = false;

    // Ships
    public static String PIRATE_GAUNTLET = "pmm_gauntlet_p";
    public static String PIRATE_SUPERCHAMP = "pmm_super_champion_p";
    public static String PIRATE_CAVERN = "pmm_cavern_p";
    public static String PIRATE_BRAWLER_TT = "pmm_cavern_p";
    public static String PIRATE_STRIKER = "pmm_striker_p";

    // Tags
    public static String SHIP_TAG_PIRATE_BP = "pirate_bp";
    public static String SHIP_TAG_PIRATE = "pirates"; // Use addKnownShips instead
    public static String SHIP_TAG_RARE_BP = "rare_bp";

    public static void initVRICrossmod() {
        IsVRIEnabled = true;
        Global.getSettings().getHullSpec(PIRATE_GAUNTLET).addTag(SHIP_TAG_PIRATE_BP);
        Global.getSector().getFaction(Factions.PIRATES).addKnownShip(PIRATE_GAUNTLET, false);
    }

    public static void initIndiesCrossmod() {
        IsIndiesEnabled = true;
        Global.getSettings().getHullSpec(PIRATE_SUPERCHAMP).addTag(SHIP_TAG_PIRATE_BP);
        Global.getSector().getFaction(Factions.PIRATES).addKnownShip(PIRATE_SUPERCHAMP, false);
    }

    public static void initScrapyardCrossmod() {
        IsScrapyardEnabled = true;
        Global.getSettings().getHullSpec(PIRATE_CAVERN).addTag(SHIP_TAG_PIRATE);
        Global.getSettings().getHullSpec(PIRATE_CAVERN).addTag(SHIP_TAG_RARE_BP);
        Global.getSector().getFaction(Factions.PIRATES).addKnownShip(PIRATE_CAVERN, false);
    }

    public static void initSWPCrossmod() {
        IsSWPEnabled = true;
        Global.getSettings().getHullSpec(PIRATE_STRIKER).addTag(SHIP_TAG_PIRATE);
        Global.getSettings().getHullSpec(PIRATE_STRIKER).addTag(SHIP_TAG_PIRATE_BP);
        Global.getSector().getFaction(Factions.PIRATES).addKnownShip(PIRATE_STRIKER, false);
    }
}
