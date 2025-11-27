package com.carcrashsimulator.models;

public enum Directions {
    N, E, S, W;

    public static Directions fromLightId(String trafficLightId){
        switch(trafficLightId.charAt(3)){
            case 'N': return N;
            case 'E': return E;
            case 'S': return S;
            case 'W': return W;
            default: throw new IllegalArgumentException();
        }
    }
}

