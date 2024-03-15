package scripts;

import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;

import java.awt.*;

public class PMMLunaSettings {
    public static Color PirateGlowColor(){
        Color pirateglowcolor = new Color(255,255,255,255);
        if (Global.getSettings().getModManager().isModEnabled("lunalib"))
        {
            pirateglowcolor = LunaSettings.getColor("PirateMiniMegaMod","pmm_piratemodsglow");
        }
        return pirateglowcolor;
    }
}
