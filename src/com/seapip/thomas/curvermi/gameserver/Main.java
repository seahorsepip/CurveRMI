package com.seapip.thomas.curvermi.gameserver;

import com.seapip.thomas.curvermi.shared.User;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;

public class Main {
    static final HashMap<String, Game> games = new HashMap<>();
    static final HashMap<String, User> users = new HashMap<>();

    static GameService gameService;
    static UserService userService;

    public static void main(String[] args) throws RemoteException {
        gameService = new GameService();
        userService = new UserService();
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("GameService", gameService);
        registry.rebind("UserService", userService);
    }
}
