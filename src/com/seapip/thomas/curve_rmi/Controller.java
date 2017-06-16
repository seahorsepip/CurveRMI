package com.seapip.thomas.curve_rmi;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private Game game;

    @FXML
    Canvas canvas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.game = new Game();

        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        (new AnimationTimer() {
            @Override
            public void handle(long now) {
                game.draw(canvas.getGraphicsContext2D());
            }
        }).start();
    }

    @FXML
    public void onKeyPressed(KeyEvent event) {
        event.consume();
        Direction direction;
        switch (event.getCode()) {
            default:
            case UP:
                direction = Direction.UP;
                break;
            case LEFT:
                direction = Direction.LEFT;
                break;
            case DOWN:
                direction = Direction.DOWN;
                break;
            case RIGHT:
                direction = Direction.RIGHT;
                break;
        }
        game.turn(direction);
    }
}
