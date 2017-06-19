package com.seapip.thomas.curvermi.gameserver;

import com.seapip.thomas.curvermi.shared.IUserPublisher;
import com.seapip.thomas.curvermi.shared.User;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;

import static com.seapip.thomas.curvermi.gameserver.Main.users;

public class UserPublisher extends UnicastRemoteObject implements IUserPublisher {

    private SecureRandom random = new SecureRandom();

    protected UserPublisher() throws RemoteException {
    }

    public String login(String username, String password) {
        String token = new BigInteger(130, random).toString(32);
        users.put(token, new User(username));
        return token;
    }

    public void logout(String token) {
        users.remove(token);
    }

    public void Register(String username, String password) {

    }

    public User get(String token) {
        if (users.containsKey(token)) {
            return users.get(token);
        }
        return null;
    }
}
