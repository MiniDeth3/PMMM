package data.missions.pmm_lightstest;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;
import java.util.LinkedList;
import java.util.List;
import org.dark.shaders.distortion.DistortionAPI;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.dark.shaders.light.LightAPI;
import org.dark.shaders.light.LightShader;
import org.dark.shaders.light.StandardLight;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class MissionDefinition implements MissionDefinitionPlugin {

    private static final Vector2f ZERO = new Vector2f();

    @Override
    public void defineMission(MissionDefinitionAPI api) {
        api.initFleet(FleetSide.PLAYER, "", FleetGoal.ATTACK, false, 0);
        api.initFleet(FleetSide.ENEMY, "", FleetGoal.ATTACK, false, 0);

        List<String> ids = Global.getSector().getAllEmptyVariantIds();
        api.addToFleet(FleetSide.PLAYER, ids.get((int) (ids.size() * Math.random())), FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.PLAYER,"pmm_champion2_Modified", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, ids.get((int) (ids.size() * Math.random())), FleetMemberType.SHIP, true);

        api.setFleetTagline(FleetSide.PLAYER, "You");
        api.setFleetTagline(FleetSide.ENEMY, "Them");

        api.addBriefingItem("This mission automatically enables dev mode");

        float width = 12000f;
        float height = 12000f;
        api.initMap(-width / 2f, width / 2f, -height / 2f, height / 2f);

        api.addPlugin(new Plugin());
    }

    private final static class Plugin extends BaseEveryFrameCombatPlugin {

        private static final List<DistortionAPI> distortionList = new LinkedList<>();
        private static final List<LightAPI> lightList = new LinkedList<>();

        @Override
        public void advance(float amount, List<InputEventAPI> events) {
            Global.getCombatEngine().setDoNotEndCombat(true);
            ShipAPI player = Global.getCombatEngine().getPlayerShip();
            if (player == null) {
                return;
            }

            for (InputEventAPI event : events) {
                if (event.isConsumed()) {
                    continue;
                }

                if (event.isKeyDownEvent() && event.getEventValue() == org.lwjgl.input.Keyboard.KEY_NUMPAD1) {
                    Vector2f loc = player.getMouseTarget();
                    StandardLight light = new StandardLight(loc, ZERO, ZERO, null);
                    light.setSize((float) Math.random() * 1000f + 100f);
                    light.setIntensity((float) Math.random() * 1.9f + 0.1f);
                    light.setHeight((float) Math.random() * 400f + 100f);
                    light.setColor((float) Math.random(), (float) Math.random(), (float) Math.random());
                    light.makePermanent();
                    light.fadeIn(0.5f);

                    lightList.add(light);
                    LightShader.addLight(light);

                    event.consume();
                    break;
                }

                if (event.isKeyDownEvent() && event.getEventValue() == org.lwjgl.input.Keyboard.KEY_NUMPAD2) {
                    Vector2f loc = player.getMouseTarget();
                    Vector2f loc2 = player.getLocation();
                    StandardLight light = new StandardLight(loc, loc2, ZERO, ZERO, null);
                    light.setSize((float) Math.random() * 1000f + 100f);
                    light.setIntensity((float) Math.random() * 1.9f + 0.1f);
                    light.setHeight((float) Math.random() * 400f + 100f);
                    light.setColor((float) Math.random(), (float) Math.random(), (float) Math.random());
                    light.makePermanent();
                    light.fadeIn(0.5f);

                    lightList.add(light);
                    LightShader.addLight(light);

                    event.consume();
                    break;
                }

                if (event.isKeyDownEvent() && event.getEventValue() == org.lwjgl.input.Keyboard.KEY_NUMPAD3) {
                    Vector2f loc = player.getMouseTarget();
                    float width = (float) Math.random() * 340f + 10f;
                    Vector2f dir = Vector2f.sub(player.getLocation(), loc, null);
                    float angle = (float) Math.toDegrees(Math.atan2(dir.y, dir.x));
                    StandardLight light = new StandardLight(loc, ZERO, angle + width * 0.5f, angle - width * 0.5f);
                    light.setSize((float) Math.random() * 1000f + 100f);
                    light.setIntensity((float) Math.random() * 1.9f + 0.1f);
                    light.setHeight((float) Math.random() * 400f + 100f);
                    light.setColor((float) Math.random(), (float) Math.random(), (float) Math.random());
                    light.makePermanent();
                    light.fadeIn(0.5f);

                    lightList.add(light);
                    LightShader.addLight(light);

                    event.consume();
                    break;
                }

                if (event.isKeyDownEvent() && event.getEventValue() == org.lwjgl.input.Keyboard.KEY_NUMPAD4) {
                    Vector3f dir = new Vector3f((float) Math.random(), (float) Math.random(), -1f);
                    StandardLight light = new StandardLight(dir);
                    light.setIntensity((float) Math.random() * 0.9f + 0.1f);
                    light.setSpecularIntensity((float) Math.random() * 2.9f + 0.1f);
                    light.setColor((float) Math.random(), (float) Math.random(), (float) Math.random());
                    light.makePermanent();
                    light.fadeIn(0.5f);

                    lightList.add(light);
                    LightShader.addLight(light);

                    event.consume();
                    break;
                }

                if (event.isKeyDownEvent() && event.getEventValue() == org.lwjgl.input.Keyboard.KEY_NUMPAD5) {
                    Vector2f loc = player.getMouseTarget();
                    RippleDistortion distortion = new RippleDistortion(loc, ZERO);
                    distortion.setSize((float) Math.random() * 450f + 50f);
                    distortion.setIntensity((float) Math.random() * 450f + 50f);
                    distortion.flip((Math.random() < 0.5));
                    distortion.setCurrentFrame((float) Math.random() * 59f);
                    if (Math.random() < 0.5) {
                        float width = (float) Math.random() * 340f + 10f;
                        Vector2f dir = Vector2f.sub(player.getLocation(), loc, null);
                        float angle = (float) Math.toDegrees(Math.atan2(dir.y, dir.x));
                        distortion.setArc(angle - width * 0.5f, angle + width * 0.5f);
                        distortion.setArcAttenuationWidth((float) Math.random() * 180f);
                    }
                    distortion.fadeInIntensity(0.5f);

                    distortionList.add(distortion);
                    DistortionShader.addDistortion(distortion);

                    event.consume();
                    break;
                }

                if (event.isKeyDownEvent() && event.getEventValue() == org.lwjgl.input.Keyboard.KEY_NUMPAD6) {
                    for (DistortionAPI distortion : distortionList) {
                        DistortionShader.removeDistortion(distortion);
                    }
                    for (LightAPI light : lightList) {
                        LightShader.removeLight(light);
                    }

                    event.consume();
                    break;
                }
            }
        }

        @Override
        public void init(CombatEngineAPI engine) {
            Global.getSettings().setDevMode(true);
        }
    }
}
