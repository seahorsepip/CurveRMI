package com.seapip.thomas.curvermi.server;

import com.seapip.thomas.curvermi.shared.IService;

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
        IService service = new Service();
        registry.rebind("Service", service);
    }
}
