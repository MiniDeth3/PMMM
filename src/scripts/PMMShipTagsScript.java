package scripts;

import com.fs.starfarer.api.Global;

public class PMMShipTagsScript {

    //Settings
    public static boolean IsMastRec = false;
    //Ships
    public static String MASTER = "pmm_derelict_master";
    //Tags
    public static String AUTOREC = "auto_rec";

    public static void initMasterRec(){
        IsMastRec = true;
        Global.getSettings().getHullSpec(MASTER).addTag(AUTOREC);
    }
}
