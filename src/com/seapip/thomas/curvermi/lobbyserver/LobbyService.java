package com.seapip.thomas.curvermi.lobbyserver;

import com.seapip.thomas.curvermi.shared.*;
import com.seapip.thomas.curvermi.shared.fontyspublisher.RemotePublisher;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;

import static com.seapip.thomas.curvermi.lobbyserver.Main.lobbies;

public class LobbyService {
    private String token;
    private String name;
    private String password;
    private User host;
    private HashMap<String, User> users = new HashMap<>();
    private RemotePublisher publisher = new RemotePublisher();
    private IUserPublisher userPublisher;
    private IGamePublisher gamePublisher;

    LobbyService(String token, String userToken, String name, String password)
            throws RemoteException, NotBoundException, NotLoggedInException {
        this.token = token;
        this.name = name;
        this.password = password;
        Registry registry = LocateRegistry.getRegistry();
        publisher.registerProperty("host");
        publisher.registerProperty("users");
        publisher.registerProperty("gameToken");
        registry.rebind(token, publisher);
        userPublisher = (IUserPublisher) registry.lookup("UserPublisher");
        gamePublisher = (IGamePublisher) registry.lookup("GamePublisher");
        this.host = userPublisher.get(userToken);
        if (this.host == null) {
            destroy();
            throw new NotLoggedInException();
        }
    }

    Lobby getLobby() {
        return new Lobby(token, name, hasPassword(), host, new ArrayList<>(users.values()));
    }

    void join(String userToken, String password) throws RemoteException {
        if (validatePassword(password) && !users.containsKey(userToken) && !isHost(userToken)) {
            User user = userPublisher.get(userToken);
            if (user != null) {
                users.put(userToken, user);
                publisher.inform("users", null, new ArrayList<>(users.values()));
            }
        }
    }

    void leave(String userToken) throws RemoteException, NotBoundException {
        if (users.containsKey(userToken)) {
            users.remove(userToken);
            publisher.inform("users", null, new ArrayList<>(users.values()));
        } else if (isHost(userToken)) {
            if (users.size() > 0) {
                String firstUserKey = (String) users.keySet().toArray()[0];
                host = users.get(firstUserKey);
                users.remove(firstUserKey);
                publisher.inform("host", null, host);
                publisher.inform("users", null, new ArrayList<>(users.values()));
            } else {
                destroy();
            }
        }
    }

    void start(String userToken) throws RemoteException, NotBoundException {
        if (isHost(userToken)) {
            System.out.println("Alive???");
            String gameToken = gamePublisher.create(userToken);
            publisher.inform("gameToken", null, gameToken);
            gamePublisher.start(gameToken);
        }
    }

    void kick(String userToken, int userId) throws RemoteException {
        if (isHost(userToken)) {
            for (Object key : users.keySet().toArray()) {
                if (users.get(key).getId() == userId) {
                    users.remove(key);
                    break;
                }
            }
            publisher.inform("users", null, new ArrayList<>(users.values()));
        }
    }

    private boolean isHost(String userToken) throws RemoteException {
        User user = userPublisher.get(userToken);
        return user != null && user.getUsername().equals(host.getUsername());
    }

    private boolean hasPassword() {
        return this.password != null;
    }

    private boolean validatePassword(String password) {
        return !hasPassword() || this.password.equals(password);
    }

    private void destroy() throws RemoteException, NotBoundException {
        LocateRegistry.getRegistry(1099).unbind(token);
        lobbies.remove(token);
    }
}
