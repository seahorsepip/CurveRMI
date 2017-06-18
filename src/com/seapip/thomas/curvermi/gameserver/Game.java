package com.seapip.thomas.curvermi.gameserver;

import com.seapip.thomas.curvermi.shared.*;
import com.seapip.thomas.curvermi.shared.fontyspublisher.RemotePublisher;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import static com.seapip.thomas.curvermi.gameserver.Main.userService;

class Game {
    private boolean started;
    private int width = 400;
    private int height = 400;
    private HashMap<String, Player> players = new HashMap<>();
    private RemotePublisher publisher = new RemotePublisher();

    Game(String token) throws RemoteException {
        publisher.registerProperty("data");
        LocateRegistry.getRegistry(1099).rebind(token, publisher);
    }

    void start() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    started = true;
                    Date date = new Date();
                    for (Player player : players.values()) {
                        player.getSnake().start(date);
                    }
                    publisher.inform("data", null, players.values());
                } catch (RemoteException ignored) {
                    //Ignore
                }
            }
        }, 5000);
    }

    void connect(String userToken) throws RemoteException {
        if (!started) {
            User user = userService.get(userToken);
            if (user != null) {
                players.put(userToken, new Player(user, new Snake(new Point(
                        ThreadLocalRandom.current().nextInt(width / 4, width / 4 * 3),
                        ThreadLocalRandom.current().nextInt(height / 4, height / 4 * 3)
                ))));
            }
        }
    }

    void disconnect(String userToken) throws RemoteException {
        if (players.containsKey(userToken)) {
            //players.get(userToken).getSnake().disconnect();
        }
    }

    void turn(String userToken, Direction direction) throws RemoteException {
        if (started && players.containsKey(userToken)) {
            players.get(userToken).getSnake().turn(direction);
            publisher.inform("data", null, players.values());
        }
    }

    /*
    private void calculateFutureOutcome() {
        timer.cancel();
        timer = new Timer();
        for (Snake snakeA : snakes) {
            for (Snake snakeB : snakes) {
                if (snakeA != snakeB && snakeA.getLost() == 0) {
                    long timeA = snakeA.intersectsInFuture(snakeB);
                    if (timeA > new Date().getTime()) {
                        long timeB = snakeB.intersectsInFuture(snakeA);
                        if (timeA > timeB) {
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    snakeA.setLost(timeA);
                                    try {
                                        updateClients();
                                    } catch (RemoteException ignored) {
                                        //Ignore
                                    }
                                }
                            }, timeA - new Date().getTime());
                        }
                    }
                }
            }
        }
    }
    */
}
