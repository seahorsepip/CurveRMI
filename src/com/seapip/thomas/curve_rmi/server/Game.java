package com.seapip.thomas.curve_rmi.server;

import com.seapip.thomas.curve_rmi.client.Direction;
import com.seapip.thomas.curve_rmi.client.Point;
import com.seapip.thomas.curve_rmi.client.Snake;
import com.seapip.thomas.curve_rmi.fontyspublisher.RemotePublisher;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;

public class Game {
    private String id;
    private int width;
    private int height;
    private ArrayList<Snake> snakes;
    private Date date;
    private RemotePublisher publisher;
    private Registry registry;
    private SecureRandom random = new SecureRandom();


    public Game(String id) throws RemoteException {
        width = 400;
        height = 400;
        snakes = new ArrayList<>();
        date = new Date();
        this.id = id;
        publisher = new RemotePublisher();
        publisher.registerProperty("snakes");
        registry = LocateRegistry.getRegistry(1099);
        registry.rebind("snakePublisher", publisher);
    }

    public String connect() throws RemoteException {
        String sessionId = new BigInteger(130, random).toString(32);
        Snake snake = new Snake(sessionId, new Point(300, 300));
        snakes.add(snake);
        updateClients();
        return sessionId;
    }

    public String getId() {
        return id;
    }

    public void turn(String sessionId, Direction direction) throws RemoteException {
        for (Snake snake : snakes) {
            if (snake.ofSession(sessionId)) {
                snake.turn(direction);
                updateClients();
                break;
            }
        }
    }

    public void updateClients() throws RemoteException {
        publisher.inform("snakes", null, snakes);
    }
}
