package com.carcrashsimulator.fxUtils;

import com.carcrashsimulator.models.*;
import com.carcrashsimulator.models.*;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Pair;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class IntersectionTemplate {
    private final Map<String, TrafficLight> trafficLights;
    private final Map<Directions, Label> exitLabels;
    private final List<CarSpawn> spawnPoints;
    private Map<String, Lane> lanes;
    private final Map<Pair<Directions, LaneDirection>, Point2D> stopLines;

    public IntersectionTemplate(Map<String, TrafficLight> trafficLights, Map<Directions, Label> exitLabels, List<CarSpawn> spawnPoints, Map<Pair<Directions, LaneDirection>, Point2D> stopPoints) {
        this.trafficLights = trafficLights;
        this.exitLabels = exitLabels;
        this.spawnPoints = spawnPoints;
        this.stopLines = stopPoints;
        lanes = new HashMap<>();
        Set<LaneDirection> laneDirs = Set.of(LaneDirection.EXIT);
        for (Directions direction : Directions.values()) {
            lanes.put(direction.toString(), new Lane(direction, laneDirs, stopLines.get(new Pair<>(direction, LaneDirection.EXIT)), CarState.DRIVING));
        }
    }

    public void carSpawns(Car car) {
        Pair<Directions, LaneDirection> laneKeyMaterial = car.getStartingLane();
        StringBuilder lightName = new StringBuilder("btn"+"light");
        lightName.insert(3, laneKeyMaterial.getKey().toString());

        if (laneKeyMaterial.getValue() == LaneDirection.R || laneKeyMaterial.getValue() == LaneDirection.L) {
            lightName.insert(4, laneKeyMaterial.getValue().toString());
        } else if (laneKeyMaterial.getValue() == LaneDirection.EXIT) {
            throw new IllegalArgumentException("Cars cannot spawn at exit lane.");
        }

        lanes.get(lightName.toString()).getCarsInLane().add(car);
    }

    public void carEntersIntersection(Car car) {
        for (Lane lane : lanes.values()) {
            if (lane.carLeaves(car)) {
                return;
            }
        }
    }

    public void carLeavesIntersection(Car car) {
        for (Lane lane : lanes.values()) {
            if (lane.getDirection().equals(car.getDirectionOut()) && lane.getLaneDirections().contains(LaneDirection.EXIT)) {
                lane.getCarsInLane().add(car);
                return;
            }
        }
        throw new IndexOutOfBoundsException("No exit lane found");
    }
}
