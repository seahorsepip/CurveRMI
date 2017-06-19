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
    private IUserService userService;
    private IGameService gameService;

    LobbyService(String token, String userToken, String name, String password)
            throws RemoteException, NotBoundException, NotLoggedInException {
        this.token = token;
        this.name = name;
        this.password = password;
        Registry registry = LocateRegistry.getRegistry(1099);
        publisher.registerProperty("host");
        publisher.registerProperty("users");
        publisher.registerProperty("gameToken");
        registry.rebind(token, publisher);
        userService = (IUserService) registry.lookup("UserService");
        gameService = (IGameService) registry.lookup("GameService");
        this.host = userService.get(userToken);
        if (this.host == null) {
            destroy();
            throw new NotLoggedInException();
        }
    }

    Lobby getLobby() {
        return new Lobby(token, name, hasPassword(), host, new ArrayList<>(users.values()));
    }

    void join(String userToken, String password) throws RemoteException {
        if (validatePassword(password)) {
            User user = userService.get(userToken);
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
            publisher.inform("gameToken", null, gameService.create(userToken));
        }
    }

    void kick(String userToken, String username) throws RemoteException {
        if (isHost(userToken)) {
            String[] keys = (String[]) users.keySet().toArray();
            for (String key : keys) {
                if (users.get(key).getUsername().equals(username)) {
                    users.remove(key);
                    break;
                }
            }
            publisher.inform("users", null, new ArrayList<>(users.values()));
        }
    }

    private boolean isHost(String userToken) throws RemoteException {
        User user = userService.get(userToken);
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
