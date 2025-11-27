package com.carcrashsimulator.fxUtils;

import com.carcrashsimulator.models.Car;
import com.carcrashsimulator.models.Directions;
import com.carcrashsimulator.models.LaneDirection;
import com.carcrashsimulator.fxControllers.GameMap;
import com.carcrashsimulator.models.*;
import javafx.animation.PathTransition;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.util.Duration;
import javafx.util.Pair;
import lombok.Getter;

import java.util.List;
import java.util.Map;


public class CarSpawn {
    @Getter
    private final Directions direction;
    private final MoveTo position;
    private final LineTo startingLine;
    private final Map<LaneDirection, PathElement> intersectionMovement;
    private final Map<LaneDirection, LineTo> outsideIntersectionMovement;
    private final Map<LaneDirection, Integer> availableDurations;
    @Getter
    private final Map<LaneDirection, Directions> exits;
    private final List<Paint> availableColors;

    public CarSpawn(Directions dir, Map<LaneDirection, Directions> exits, MoveTo position, LineTo startingLine, Map<LaneDirection, PathElement> intersectionMovement, Map<LaneDirection, LineTo> outsideIntersectionMovement, Map<LaneDirection, Integer> availableDurations, List<Paint> availableColors) {
        this.direction = dir;
        this.exits = exits;
        this.position = position;
        this.startingLine = startingLine;
        this.intersectionMovement = intersectionMovement;
        this.outsideIntersectionMovement = outsideIntersectionMovement;
        this.availableDurations = availableDurations;
        this.availableColors = availableColors;
    }

    public Car SpawnCar(GameMap map, Pair<Directions, LaneDirection> currentLane) {
        if (! exits.containsKey(currentLane.getValue())) {
            throw new IllegalArgumentException("Cars cannot drive like that from this lane");
        }
        MoveTo tempMoveTo;
        List<PathTransition> pathTransition = List.of(new PathTransition(), new PathTransition(), new PathTransition());
        List<Path> paths = List.of(new Path(), new Path(), new Path());
        Rectangle visibleCar = createVisibleCar();
        paths.get(0).getElements().add(position);
        paths.get(0).getElements().add(startingLine);
        pathTransition.get(0).setDuration(Duration.millis(800));

        paths.get(1).getElements().add(new MoveTo(startingLine.getX(), startingLine.getY()));
        paths.get(1).getElements().add(intersectionMovement.get(currentLane.getValue()));
        pathTransition.get(1).setDuration(Duration.millis(availableDurations.get(currentLane.getValue()) / 2.));

        if (intersectionMovement.get(currentLane.getValue()) instanceof LineTo line) {
            tempMoveTo = new MoveTo(line.getX(), line.getY());
        } else if (intersectionMovement.get(currentLane.getValue()) instanceof CubicCurveTo curve) {
            tempMoveTo = new MoveTo(curve.getX(), curve.getY());
        } else {
            throw new ClassCastException("Intersection path type detection failure.");
        }
        paths.get(2).getElements().add(tempMoveTo);
        paths.get(2).getElements().add(outsideIntersectionMovement.get(currentLane.getValue()));
        pathTransition.get(2).setDuration(Duration.millis(550));

        for (int i = 0; i < 3; i++) {
            pathTransition.get(i).setNode(visibleCar);
            pathTransition.get(i).setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
            pathTransition.get(i).setPath(paths.get(i));
        }
        return new Car(map, currentLane, exits.get(currentLane.getValue()), visibleCar, pathTransition);
    }

    private Rectangle createVisibleCar() {
        int randomColor = (int) (Math.random() * availableColors.size());
        Rectangle visibleCar = new Rectangle(30, 30);
        visibleCar.setFill(availableColors.get(randomColor));
        return visibleCar;
    }
}
