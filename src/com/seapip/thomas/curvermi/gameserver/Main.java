package com.seapip.thomas.curvermi.gameserver;

import com.seapip.thomas.curvermi.shared.IGameService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    static final List<Game> games = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(1099);
        IGameService service = new GameService();
        registry.rebind("GameService", service);
    }
}
