package com.seapip.thomas.curvermi.gameserver;

import com.seapip.thomas.curvermi.shared.User;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;

public class Main {
    static final HashMap<String, GameService> games = new HashMap<>();
    static final HashMap<String, User> users = new HashMap<>();

    static UserPublisher userPublisher;

    public static void main(String[] args) throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("GamePublisher", new GamePublisher());
        userPublisher = new UserPublisher();
        registry.rebind("UserPublisher", userPublisher);
    }
}
