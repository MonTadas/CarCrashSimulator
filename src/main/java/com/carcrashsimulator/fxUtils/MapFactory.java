package com.carcrashsimulator.fxUtils;

import com.carcrashsimulator.fxControllers.GameMap;
import com.carcrashsimulator.models.Directions;
import com.carcrashsimulator.models.LaneDirection;
import com.carcrashsimulator.models.TrafficLight;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFactory {
    private List<CubicCurveTo> allCubicCurves;
    private Map<LaneDirection, Integer> animationDurations;
    private final List<Paint> allColours;

    public MapFactory() {
        allCubicCurves = new ArrayList<>();
        animationDurations = new HashMap<>();
        animationDurations.put(LaneDirection.L, 2000);
        animationDurations.put(LaneDirection.R, 1000);
        animationDurations.put(LaneDirection.F, 1500);
        allColours = List.of(Color.AQUA, Color.DARKBLUE, Color.DARKGRAY, Color.YELLOW, Color.AQUAMARINE,
                Color.DARKORANGE, Color.TOMATO, Color.VIOLET, Color.SPRINGGREEN, Color.DARKRED, Color.ORANGERED,
                Color.MAROON, Color.GREENYELLOW);
    }

    public IntersectionTemplate developmentIntersection(GameMap map) {
        Map<String, TrafficLight> trafficLights = Map.of("btnSLight", generateTrafficLight(" S ", "btnSLight", 415, 440, "green-light", map));
        Map<Directions, Label> exitLabels = new HashMap<>();
        List<CarSpawn> spawnPoints = new ArrayList<>();
        List<CubicCurveTo> allCubicCurves = new ArrayList<>();
        Map<Pair<Directions, LaneDirection>, Point2D> stopLines = new HashMap<>();
        allCubicCurves.add(new CubicCurveTo());
        spawnPoints.add(new CarSpawn(Directions.S, Map.of(LaneDirection.L, Directions.W),
                new MoveTo(430, 605), new LineTo(430, 440),
                Map.of(LaneDirection.L, new CubicCurveTo(400, 280, 200, 260, 120, 265 + 15)),
                Map.of(LaneDirection.F, new LineTo(- 30, 280)), animationDurations, allColours));

        spawnPoints.add(new CarSpawn(Directions.N, Map.of(LaneDirection.F, Directions.S),
                new MoveTo(365, - 30), new LineTo(365, 120),
                Map.of(LaneDirection.F, new LineTo(365, 440)),
                Map.of(LaneDirection.F, new LineTo(350 + 15, 605)), animationDurations, allColours));

        return new IntersectionTemplate(trafficLights, exitLabels, spawnPoints, stopLines);
    }

    public IntersectionTemplate basicIntersection(GameMap map) {
        Map<String, TrafficLight> trafficLights = new HashMap<>();
        Map<Directions, Label> exitLabels = new HashMap<>();
        List<CarSpawn> spawnPoints = new ArrayList<>();
        Map<Pair<Directions, LaneDirection>, Point2D> stopLines = new HashMap<>();

        trafficLights.put("btnNlight", generateTrafficLight(" N ", "btnNlight", 350, 120, "green-light", map));
        exitLabels.put(Directions.N, generateExitLabel("labelNExit", 430,30));
        stopLines.put(new Pair<>(Directions.N, LaneDirection.F), new Point2D(350 + 15, 105));
        stopLines.put(new Pair<>(Directions.N, LaneDirection.EXIT), new Point2D(430, - 200));//
        spawnPoints.add(new CarSpawn(Directions.N, Map.of(LaneDirection.F, Directions.S),
                new MoveTo(350 + 15, - 200), new LineTo(350 + 15, 120),
                Map.of(LaneDirection.F, new LineTo(350 + 15, 440)), Map.of(LaneDirection.F, new LineTo(350 + 15, 605)), animationDurations, allColours));

        trafficLights.put("btnElight", generateTrafficLight(" E ", "btnElight", 640, 265, "red-light", map));
        exitLabels.put(Directions.E, generateExitLabel("labelEExit", 750,330));
        stopLines.put(new Pair<>(Directions.E, LaneDirection.F), new Point2D(655, 265 + 15));
        stopLines.put(new Pair<>(Directions.E, LaneDirection.EXIT), new Point2D(1010, 335));//
        spawnPoints.add(new CarSpawn(Directions.E, Map.of(LaneDirection.F, Directions.W),
                new MoveTo(1010, 265 + 15), new LineTo(640, 265 + 15),
                Map.of(LaneDirection.F, new LineTo(120, 265 + 15)), Map.of(LaneDirection.F, new LineTo(- 30, 265 + 15)), animationDurations, allColours));

        trafficLights.put("btnSlight", generateTrafficLight(" S ", "btnSlight", 415, 440, "green-light", map));
        exitLabels.put(Directions.S, generateExitLabel("labelSExit", 350,550));
        stopLines.put(new Pair<>(Directions.S, LaneDirection.F), new Point2D(415 + 15, 455));
        stopLines.put(new Pair<>(Directions.S, LaneDirection.EXIT), new Point2D(350, 805));//
        spawnPoints.add(new CarSpawn(Directions.S, Map.of(LaneDirection.F, Directions.N),
                new MoveTo(415 + 15, 805), new LineTo(415 + 15, 440),
                Map.of(LaneDirection.F, new LineTo(415 + 15, 120)), Map.of(LaneDirection.F, new LineTo(415 + 15, - 30)), animationDurations, allColours));

        trafficLights.put("btnWlight", generateTrafficLight(" W ", "btnWlight", 120, 320, "red-light", map));
        exitLabels.put(Directions.W, generateExitLabel("labelWExit", 35,280));
        stopLines.put(new Pair<>(Directions.W, LaneDirection.F), new Point2D(105, 335));
        stopLines.put(new Pair<>(Directions.W, LaneDirection.EXIT), new Point2D(- 200, 265 + 15));
        spawnPoints.add(new CarSpawn(Directions.W, Map.of(LaneDirection.F, Directions.E),
                new MoveTo(- 200, 320 + 15), new LineTo(120, 320 + 15),
                Map.of(LaneDirection.F, new LineTo(640, 320 + 15)), Map.of(LaneDirection.F, new LineTo(810, 320 + 15)), animationDurations, allColours));

        return new IntersectionTemplate(trafficLights, exitLabels, spawnPoints, stopLines);
    }

    public IntersectionTemplate intersectionWithLeftLanes(GameMap map) {
        Map<String, TrafficLight> trafficLights = new HashMap<>();
        Map<Directions, Label> exitLabels = new HashMap<>();
        List<CarSpawn> spawnPoints = new ArrayList<>();
        Map<Pair<Directions, LaneDirection>, Point2D> stopLines = new HashMap<>();
        trafficLights.put("btnNlight", generateTrafficLight(" N ", "btnNlight", 315, 120, "green-light", map));
        trafficLights.put("btnNLlight", generateTrafficLight("NL", "btnNLlight", 374, 120, "red-light", map));
        trafficLights.put("btnElight", generateTrafficLight(" E ", "btnElight", 640, 223, "red-light", map));
        trafficLights.put("btnELlight", generateTrafficLight("EL", "btnELlight", 640, 274, "red-light", map));
        trafficLights.put("btnSlight", generateTrafficLight(" S ", "btnSlight", 476, 440, "green-light", map));
        trafficLights.put("btnSLlight", generateTrafficLight("SL", "btnSLlight", 414, 440, "red-light", map));
        trafficLights.put("btnWlight", generateTrafficLight(" W ", "btnWlight", 120, 334, "red-light", map));
        trafficLights.put("btnWLlight", generateTrafficLight("WL", "btnWLlight", 120, 293, "red-light", map));

        /*spawnPoints.add(new Point2D(315, - 30));
        spawnPoints.add(new Point2D(376, - 30));
        spawnPoints.add(new Point2D(810, 233));
        spawnPoints.add(new Point2D(810, 274));
        spawnPoints.add(new Point2D(476, 605));
        spawnPoints.add(new Point2D(414, 605));
        spawnPoints.add(new Point2D(- 30, 334));
        spawnPoints.add(new Point2D(- 30, 293));*/

        return new IntersectionTemplate(trafficLights, exitLabels, spawnPoints, stopLines);
    }

    private static TrafficLight generateTrafficLight(String name, String id, double x, double y, String classCSS, GameMap map) {
        Button trafficLightSprite = new Button(name);
        trafficLightSprite.setId(id);
        trafficLightSprite.setLayoutX(x);
        trafficLightSprite.setLayoutY(y);
        trafficLightSprite.setMinWidth(30);
        trafficLightSprite.setMinHeight(25);
        trafficLightSprite.setOnAction(map::switchTrafficLights);
        trafficLightSprite.getStyleClass().add("light-text-style");
        trafficLightSprite.getStyleClass().add(classCSS);
        trafficLightSprite.toFront();
        return new TrafficLight(trafficLightSprite);
    }

    private static Label generateExitLabel(String id, int x, int y){
        Label exitLabel = new Label();
        exitLabel.setId(id);
        exitLabel.setLayoutX(x);
        exitLabel.setLayoutY(y);
        exitLabel.setText(0+"");
        return exitLabel;
    }

    private static Point2D rotatePoint(Point2D p, double angle) {
        Point2D center = new Point2D(400, 300);
        double rad = Math.toRadians(angle);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        double dx = p.getX() - center.getX();
        double dy = p.getY() - center.getY();
        return new Point2D(
                center.getX() + dx * cos - dy * sin,
                center.getY() + dx * sin + dy * cos
        );
    }
}
