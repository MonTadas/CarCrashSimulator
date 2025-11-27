package com.carcrashsimulator.models;

import com.carcrashsimulator.fxControllers.GameMap;
import javafx.animation.PathTransition;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;
import lombok.Getter;

import java.util.List;

@Getter
public class Car {
    private final int id;
    private int pathSection;
    private final Pair<Directions, LaneDirection> startingLane;
    private final Directions directionOut;
    private CarState state;
    private final Rectangle sprite;
    private final List<PathTransition> carPath;
    private boolean visible;

    public Car(GameMap map, Pair<Directions, LaneDirection> startLane, Directions directionOut, Rectangle physicalForm, List<PathTransition> carPath) {
        this.id = map.getAndIncrementLatestCarId();
        this.directionOut = directionOut;
        startingLane = startLane;
        this.sprite = physicalForm;
        this.carPath = carPath;
        this.pathSection = 0;
        visible = false;
        this.carPath.get(0).setOnFinished(event -> {
            nextSection();
            map.getCarsInIntersection().add(this);
            map.getMap().carEntersIntersection(this);
        });
        this.carPath.get(1).setOnFinished(event -> {
            nextSection();
            map.getCarsInIntersection().remove(this);
            map.getMap().carLeavesIntersection(this);
        });
        this.carPath.get(2).setOnFinished(event -> {
            map.anchorTheBase.getChildren().remove(physicalForm);
        });
    }

    public void startCar() {
        this.carPath.get(pathSection).play();
        this.state = CarState.DRIVING;
    }

    public void pauseCar() {
        this.carPath.get(pathSection).pause();
        this.state = CarState.WAITING;
    }

    public void carCrash() {
        this.state = CarState.CRASHED;
        this.carPath.get(1).pause();
    }

    public void makeCarVisible(GameMap map) {
        startCar();
        map.anchorTheBase.getChildren().add(sprite);
        visible = true;
        sprite.toBack();
    }

    public void nextSection() {
        this.pathSection++;
        carPath.get(pathSection).setDelay(Duration.ZERO);
        carPath.get(pathSection).play();
    }
}
