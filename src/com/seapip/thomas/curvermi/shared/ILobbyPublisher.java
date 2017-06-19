package com.seapip.thomas.curvermi.shared;

import com.seapip.thomas.curvermi.lobbyserver.LobbyService;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ILobbyPublisher extends Remote {
    List<Lobby> get() throws RemoteException;

    String create(String userToken, String name, String password) throws RemoteException, NotBoundException, NotLoggedInException;

    void join(String lobbyToken, String userToken, String password) throws RemoteException;

    void leave(String lobbyToken, String userToken) throws RemoteException, NotBoundException;

    void start(String lobbyToken, String userToken) throws RemoteException, NotBoundException;

    void kick(String lobbyToken, String userToken, String username) throws RemoteException;
}
