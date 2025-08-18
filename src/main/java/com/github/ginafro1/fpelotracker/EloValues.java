package com.github.ginafro1.fpelotracker;

public enum EloValues {

    KILL(2),
    FKILL(4),
    FDEATH(-4),
    BEDLOSE(-5),
    BEDBREAK(6),
    WIN(10),
    LOSE(-10);

    public final int eloValue;
    EloValues(int eloV){
        this.eloValue = eloV;
    }
}
