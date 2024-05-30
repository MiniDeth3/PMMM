package scripts;

import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;

import java.awt.*;

public class PMMLunaSettings {
    public static Color PirateGlowColorBallistic(){
        Color pirateglowcolor = new Color(255,255,255,255);
        if (Global.getSettings().getModManager().isModEnabled("lunalib"))
        {
            pirateglowcolor = LunaSettings.getColor("PirateMiniMegaMod","pmm_piratemodsglowballistic");
        }
        return pirateglowcolor;
    }
    public static Color PirateGlowColorEnergy(){
        Color pirateglowcolor = new Color(255,255,255,255);
        if (Global.getSettings().getModManager().isModEnabled("lunalib"))
        {
            pirateglowcolor = LunaSettings.getColor("PirateMiniMegaMod","pmm_piratemodsglowenergy");
        }
        return pirateglowcolor;
    }
    public static Boolean OmegaToggle(){
        Boolean omega = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib"))
        {
             omega = LunaSettings.getBoolean("PirateMiniMegaMod","pmm_omegatoggle");
        }
        return omega;
    }
    public static Boolean MasterRecover(){
        Boolean mastrec = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib"))
        {
            mastrec = LunaSettings.getBoolean("PirateMiniMegaMod","pmm_masterrecover");
        }
        return mastrec;
    }
}
