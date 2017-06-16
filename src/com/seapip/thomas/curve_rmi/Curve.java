package com.seapip.thomas.curve_rmi;

import java.util.Date;

public class Curve {
    private Direction direction;
    private Date date;

    public Curve(Direction direction, Date date) {
        this.direction = direction;
        this.date = date;
    }

    public Direction getDirection() {
        return direction;
    }

    public Date getDate() {
        return date;
    }
}
