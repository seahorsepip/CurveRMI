package com.seapip.thomas.curvermi.client;

import com.seapip.thomas.curvermi.shared.Player;
import com.seapip.thomas.curvermi.shared.Turn;
import com.seapip.thomas.curvermi.shared.User;
import com.seapip.thomas.curvermi.shared.fontyspublisher.IRemotePropertyListener;
import com.seapip.thomas.curvermi.shared.fontyspublisher.IRemotePublisherForListener;

import java.beans.PropertyChangeEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class LobbyListener extends UnicastRemoteObject implements IRemotePropertyListener {

    private Callback callback;

    protected LobbyListener(String lobbyToken, String userToken, Callback callback) throws RemoteException, NotBoundException {
        this.callback = callback;
        Registry registry = LocateRegistry.getRegistry();
        IRemotePublisherForListener publisher = (IRemotePublisherForListener) registry.lookup(lobbyToken);
        publisher.subscribeRemoteListener(this, "host");
        publisher.subscribeRemoteListener(this, "users");
        publisher.subscribeRemoteListener(this, "gameToken");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException {
        Object value = evt.getNewValue();
        if (value != null) {
            switch (evt.getPropertyName()) {
                case "host":
                    callback.onHost((User) value);
                    break;
                case "users":
                    callback.onUsers((List<User>) value);
                    break;
                case "gameToken":
                    callback.onGameToken((String) value);
                    break;
            }
        }
    }

    interface Callback {
        void onHost(User value);

        void onUsers(List<User> values);

        void onGameToken(String value);
    }
}
