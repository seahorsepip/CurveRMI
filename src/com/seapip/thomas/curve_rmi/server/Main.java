package com.seapip.thomas.curve_rmi.server;

import com.seapip.thomas.curve_rmi.shared.IService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static final List<Game> games = Collections.synchronizedList(new ArrayList<>());
    private static Registry registry;

    public static void main(String[] args) throws RemoteException {
        registry = LocateRegistry.createRegistry(1099);
        IService service = new Service();
        registry.rebind("Service", service);
    }
}
