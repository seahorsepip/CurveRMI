package com.seapip.thomas.curvermi.lobbyserver;

import com.seapip.thomas.curvermi.shared.ILobbyService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    static final List<Lobby> lobbies = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(1099);
        ILobbyService service = new LobbyService();
        registry.rebind("LobbyService", service);
    }
}
