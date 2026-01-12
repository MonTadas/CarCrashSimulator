package com.carcrashsimulator.models;

import com.carcrashsimulator.fxControllers.GameMap;
import com.carcrashsimulator.fxUtils.CarSpawn;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

public class CarSpawnTest {
    GameMap map = new GameMap();
    CarSpawn spawn = new CarSpawn(Directions.S, Map.of(LaneDirection.F, Directions.N), new MoveTo(415 + 15, 805),
            new LineTo(415 + 15, 440), Map.of(LaneDirection.F, new LineTo(415 + 15, 120)), Map.of(LaneDirection.F,
            new LineTo(415 + 15, - 30)), Map.of(LaneDirection.F, 1000), List.of(Color.AQUA));

    /**
     * Utility method for Car creation
     * @return
     */
    public Car SpawnCar(){
        return spawn.SpawnCar(map, new Pair<>(Directions.S, LaneDirection.F));
    }
    @Test
    void carSpawnsCorrectly(){
        Car testCar = SpawnCar();
        assertEquals(1, testCar.getId());
        assertEquals(new Pair<>(Directions.S, LaneDirection.F), testCar.getStartingLane());
        assertEquals(Directions.N, testCar.getDirectionOut());
        assertNull(testCar.getState());
    }

    @Test
    void wrongExitLaneProvided(){
        assertThrows(IllegalArgumentException.class, () -> {
            spawn.SpawnCar(map, new Pair<>(Directions.S, LaneDirection.L));
        });
    }
}
