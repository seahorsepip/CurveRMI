package com.seapip.thomas.curvermi.lobbyserver;

import com.seapip.thomas.curvermi.shared.ILobbyService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class LobbyService extends UnicastRemoteObject implements ILobbyService {

    protected LobbyService() throws RemoteException {
    }

    @Override
    public List<Lobby> get() throws RemoteException {
        return Main.lobbies;
    }

    @Override
    public void create(String id) throws RemoteException, NotBoundException {
        getLobby(id);
    }

    @Override
    public void join(String id) throws RemoteException {
        getLobby(id).join();
    }

    @Override
    public void leave(String id) throws RemoteException {
        getLobby(id).leave();
    }

    @Override
    public void start(String id) throws RemoteException {
        getLobby(id).start();
    }

    private Lobby getLobby(String id) throws RemoteException {
        synchronized (Main.lobbies) {
            for (Lobby lobby : Main.lobbies) {
                if (lobby.getId().equals(id)) {
                    return lobby;
                }
            }
        }
        Lobby lobby = new Lobby(id);
        Main.lobbies.add(lobby);
        return lobby;
    }
}
