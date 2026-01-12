package com.carcrashsimulator.fxControllers;

import com.carcrashsimulator.fxUtils.*;
import com.carcrashsimulator.models.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javafx.util.Pair;
import lombok.Getter;

import java.net.URL;
import java.util.*;

public class GameMap implements Initializable {
    @FXML
    private Button btnPause;
    @FXML
    private Label labelScore;
    @FXML
    private Label labelGameStatus;
    @FXML
    @Getter
    private AnchorPane anchorTheBase;

    private int score;
    private int scoreDecimator;
    private int carIds;
    private int switchCountdown;
    private MapFactory mapFactory;
    @Getter
    private IntersectionTemplate map;
    @Getter
    private GameStatus gameStatus;
    private MoveOutDirection carMoveOutDirection;
    private List<Button> buttonsPlayingAnimations;
    private List<Car> cars;
    @Getter
    private List<Car> carsInIntersection;
    private Map<Directions, Lane> exitLanes;
    private List<IReactToStatus> reactToStatus;

    private Timeline gameFrames;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reactToStatus = new ArrayList<>();
        buttonsPlayingAnimations = new ArrayList<>();
        cars = new ArrayList<>();
        carsInIntersection = new ArrayList<>();
        mapFactory = new MapFactory();
        exitLanes = new HashMap<>();

        double framerate = 60;
        gameFrames = new Timeline(new KeyFrame(Duration.millis(1000 / framerate), e -> runTick()));
        gameFrames.setCycleCount(Timeline.INDEFINITE);

