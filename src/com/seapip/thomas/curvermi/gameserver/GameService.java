package com.seapip.thomas.curvermi.gameserver;

import com.seapip.thomas.curvermi.shared.Direction;
import com.seapip.thomas.curvermi.shared.IGameService;

import java.math.BigInteger;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;

import static com.seapip.thomas.curvermi.gameserver.Main.games;
import static com.seapip.thomas.curvermi.gameserver.Main.users;

public class GameService extends UnicastRemoteObject implements IGameService {

    private SecureRandom random = new SecureRandom();

    protected GameService() throws RemoteException {
    }

    @Override
    public String create(String userToken) throws RemoteException, NotBoundException {
        if (users.containsKey(userToken)) {
            String token = new BigInteger(130, random).toString(32);
            Game game = new Game(token);
            games.put(token, game);
            return token;

        }
        return null;
    }

    @Override
    public void start(String gameToken) throws RemoteException {
        if(games.containsKey(gameToken)) {
            games.get(gameToken).start();
        }
    }

    @Override
    public void connect(String gameToken, String userToken) throws RemoteException {
        if (games.containsKey(gameToken) && users.containsKey(userToken)) {
            games.get(gameToken).connect(userToken);
        }
    }

    @Override
    public void disconnect(String gameToken, String userToken) throws RemoteException {
        if (games.containsKey(gameToken) && users.containsKey(userToken)) {
            games.get(gameToken).disconnect(userToken);
        }
    }

    @Override
    public void turn(String gameToken, String userToken, Direction direction) throws RemoteException {
        if (games.containsKey(gameToken) && users.containsKey(userToken)) {
            games.get(gameToken).turn(userToken, direction);
        }
    }
}
