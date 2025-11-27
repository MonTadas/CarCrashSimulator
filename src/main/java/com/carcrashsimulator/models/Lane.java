package com.carcrashsimulator.models;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
public class Lane {
    private final Directions direction;
    private final Set<LaneDirection> laneDirections;
    private List<Car> carsInLane;
    private CarState laneState;
    private final Point2D stopLine;

    public Lane(Directions direction, Set<LaneDirection> laneDirections, Point2D stopLine, CarState laneState) {
        this.direction = direction;
        this.laneDirections = laneDirections;
        this.laneState = laneState;
        this.stopLine = stopLine;
        carsInLane = new ArrayList<>();
    }

    public Lane(Directions direction, Set<LaneDirection> laneDirections, Point2D stopLine, String lightColour) {
        this.direction = direction;
        this.laneDirections = laneDirections;
        this.carsInLane = new ArrayList<>();
        this.stopLine = stopLine;
        changeLaneState(lightColour);
    }


    public boolean carLeaves(Car car) {
        return carsInLane.remove(car);
    }

    public void changeLaneState(String lightColourClass) {
        switch (lightColourClass) {
            case "red-light":
                laneState = CarState.WAITING;
                break;
            case "green-light":
                laneState = CarState.DRIVING;
                break;
            default:
                throw new IllegalArgumentException("Provided class is invalid");
        }
    }

    public void laneCheck(Car car) {
        int carIndex = carsInLane.indexOf(car);
        if (carIndex > 0 && ! laneDirections.contains(LaneDirection.EXIT)) {
            if (getProximity(car, carsInLane.get(carIndex - 1)) < 40) {
                car.pauseCar();
            } else {
                car.startCar();
            }
        } else if (carIndex > 0 && carsInLane.size() < carIndex + 1 && laneDirections.contains(LaneDirection.EXIT)) {
            if (getProximity(car, carsInLane.get(carIndex + 1)) < 40) {
                car.pauseCar();
            } else {
                car.startCar();
            }
        } else if (carIndex == 0 && getProximityToLine(car, stopLine) < 15 && laneState == CarState.WAITING) {
            car.pauseCar();
        } else {
            car.startCar();
        }
    }

    private int getProximity(Car car1, Car car2) {
        Bounds b1 = car1.getSprite().getBoundsInParent(),
                b2 = car2.getSprite().getBoundsInParent();
        if (b1.getCenterX() - b2.getCenterX() == 0) {
            return (int) Math.abs(b1.getCenterY() - b2.getCenterY());
        } else if (b1.getCenterY() - b2.getCenterY() == 0) {
            return (int) Math.abs(b1.getCenterX() - b2.getCenterX());
        }
        return 420;     //must be high to get that
    }

    private int getProximityToLine(Car car, Point2D line) {
        Bounds b = car.getSprite().getBoundsInParent();
        if (direction == Directions.N || direction == Directions.S) {
            return (int) Math.abs(b.getCenterY() - line.getY());
        } else if (direction == Directions.E || direction == Directions.W) {
            return (int) Math.abs(b.getCenterX() - line.getX());
        }
        return 420;
    }
}
