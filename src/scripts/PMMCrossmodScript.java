package scripts;

import com.fs.starfarer.api.Global;

public class PMMCrossmodScript {

    public static boolean isVRIEnabled = false;
    public static String PIRATE_GAUNTLET = "gauntlet_pirates";
    public static String SHIP_TAG_PIRATE_BP = "pirate_bp";

    public static void initVRICrossmod(){
        isVRIEnabled = true;
        Global.getSettings().getHullSpec(PIRATE_GAUNTLET).addTag(SHIP_TAG_PIRATE_BP);

    }
}
