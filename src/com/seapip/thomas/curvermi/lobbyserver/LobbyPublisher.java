package com.seapip.thomas.curvermi.lobbyserver;

import com.seapip.thomas.curvermi.shared.ILobbyPublisher;
import com.seapip.thomas.curvermi.shared.Lobby;
import com.seapip.thomas.curvermi.shared.NotLoggedInException;

import java.math.BigInteger;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static com.seapip.thomas.curvermi.lobbyserver.Main.lobbies;

public class LobbyPublisher extends UnicastRemoteObject implements ILobbyPublisher {

    private SecureRandom random = new SecureRandom();

    protected LobbyPublisher() throws RemoteException {
    }

    @Override
    public List<Lobby> get() throws RemoteException {
        List<Lobby> values = new ArrayList<>();
        for (LobbyService service : lobbies.values()) {
            values.add(service.getLobby());
        }
        return values;
    }

    @Override
    public String create(String userToken, String name, String password) throws RemoteException, NotBoundException, NotLoggedInException {
        String token = new BigInteger(130, random).toString(32);
        LobbyService lobbyService = new LobbyService(token, userToken, name, password);
        lobbyService.join(userToken, password);
        lobbies.put(token, lobbyService);
        System.out.println("Lobby created: " + name);
        return token;
    }

    @Override
    public void join(String lobbyToken, String userToken, String password) throws RemoteException {
        if (lobbies.containsKey(lobbyToken)) {
            lobbies.get(lobbyToken).join(userToken, password);
        }
    }

    @Override
    public void leave(String lobbyToken, String userToken) throws RemoteException, NotBoundException {
        if (lobbies.containsKey(lobbyToken)) {
            lobbies.get(lobbyToken).leave(userToken);
        }
    }

    @Override
    public void start(String lobbyToken, String userToken) throws RemoteException, NotBoundException {
        if (lobbies.containsKey(lobbyToken)) {
            System.out.println("Lobby started!");
            lobbies.get(lobbyToken).start(userToken);
        }
    }

    @Override
    public void kick(String lobbyToken, String userToken, int userId) throws RemoteException {
        if (lobbies.containsKey(lobbyToken)) {
            lobbies.get(lobbyToken).kick(userToken, userId);
        }
    }
}
