package com.seapip.thomas.curvermi.gameserver;

import com.seapip.thomas.curvermi.shared.*;
import com.seapip.thomas.curvermi.shared.fontyspublisher.RemotePublisher;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.seapip.thomas.curvermi.gameserver.Main.userPublisher;

class GameService {
    private boolean started;
    private int width = 400;
    private int height = 400;
    private HashMap<String, Player> players = new HashMap<>();
    private RemotePublisher publisher = new RemotePublisher();

    GameService(String token) throws RemoteException {
        publisher.registerProperty("players");
        publisher.registerProperty("player");
        publisher.registerProperty("turn");
        LocateRegistry.getRegistry().rebind(token, publisher);
        System.out.println("Created");
    }

    void start() {
        System.out.println("Started!");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println("Actually started!");
                    started = true;
                    Date date = new Date();
                    for (Player player : players.values()) {
                        player.getSnake().start(date);
                    }
                    publisher.inform("players", null, new ArrayList<>(players.values()));
                } catch (RemoteException ignored) {
                    //Ignore
                }
            }
        }, 5000);
    }

    void connect(String userToken) throws RemoteException {
        if (!started) {
            User user = userPublisher.get(userToken);
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
            players.get(userToken).disconnect();
        }
    }

    void turn(String userToken, Direction direction) throws RemoteException {
        if (started && userPublisher.get(userToken) != null) {
            Player player = players.get(userToken);
            Turn turn = new Turn(player.getUser(), player.getSnake().turn(direction));
            publisher.inform("turn", null, turn);
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
