package com.seapip.thomas.curvermi.client;

import com.seapip.thomas.curvermi.shared.Snake;
import com.seapip.thomas.curvermi.shared.fontyspublisher.IRemotePropertyListener;
import com.seapip.thomas.curvermi.shared.fontyspublisher.IRemotePublisherForListener;

import java.beans.PropertyChangeEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServiceListener extends UnicastRemoteObject implements IRemotePropertyListener {

    private transient Callback callback;

    protected ServiceListener(String id, Callback callback) throws RemoteException, NotBoundException {
        this.callback = callback;
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        IRemotePublisherForListener publisher = (IRemotePublisherForListener) registry.lookup(id);
        publisher.subscribeRemoteListener(this, "snakes");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException {
        callback.onSnakesChanged((ArrayList<Snake>) evt.getNewValue());
    }

    interface Callback {
        void onSnakesChanged(ArrayList<Snake> snakes);
    }
}
