package com.seapip.thomas.curvermi.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IUserService extends Remote {

    String login(String username, String password) throws RemoteException;

    void logout(String token) throws RemoteException;

    void Register(String username, String password) throws RemoteException;

    User get(String token) throws RemoteException;
}
