package com.carcrashsimulator.models;

import com.carcrashsimulator.fxControllers.GameMap;
import com.carcrashsimulator.fxUtils.GameStatus;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.util.Duration;
import lombok.Getter;

public class TrafficLight implements IReactToStatus {
    @Getter
    private Button sprite;
    private Timeline timeline;
    private boolean timelineOver;

    public TrafficLight(Button sprite) {
        this.sprite = sprite;
        this.timelineOver = true;
    }

    public String getName() {
        return sprite.getId();
    }

    public void changeColourToRed(Button btn, GameMap gameMap) {
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> {
                    timelineOver = false;
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
                    gameMap.getMap().getLanes().get(btn.getId()).changeLaneState(btn.getStyleClass().getLast());
                    Lane lane = gameMap.getMap().getLanes().get(btn.getId());
                    for (Car car : lane.getCarsInLane()) {
                        lane.laneCheck(car);
                    }
                }),
                new KeyFrame(Duration.seconds(5), e -> {
                    btn.getStyleClass().remove(2);
                    btn.getStyleClass().add("red-light");
                    timelineOver = true;
                })
        );
        timeline.play();
    }

    public void changeColourToGreen(Button btn, GameMap gameMap) {
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> {
                    timelineOver = false;
                    btn.getStyleClass().remove(2);
                    btn.getStyleClass().add("yellow-light");
                }),
                new KeyFrame(Duration.seconds(2), e -> {
                    btn.getStyleClass().remove(2);
                    btn.getStyleClass().add("green-light");
                    Lane lane = gameMap.getMap().getLanes().get(btn.getId());
                    lane.changeLaneState(btn.getStyleClass().getLast());
                    if (!lane.getCarsInLane().isEmpty()) {
                        lane.getCarsInLane().getFirst().reactToGameStatus(gameMap.getGameStatus());
                    }
                    timelineOver = true;
                })
        );
        timeline.play();
    }

    @Override
    public void reactToGameStatus(GameStatus gameStatus) {
        switch (gameStatus) {
            case RUNNING:
                if (timeline != null && !timelineOver) {
                    timeline.play();
                }
                break;
            case PAUSED:
                if (timeline != null && !timelineOver) {
                    timeline.pause();
                }
                break;
            case OVER:
                if (timeline != null && !timelineOver) {
                    timeline.stop();
                }
                break;
        }
    }
}
