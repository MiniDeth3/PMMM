package scripts;

import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;

import java.awt.*;

public class PMMLunaSettings {
    public static Boolean PirateGlowToggle() {
        Boolean glow = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            glow = LunaSettings.getBoolean("PirateMiniMegaMod", "pmm_piratemodsglowtoggle");
        }
        return glow;
    }

    public static Color PirateGlowColorBallistic() {
        Color pirateGlowColorBallistic = new Color(255, 255, 255, 255);
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            pirateGlowColorBallistic = LunaSettings.getColor("PirateMiniMegaMod", "pmm_piratemodsglowballistic");
        }
        return pirateGlowColorBallistic;
    }

    public static Color PirateGlowColorEnergy() {
        Color pirateGlowColorEnergy = new Color(255, 255, 255, 255);
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            pirateGlowColorEnergy = LunaSettings.getColor("PirateMiniMegaMod", "pmm_piratemodsglowenergy");
        }
        return pirateGlowColorEnergy;
    }

    public static Boolean VanillaChangeToggle_Afflictor() {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            String toggleValue = LunaSettings.getString("PirateMiniMegaMod", "pmm_vanillatoggle_afflictor");
            return toggleValue != null && toggleValue.contains("PMM"); // Check if the string contains "PMM"
        }
        return true; // Default value if the mod isn't enabled
    }

    public static Boolean VanillaChangeToggle_Buffalo() {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            String toggleValue = LunaSettings.getString("PirateMiniMegaMod", "pmm_vanillatoggle_buffalo");
            return toggleValue != null && toggleValue.contains("PMM"); // Check if the string contains "PMM"
        }
        return true; // Default value if the mod isn't enabled
    }

    public static Boolean VanillaChangeToggle_Cerberus() {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            String toggleValue = LunaSettings.getString("PirateMiniMegaMod", "pmm_vanillatoggle_cerberus");
            return toggleValue != null && toggleValue.contains("PMM"); // Check if the string contains "PMM"
        }
        return true; // Default value if the mod isn't enabled
    }

    public static Boolean VanillaChangeToggle_Enforcer() {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            String toggleValue = LunaSettings.getString("PirateMiniMegaMod", "pmm_vanillatoggle_enforcer");
            return toggleValue != null && toggleValue.contains("PMM"); // Check if the string contains "PMM"
        }
        return true; // Default value if the mod isn't enabled
    }

    public static Boolean VanillaChangeToggle_Eradicator() {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            String toggleValue = LunaSettings.getString("PirateMiniMegaMod", "pmm_vanillatoggle_eradicator");
            return toggleValue != null && toggleValue.contains("PMM"); // Check if the string contains "PMM"
        }
        return true; // Default value if the mod isn't enabled
    }

    public static Boolean VanillaChangeToggle_Gremlin() {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            String toggleValue = LunaSettings.getString("PirateMiniMegaMod", "pmm_vanillatoggle_gremlin");
            return toggleValue != null && toggleValue.contains("PMM"); // Check if the string contains "PMM"
        }
        return true; // Default value if the mod isn't enabled
    }

    public static Boolean VanillaChangeToggle_Hound() {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            String toggleValue = LunaSettings.getString("PirateMiniMegaMod", "pmm_vanillatoggle_hound");
            return toggleValue != null && toggleValue.contains("PMM"); // Check if the string contains "PMM"
        }
        return true; // Default value if the mod isn't enabled
    }

    public static Boolean VanillaChangeToggle_Kite() {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            String toggleValue = LunaSettings.getString("PirateMiniMegaMod", "pmm_vanillatoggle_kite");
            return toggleValue != null && toggleValue.contains("PMM"); // Check if the string contains "PMM"
        }
        return true; // Default value if the mod isn't enabled
    }

    public static Boolean VanillaChangeToggle_Mudskipper() {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            String toggleValue = LunaSettings.getString("PirateMiniMegaMod", "pmm_vanillatoggle_mudskipper");
            return toggleValue != null && toggleValue.contains("PMM"); // Check if the string contains "PMM"
        }
        return true; // Default value if the mod isn't enabled
    }

    public static Boolean VanillaChangeToggle_Mule() {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            String toggleValue = LunaSettings.getString("PirateMiniMegaMod", "pmm_vanillatoggle_mule");
            return toggleValue != null && toggleValue.contains("PMM"); // Check if the string contains "PMM"
        }
        return true; // Default value if the mod isn't enabled
    }

    public static Boolean OmegaToggle() {
        Boolean omega = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            omega = LunaSettings.getBoolean("PirateMiniMegaMod", "pmm_omegatoggle");
        }
        return omega;
    }

    public static Boolean MasterRecover() {
        Boolean masterRecover = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            masterRecover = LunaSettings.getBoolean("PirateMiniMegaMod", "pmm_masterrecover");
        }
        return masterRecover;
    }
}
