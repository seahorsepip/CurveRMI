package com.seapip.thomas.curvermi.shared;

import java.io.Serializable;
import java.util.Date;

public class Player implements Serializable {
    private User user;
    private Snake snake;
    private boolean connected = true;

    public Player(User user, Snake snake) {
        this.user = user;
        this.snake = snake;
    }

    public User getUser() {
        return user;
    }

    public Snake getSnake() {
        return snake;
    }

    public void disconnect() {
        snake.setLost(new Date().getTime());
        connected = false;
    }
}
