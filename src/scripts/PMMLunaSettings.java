package scripts;

import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;

import java.awt.*;

public class PMMLunaSettings {
    public static Boolean PirateGlowToggle(){
        Boolean glow = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib"))
        {
            glow = LunaSettings.getBoolean("PirateMiniMegaMod","pmm_piratemodsglowtoggle");
        }
        return glow;
    }
    public static Color PirateGlowColorBallistic(){
        Color pirateglowcolorballistic = new Color(255,255,255,255);
        if (Global.getSettings().getModManager().isModEnabled("lunalib"))
        {
            pirateglowcolorballistic = LunaSettings.getColor("PirateMiniMegaMod","pmm_piratemodsglowballistic");
        }
        return pirateglowcolorballistic;
    }
    public static Color PirateGlowColorEnergy(){
        Color pirateglowcolorenergy = new Color(255,255,255,255);
        if (Global.getSettings().getModManager().isModEnabled("lunalib"))
        {
            pirateglowcolorenergy = LunaSettings.getColor("PirateMiniMegaMod","pmm_piratemodsglowenergy");
        }
        return pirateglowcolorenergy;
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
