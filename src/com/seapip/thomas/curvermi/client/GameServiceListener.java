package com.seapip.thomas.curvermi.client;

import com.seapip.thomas.curvermi.shared.Player;
import com.seapip.thomas.curvermi.shared.Turn;
import com.seapip.thomas.curvermi.shared.fontyspublisher.IRemotePropertyListener;
import com.seapip.thomas.curvermi.shared.fontyspublisher.IRemotePublisherForListener;

import java.beans.PropertyChangeEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class GameServiceListener extends UnicastRemoteObject implements IRemotePropertyListener {

    private transient Callback callback;

    protected GameServiceListener(String id, Callback callback) throws RemoteException, NotBoundException {
        this.callback = callback;
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        IRemotePublisherForListener publisher = (IRemotePublisherForListener) registry.lookup(id);
        publisher.subscribeRemoteListener(this, "players");
        publisher.subscribeRemoteListener(this, "player");
        publisher.subscribeRemoteListener(this, "turn");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException {
        Object value = evt.getNewValue();
        if (value != null) {
            switch (evt.getPropertyName()) {
                case "players":
                    callback.onPlayers((List<Player>) value);
                    break;
                case "player":
                    callback.onPlayer((Player) value);
                    break;
                case "turn":
                    callback.onTurn((Turn) value);
                    break;
            }
        }
    }

    interface Callback {
        void onPlayers(List<Player> values);

        void onPlayer(Player value);

        void onTurn(Turn value);
    }
}
