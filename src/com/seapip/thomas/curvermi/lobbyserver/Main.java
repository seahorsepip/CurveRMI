package com.seapip.thomas.curvermi.lobbyserver;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;

public class Main {
    static final HashMap<String, LobbyService> lobbies = new HashMap<>();

    public static void main(String[] args) throws RemoteException {
        LocateRegistry.getRegistry(1099).rebind("LobbyPublisher", new LobbyPublisher());
    }
}
