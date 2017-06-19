package com.seapip.thomas.curvermi.shared;

import java.io.Serializable;
import java.util.Date;

public class Turn implements Serializable {
    private User user;
    private Curve curve;

    public Turn(User user, Curve curve) {
        this.user = user;
        this.curve = curve;
    }

    public User getUser() {
        return user;
    }

    public Curve getCurve() {
        return curve;
    }
}
