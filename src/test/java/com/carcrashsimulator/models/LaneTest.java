package com.carcrashsimulator.models;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LaneTest {
    Lane lane = new Lane(Directions.S, Set.of(LaneDirection.F), new Point2D(415 + 15, 455), "red-light");
    CarSpawnTest carSpawnTest = new CarSpawnTest();
    Car testCar = carSpawnTest.SpawnCar(), testCar2 = carSpawnTest.SpawnCar();

    @BeforeAll
    void setupThing(){
        lane.getCarsInLane().addAll(List.of(testCar, testCar2));
    }

    @Test
    @Order(8)
    void testCarLeavesCarNotInList() {
        assertFalse(lane.carLeaves(testCar));
    }

    @Test
    @Order(7)
    void testCarLeavesCarInList() {
        assertTrue(lane.carLeaves(testCar));
    }

    @Test
    @Order(1)
    void testChangeLaneStateIllegalColourProvided() {
        assertThrows(IllegalArgumentException.class, () -> {
            lane.changeLaneState("blue-light");
        });
    }
    @Test
    @Order(2)
    void testChangeLaneStateWhenLightIsRedOrYellow(){
        lane.changeLaneState("yellow-light");
        assertEquals(CarState.WAITING, lane.getLaneState());
    }
    @Test
    @Order(3)
    void testChangeLaneStateWhenLightIsGreen(){
        lane.changeLaneState("green-light");
        assertEquals(CarState.DRIVING, lane.getLaneState());
    }

    /*
    Proximity testing
    Spawn point: (430, 805)
    Line: (430, 455)
    Moved to: (430, 460)
    */
    @Test
    @Order(4)
    void testProximityToLine() {
        lane.changeLaneState("red-light");
        testCar.changeStateToDriving();
        testCar.getSprite().setTranslateX(430);
        testCar.getSprite().setTranslateY(450);
        lane.laneCheck(testCar);
        assertEquals(CarState.WAITING, testCar.getState());
    }
    @Test
    @Order(5)
    void testProximityToCarFarAway(){
        testCar2.changeStateToDriving();
        lane.laneCheck(testCar2);
        assertEquals(CarState.DRIVING, testCar2.getState());
    }
    @Test
    @Order(6)
    void testProximityToCarClose(){
        testCar2.getSprite().setTranslateX(430);
        testCar2.getSprite().setTranslateY(500);
        lane.laneCheck(testCar2);
        assertEquals(CarState.DRIVING, testCar2.getState());
    }
}