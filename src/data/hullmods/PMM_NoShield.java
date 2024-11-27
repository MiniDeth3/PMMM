package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShieldAPI.ShieldType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class PMM_NoShield extends BaseHullMod {
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.setShield(ShieldType.NONE, 0f, 1f, 1f);
    }
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return false;
    }
    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        if (ship.getVariant().hasHullMod(HullMods.MAKESHIFT_GENERATOR)) {return "Ship has no space for shields";}
        return null;
    }
}