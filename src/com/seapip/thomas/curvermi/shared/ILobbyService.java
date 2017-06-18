package com.seapip.thomas.curvermi.shared;

import com.seapip.thomas.curvermi.lobbyserver.Lobby;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ILobbyService extends Remote {
    List<Lobby> get() throws RemoteException;

    void create(String id) throws RemoteException, NotBoundException;

    void join(String id) throws RemoteException;

    void leave(String id) throws RemoteException;

    void start(String id) throws RemoteException;
}
