package com.seapip.thomas.curvermi.shared;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGamePublisher extends Remote {

    String create(String userToken) throws RemoteException, NotBoundException;

    void start(String gameToken) throws RemoteException;

    void connect(String gameToken, String userToken) throws RemoteException;

    void disconnect(String gameToken, String userToken) throws RemoteException;

    void turn(String gameToken, String userToken, Direction direction) throws RemoteException;
}
