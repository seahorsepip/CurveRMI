package com.seapip.thomas.curvermi.gameserver;

import com.seapip.thomas.curvermi.shared.Direction;
import com.seapip.thomas.curvermi.shared.IGamePublisher;

import java.math.BigInteger;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;

import static com.seapip.thomas.curvermi.gameserver.Main.games;
import static com.seapip.thomas.curvermi.gameserver.Main.userPublisher;

public class GamePublisher extends UnicastRemoteObject implements IGamePublisher {

    private SecureRandom random = new SecureRandom();

    protected GamePublisher() throws RemoteException {
    }

    @Override
    public String create(String userToken) throws RemoteException, NotBoundException {
        System.out.println("uhh no!");
        if (userPublisher.get(userToken) != null) {
            System.out.println("Welcome!");
            String token = new BigInteger(130, random).toString(32);
            GameService gameService = new GameService(token);
            games.put(token, gameService);
            return token;

        }
        return null;
    }

    @Override
    public void start(String gameToken) throws RemoteException {
        if (games.containsKey(gameToken)) {
            games.get(gameToken).start();
        }
    }

    @Override
    public void connect(String gameToken, String userToken) throws RemoteException {
        if (games.containsKey(gameToken) && userPublisher.get(userToken) != null) {
            games.get(gameToken).connect(userToken);
        }
    }

    @Override
    public void disconnect(String gameToken, String userToken) throws RemoteException {
        if (games.containsKey(gameToken) && userPublisher.get(userToken) != null) {
            games.get(gameToken).disconnect(userToken);
        }
    }

    @Override
    public void turn(String gameToken, String userToken, Direction direction) throws RemoteException {
        if (games.containsKey(gameToken) && userPublisher.get(userToken) != null) {
            games.get(gameToken).turn(userToken, direction);
        }
    }
}
