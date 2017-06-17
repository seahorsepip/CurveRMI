package com.seapip.thomas.curve_rmi.client;

import com.seapip.thomas.curve_rmi.server.IService;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private String sessionId;
    private Registry registry;
    private IService service;
    private ArrayList<Snake> snakes;
    @FXML
    private Canvas canvas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            registry = LocateRegistry.getRegistry("localhost", 1099);
            service = (IService) registry.lookup("Service");
            sessionId = service.connect("Example");
            ServiceListener listener = new ServiceListener(value -> snakes = value);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        (new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (snakes != null) {
                    for (Snake snake : snakes) {
                        snake.draw(canvas.getGraphicsContext2D());
                    }
                }
            }
        }).start();
    }

    @FXML
    public void onKeyPressed(KeyEvent event) throws RemoteException {
        Direction direction;
        switch (event.getCode()) {
            default:
                //Ignore event
                return;
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
        event.consume();
        service.turn("Example", sessionId, direction);
    }
}