        restartGame();
    }

    public int getAndIncrementLatestCarId() {
        return ++carIds;
    }

    public void addLanesByTrafficLights(List<String> trLightIds, List<LaneDirection> laneDirections) {
        for (String lightName : map.getTrafficLights().keySet()) {
            if (trLightIds.contains(lightName)) {
                map.getLanes().put(lightName, new Lane(Directions.fromLightId(lightName), new HashSet<>(laneDirections),
                        map.getStopLines().get(new Pair<>(Directions.fromLightId(lightName), laneDirections.getFirst())),
                        map.getTrafficLights().get(lightName).getSprite().getStyleClass().getLast()));
            }
        }
    }

    public void runTick() {
        if (gameStatus != GameStatus.OVER) {
            scoreDecimator++;
            if (scoreDecimator == 120) {
                scoreDecimator = 0;
                score++;
                switchCountdown++;
                labelScore.setText("Score: " + score);
                for (CarSpawn spawn : map.getSpawnPoints()) {
                    if ((int) (Math.random() * 9) > 3) {
                        cars.add(spawn.SpawnCar(this, new Pair<>(spawn.getDirection(), LaneDirection.F)));
                        reactToStatus.add(cars.getLast());
                        map.carSpawns(cars.getLast());
                    }
                }
                carsExit();
                for (Directions direction : exitLanes.keySet()) {
                    map.getExitLabels().get(direction).setText(exitLanes.get(direction).getCarsInLane().size() + "");
                }
                for (TrafficLight light : map.getTrafficLights().values()) {
                    light.getSprite().setText(map.getLanes().get(light.getName()).getCarsInLane().size() + "");
                }
            }
            if (switchCountdown == 10) {
                switchCountdown = 0;
                switchCarMoveOutDirection();
            }
            for (Lane lane : map.getLanes().values()) {
                if (lane.getLaneState() == CarState.WAITING && lane.getCarsInLane().size() >= 10) {
                    gameStatus = GameStatus.OVER;
                    labelGameStatus.setText("Game Over");
                    allReactToStatus();
                }
                for (Car car : lane.getCarsInLane()) {
                    if (lane.getCarsInLane().indexOf(car) <= 4 && !car.isVisible()) {
                        anchorTheBase.getChildren().add(car.getSprite());
                        car.markCarVisible();
                    }
                    lane.laneCheck(car);
                }
            }
            for (Car car : carsInIntersection) {
                Bounds car1Bounds = car.getSprite().getBoundsInParent();
                for (Car car2 : carsInIntersection) {
                    Bounds car2Bounds = car2.getSprite().getBoundsInParent();
                    if (car != car2) {
                        if (car1Bounds.intersects(car2Bounds)) {
                            gameStatus = GameStatus.OVER;
                            car.carCrash();
                            car2.carCrash();
                            labelGameStatus.setText("Game Over");
                        }
                    }
                }
            }
            if (score == 120) {
                gameStatus = GameStatus.OVER;
                labelGameStatus.setText("Game Won!");
                allReactToStatus();
            }
            allReactToStatus();
        }
    }

    private void carsExit() {
        Car car;
        switch (carMoveOutDirection) {
            case HORIZONTAL:
                if (!exitLanes.get(Directions.E).getCarsInLane().isEmpty()) {
                    car = exitLanes.get(Directions.E).getCarsInLane().getFirst();
                    anchorTheBase.getChildren().remove(car.getSprite());
                    exitLanes.get(Directions.E).getCarsInLane().remove(car);
                }
                if (!exitLanes.get(Directions.W).getCarsInLane().isEmpty()) {
                    car = exitLanes.get(Directions.W).getCarsInLane().getFirst();
                    anchorTheBase.getChildren().remove(car.getSprite());
                    exitLanes.get(Directions.W).getCarsInLane().remove(car);
                }
                break;
            case VERTICAL:
                if (!exitLanes.get(Directions.N).getCarsInLane().isEmpty()) {
                    car = exitLanes.get(Directions.N).getCarsInLane().getFirst();
                    anchorTheBase.getChildren().remove(car.getSprite());
                    exitLanes.get(Directions.N).getCarsInLane().remove(car);
                }
                if (!exitLanes.get(Directions.S).getCarsInLane().isEmpty()) {
                    car = exitLanes.get(Directions.S).getCarsInLane().getFirst();
                    anchorTheBase.getChildren().remove(car.getSprite());
                    exitLanes.get(Directions.S).getCarsInLane().remove(car);
                }
                break;
        }
    }

    private void switchCarMoveOutDirection() {
        if (Objects.requireNonNull(carMoveOutDirection) == MoveOutDirection.HORIZONTAL) {
            carMoveOutDirection = MoveOutDirection.VERTICAL;
        } else {
            carMoveOutDirection = MoveOutDirection.HORIZONTAL;
        }
    }

    @FXML
    public void switchTrafficLights(ActionEvent actionEvent) {
        Button caller = (Button) actionEvent.getSource();
        if (!buttonsPlayingAnimations.contains(caller) && gameStatus == GameStatus.RUNNING) {
            buttonsPlayingAnimations.add(caller);
            switch (caller.getStyleClass().get(2)) {
                case "green-light":
                    map.getTrafficLights().get(caller.getId()).changeColourToRed(caller, this);
                    break;
                case "red-light":
                    map.getTrafficLights().get(caller.getId()).changeColourToGreen(caller, this);
                    break;
            }
            buttonsPlayingAnimations.remove(caller);
        }
    }

    public void pauseTrafficAction() {
        if (gameStatus == GameStatus.PAUSED) {
            gameStatus = GameStatus.RUNNING;
        } else if (gameStatus == GameStatus.RUNNING) {
            gameStatus = GameStatus.PAUSED;
        }
        allReactToStatus();
    }

    private void allReactToStatus() {
        for (IReactToStatus animatedElement : reactToStatus) {
            animatedElement.reactToGameStatus(this.gameStatus);
        }
        switch (gameStatus) {
            case RUNNING:
                btnPause.setText("Pause");
                gameFrames.play();
                labelGameStatus.setText("Running");
                break;
            case PAUSED:
                btnPause.setText("Resume");
                gameFrames.pause();
                break;
            case OVER:
                btnPause.setText("Resume");
                gameFrames.stop();
                labelGameStatus.setText("Game Over");
                break;
        }
    }

    public void restartGame() {
        reactToStatus.clear();
        carIds = 0;
        score = 0;
        scoreDecimator = 0;
        switchCountdown = 0;
        cars.forEach(car -> {
            anchorTheBase.getChildren().remove(car.getSprite());
        });
        cars.clear();
        carsInIntersection.clear();
        buttonsPlayingAnimations.clear();
        carMoveOutDirection = MoveOutDirection.VERTICAL;
        gameStatus = GameStatus.OVER;
        gameFrames.stop();

        map = mapFactory.basicIntersection(this);
        for (CarSpawn spawn : map.getSpawnPoints()) {
            String id = "btn" + spawn.getDirection() + "light";
            addLanesByTrafficLights(List.of(id), new ArrayList<>(spawn.getExits().keySet()));
        }
        map.getLanes().values().forEach(l -> {
            if (l.getLaneDirections().contains(LaneDirection.EXIT))
                exitLanes.put(l.getDirection(), l);
        });
        //useless for one map implementation
        anchorTheBase.getChildren().removeAll(map.getExitLabels().values());
        anchorTheBase.getChildren().addAll(map.getExitLabels().values());
        map.getTrafficLights().values().forEach(trafficLight -> {
            anchorTheBase.getChildren().add(trafficLight.getSprite());
            reactToStatus.add(trafficLight);
        });
        gameStatus = GameStatus.PAUSED;
        labelGameStatus.setText("Stopped");
        btnPause.setText("Resume");
    }
}
