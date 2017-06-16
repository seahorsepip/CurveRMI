package com.seapip.thomas.curve_rmi;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Date;

public class Game {
    private int width;
    private int height;
    private Snake snake;
    private ArrayList<Snake> snakes;
    private Date date;

    public Game() {
        width = 400;
        height = 400;
        snake = new Snake(new Point(200, 320));
        snakes = new ArrayList<>();
        date = new Date();
    }

    public void turn(Direction direction) {
        snake.turn(direction);
    }

    public void draw(GraphicsContext context) {
        context.clearRect(0, 0, width, height);
        context.setFill(Color.BLACK);
        context.fillRect(0, 0, width, height);
        snake.draw(context, date);
        for (Snake snake : snakes) {
            snake.draw(context, date);
        }
    }
}
