package com.seapip.thomas.curve_rmi.client;

import com.seapip.thomas.curve_rmi.server.IService;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;

public class Game {
    Registry registry;
    private int width;
    private int height;
    private Snake snake;
    private ArrayList<Snake> snakes;
    private Date date;
    private IService game;

    public Game() throws RemoteException, NotBoundException {
        width = 400;
        height = 400;
        snakes = new ArrayList<>();
        date = new Date();
        registry = LocateRegistry.getRegistry("localhost", 1099);
        game = (IService) registry.lookup("Service");
        //snake = game.connect();

    }

    public void turn(Direction direction) throws RemoteException {
        //game.turn(direction);
        //snake.turn(direction);
    }

    public void draw(GraphicsContext context) {
        context.clearRect(0, 0, width, height);
        context.setFill(Color.BLACK);
        context.fillRect(0, 0, width, height);
        snake.draw(context);
        for (Snake snake : snakes) {
            snake.draw(context);
        }
    }
}
