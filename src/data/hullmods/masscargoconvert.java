package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class masscargoconvert extends BaseHullMod {
    public static float CARGOMULT = 0.5f;

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

        ship.getMutableStats().getCargoMod().modifyMult(id, CARGOMULT);
    }
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return false;
    }
    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        if (ship.getVariant().hasHullMod(HullMods.EXPANDED_CARGO_HOLDS)) {return "Cargo bay has been irreversibly lost or converted";}
        return null;
    }
}