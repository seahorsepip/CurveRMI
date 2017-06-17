package com.seapip.thomas.curvermi.client;

import com.seapip.thomas.curvermi.shared.Direction;
import com.seapip.thomas.curvermi.shared.IService;
import com.seapip.thomas.curvermi.shared.Snake;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private String sessionId;
    private IService service;
    private ArrayList<Snake> snakes;
    @FXML
    private Canvas canvas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            service = (IService) registry.lookup("Service");
            sessionId = service.connect("Example");
            new ServiceListener(value -> snakes = value);
        } catch (RemoteException | NotBoundException ignored) {
            //Ignore
        }
        (new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw(canvas.getGraphicsContext2D());
            }
        }).start();
    }

    private void draw(GraphicsContext context) {
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        context.setFill(Color.BLACK);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (snakes != null) {
            for (Snake snake : snakes) {
                snake.draw(canvas.getGraphicsContext2D());
            }
        }
    }

    @FXML
    public void onKeyPressed(KeyEvent event) throws RemoteException {
        Direction direction;
        switch (event.getCode()) {
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
            default:
                //Ignore event
                return;
        }
        event.consume();
        service.turn("Example", sessionId, direction);
    }
}
