package com.seapip.thomas.curve_rmi.server;

import com.seapip.thomas.curve_rmi.client.Direction;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Service extends UnicastRemoteObject implements IService {

    protected Service() throws RemoteException {
    }

    @Override
    public String connect(String id) throws RemoteException, NotBoundException {
        Game game = getGame(id);
        return game.connect();
    }

    @Override
    public void turn(String id, String sessionId, Direction direction) throws RemoteException {
        System.out.println("Server turn" + direction.name());
        getGame(id).turn(sessionId, direction);
    }

    private Game getGame(String id) throws RemoteException {
        synchronized (Main.games) {
            for (Game game : Main.games) {
                if (game.getId().equals(id)) {
                    return game;
                }
            }
        }
        Game game = new Game(id);
        Main.games.add(game);
        return game;
    }
}
