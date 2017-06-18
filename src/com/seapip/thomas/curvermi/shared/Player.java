package com.seapip.thomas.curvermi.shared;

public class Player {
    User user;
    Snake snake;

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
}
