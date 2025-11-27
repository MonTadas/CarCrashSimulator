package com.carcrashsimulator.fxControllers;

import com.carcrashsimulator.fxUtils.*;
import com.carcrashsimulator.models.*;
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
    public Button btnPause;
    public Button btnRestart;
    public Label labelScore;
    public Label labelGameStatus;
    public AnchorPane anchorTheBase;

    private int score;
    private int scoreDecimator;
    private int carIds;
    private int switchCountdown;
    private MapFactory mapFactory;
    @Getter
    private IntersectionTemplate map;
    private GameStatus gameStatus;
    private MoveOutDirection carMoveOutDirection;
    private List<Button> buttonsPlayingAnimations;
    private List<Car> cars;
    @Getter
    private List<Car> carsInIntersection;
    private Map<Directions, Lane> exitLanes;

    Timeline gameFrames;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonsPlayingAnimations = new ArrayList<>();
        cars = new ArrayList<>();
        carsInIntersection = new ArrayList<>();
        mapFactory = new MapFactory();
        exitLanes = new HashMap<>();

        gameFrames = new Timeline(new KeyFrame(Duration.millis(1000 / 60.), e -> runTick()));
        gameFrames.setCycleCount(Timeline.INDEFINITE);

        restartGame();
    }

    public int getAndIncrementLatestCarId() {
        return ++ carIds;
    }

    public void addLanesByTrafficLights(List<String> trLightIds, List<LaneDirection> laneDirections) {
        for (String direction : trLightIds) {
            if (map.getTrafficLights().containsKey(direction)) {
                map.getLanes().put(direction, new Lane(Directions.fromLightId(direction), new HashSet<>(laneDirections),
                        map.getStopLines().get(new Pair<>(Directions.fromLightId(direction), laneDirections.getFirst())),
                        map.getTrafficLights().get(direction).getStyleClass().getLast()));
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
                        map.carSpawns(cars.getLast());
                    }
                }
                carsExit();
                for (Directions direction : exitLanes.keySet()) {
                    map.getExitLabels().get(direction).setText(exitLanes.get(direction).getCarsInLane().size() + "");
                }
                for (String lightId : map.getTrafficLights().keySet()) {
                    map.getTrafficLights().get(lightId).setText(map.getLanes().get(lightId).getCarsInLane().size() + "");
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
                    gameFrames.stop();
                }
                for (Car car : lane.getCarsInLane()) {
                    if (lane.getCarsInLane().indexOf(car) <= 4 && ! car.isVisible()) {
                        car.makeCarVisible(this);
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
            if(score==120){
                gameStatus = GameStatus.OVER;
                labelGameStatus.setText("Game Won!");
            }
        }
    }

    public void changeColourToRed(Button btn) {
        Timeline tl = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> {
                    btn.getStyleClass().remove(2);
                    btn.getStyleClass().add("darker-green-light");
                }),
                new KeyFrame(Duration.seconds(1), e -> {
                    btn.getStyleClass().remove(2);
                    btn.getStyleClass().add("green-light");
                }),
                new KeyFrame(Duration.seconds(1.5), e -> {
                    btn.getStyleClass().remove(2);
                    btn.getStyleClass().add("darker-green-light");
                }),
                new KeyFrame(Duration.seconds(2), e -> {
                    btn.getStyleClass().remove(2);
                    btn.getStyleClass().add("green-light");
                }),
                new KeyFrame(Duration.seconds(2.5), e -> {
                    btn.getStyleClass().remove(2);
                    btn.getStyleClass().add("darker-green-light");
                }),
                new KeyFrame(Duration.seconds(3), e -> {
                    btn.getStyleClass().remove(2);
                    btn.getStyleClass().add("yellow-light");
                }),
                new KeyFrame(Duration.seconds(5), e -> {
                    btn.getStyleClass().remove(2);
                    btn.getStyleClass().add("red-light");
                    map.getLanes().get(btn.getId()).changeLaneState(btn.getStyleClass().getLast());
                    Lane lane = map.getLanes().get(btn.getId());
                    for (Car car : lane.getCarsInLane()) {
                        lane.laneCheck(car);
                    }
                })
        );
        tl.play();
    }

    public void changeColourToGreen(Button btn) {
        Timeline tl = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> {
                    btn.getStyleClass().remove(2);
                    btn.getStyleClass().add("yellow-light");
                }),
                new KeyFrame(Duration.seconds(2), e -> {
                    btn.getStyleClass().remove(2);
                    btn.getStyleClass().add("green-light");
                    Lane lane = map.getLanes().get(btn.getId());
                    lane.changeLaneState(btn.getStyleClass().getLast());
                    if (! lane.getCarsInLane().isEmpty()) {
                        lane.getCarsInLane().getFirst().startCar();
                    }
                })
        );
        tl.play();
    }

    private void carsExit() {
        Car car;
        switch (carMoveOutDirection) {
            case HORIZONTAL:
                if (! exitLanes.get(Directions.E).getCarsInLane().isEmpty()) {
                    car = exitLanes.get(Directions.E).getCarsInLane().getFirst();
                    anchorTheBase.getChildren().remove(car.getSprite());
                    exitLanes.get(Directions.E).getCarsInLane().remove(car);
                }
                if (! exitLanes.get(Directions.W).getCarsInLane().isEmpty()) {
                    car = exitLanes.get(Directions.W).getCarsInLane().getFirst();
                    anchorTheBase.getChildren().remove(car.getSprite());
                    exitLanes.get(Directions.W).getCarsInLane().remove(car);
                }
                break;
            case VERTICAL:
                if (! exitLanes.get(Directions.N).getCarsInLane().isEmpty()) {
                    car = exitLanes.get(Directions.N).getCarsInLane().getFirst();
                    anchorTheBase.getChildren().remove(car.getSprite());
                    exitLanes.get(Directions.N).getCarsInLane().remove(car);
                }
                if (! exitLanes.get(Directions.S).getCarsInLane().isEmpty()) {
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
        if (! buttonsPlayingAnimations.contains(caller) && gameStatus == GameStatus.RUNNING) {
            buttonsPlayingAnimations.add(caller);
            switch (caller.getStyleClass().get(2)) {
                case "green-light":
                    changeColourToRed(caller);
                    break;
                case "red-light":
                    changeColourToGreen(caller);
                    break;
            }
            buttonsPlayingAnimations.remove(caller);
        }
    }

    public void pauseTrafficAction() {
        if (gameStatus == GameStatus.PAUSED) {
            btnPause.setText("Pause");
            gameStatus = GameStatus.RUNNING;
            gameFrames.play();
            for (Car car : cars) {
                car.startCar();
            }
            labelGameStatus.setText("Running");
        } else if (gameStatus == GameStatus.RUNNING) {
            btnPause.setText("Resume");
            gameStatus = GameStatus.PAUSED;
            gameFrames.pause();
            for (Car car : cars) {
                car.pauseCar();
            }
        }
    }

    public void restartGame() {
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
        for (Label label : map.getExitLabels().values()) {
            anchorTheBase.getChildren().remove(label);
        }
        for (Label label : map.getExitLabels().values()) {
            anchorTheBase.getChildren().add(label);
        }
        anchorTheBase.getChildren().addAll(map.getTrafficLights().values());
        gameStatus = GameStatus.PAUSED;
        labelGameStatus.setText("Stopped");
        btnPause.setText("Resume");
    }

}
