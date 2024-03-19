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
}
