package com.seapip.thomas.curvermi.shared;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IService extends Remote {
    String connect(String id) throws RemoteException, NotBoundException;

    void turn(String id, String sessionId, Direction direction) throws RemoteException;
}
