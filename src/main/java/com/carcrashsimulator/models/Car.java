package com.carcrashsimulator.models;

import com.carcrashsimulator.fxControllers.GameMap;
import com.carcrashsimulator.fxUtils.GameStatus;
import javafx.animation.PathTransition;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;
import lombok.Getter;

import java.util.List;

@Getter
public class Car implements IReactToStatus {
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
        initializeAnimations(map);
    }

    private void initializeAnimations(GameMap map) {
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
            map.getAnchorTheBase().getChildren().remove(this.sprite);
        });
    }

    public void carCrash() {
        this.state = CarState.CRASHED;
        this.carPath.get(1).pause();
    }

    public void markCarVisible() {
        this.carPath.get(pathSection).stop();
        visible = true;
        sprite.toBack();
    }

    public void nextSection() {
        if (this.pathSection == 2) {
            throw new IndexOutOfBoundsException("No more animation sections exist.");
        }
        this.pathSection++;
        carPath.get(pathSection).setDelay(Duration.ZERO);
        carPath.get(pathSection).play();
    }

    public void changeStateToWaiting() {
        this.state = CarState.WAITING;
    }

    public void changeStateToDriving() {
        this.state = CarState.DRIVING;
    }

    @Override
    public void reactToGameStatus(GameStatus gameStatus) {
        switch (gameStatus) {
            case RUNNING:
                if(state == CarState.DRIVING) {
                    this.carPath.get(pathSection).play();
                }
                else if(state == CarState.WAITING) {
                    this.carPath.get(pathSection).pause();
                }
                break;
            case PAUSED, OVER:
                this.carPath.get(pathSection).pause();
                break;
        }
    }
}
