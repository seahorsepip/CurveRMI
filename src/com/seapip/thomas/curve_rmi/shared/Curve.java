package com.seapip.thomas.curve_rmi.shared;

import java.io.Serializable;
import java.util.Date;

public class Curve implements Serializable {
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
