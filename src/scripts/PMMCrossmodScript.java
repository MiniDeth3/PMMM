package scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.loading.specs.FactionDoctrine;

public class PMMCrossmodScript {

    //Mods
    public static boolean IsVRIEnabled = false;
    public static boolean IsScrapyardEnabled = false;
    public static boolean IsIndiesEnabled = false;

    //Ships
    public static String PIRATE_GAUNTLET = "gauntlet_pirates";
    public static String PIRATE_SUPERCHAMP = "super_champion_pirates";
    public static String PIRATE_CAVERN = "cavern_pirates";

    //Tags
    public static String SHIP_TAG_PIRATE_BP = "pirate_bp";
    public static String SHIP_TAG_PIRATE = "pirates";
    public static String SHIP_TAG_RARE_BP = "rare_bp";

    public static void initVRICrossmod(){
        IsVRIEnabled = true;
        Global.getSettings().getHullSpec(PIRATE_GAUNTLET).addTag(SHIP_TAG_PIRATE_BP);
        Global.getSector().getFaction(Factions.PIRATES).addKnownShip(PIRATE_GAUNTLET, false);

    }
    public static void initIndiesCrossmod(){
        IsIndiesEnabled = true;
        Global.getSettings().getHullSpec(PIRATE_SUPERCHAMP).addTag(SHIP_TAG_PIRATE_BP);
        Global.getSector().getFaction(Factions.PIRATES).addKnownShip(PIRATE_SUPERCHAMP, false);

    }
    public static void initScrapyardCrossmod(){
        IsScrapyardEnabled = true;
        Global.getSettings().getHullSpec(PIRATE_CAVERN).addTag(SHIP_TAG_PIRATE_BP);
        Global.getSettings().getHullSpec(PIRATE_CAVERN).addTag(SHIP_TAG_RARE_BP);
        Global.getSector().getFaction(Factions.PIRATES).addKnownShip(PIRATE_CAVERN, false);

    }
}
