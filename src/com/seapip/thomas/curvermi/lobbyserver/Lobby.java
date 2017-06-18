package com.seapip.thomas.curvermi.lobbyserver;

import com.seapip.thomas.curvermi.shared.fontyspublisher.RemotePublisher;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Lobby {
    private String id;
    private int userCount;
    private RemotePublisher publisher = new RemotePublisher();

    Lobby(String id) throws RemoteException {
        this.id = id;
        publisher.registerProperty("users");
        publisher.registerProperty("started");
        LocateRegistry.getRegistry(1099).rebind(id, publisher);
    }

    String getId() {
        return id;
    }

    void join() throws RemoteException {
        userCount++;
        publisher.inform("users", null, userCount);
    }

    void leave() throws RemoteException {
        userCount--;
        publisher.inform("users", null, userCount);
    }

    void start() throws RemoteException {
        publisher.inform("started", null, true);
    }
}
