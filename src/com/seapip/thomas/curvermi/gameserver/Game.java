package com.seapip.thomas.curvermi.gameserver;

import com.seapip.thomas.curvermi.shared.Direction;
import com.seapip.thomas.curvermi.shared.Point;
import com.seapip.thomas.curvermi.shared.Snake;
import com.seapip.thomas.curvermi.shared.fontyspublisher.RemotePublisher;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

class Game {
    private String id;
    private ArrayList<Snake> snakes = new ArrayList<>();
    private RemotePublisher publisher = new RemotePublisher();
    private SecureRandom random = new SecureRandom();
    private Timer timer = new Timer();


    Game(String id) throws RemoteException {
        this.id = id;
        publisher.registerProperty("snakes");
        LocateRegistry.getRegistry(1099).rebind(id, publisher);
    }

    String connect() {
        String sessionId = new BigInteger(130, random).toString(32);
        Snake snake = new Snake(sessionId, new Point(
                ThreadLocalRandom.current().nextInt(50, 351),
                ThreadLocalRandom.current().nextInt(50, 351)
        ));
        snakes.add(snake);
        new Thread(() -> {
            try {
                Thread.sleep(100);
                updateClients();
            } catch (RemoteException | InterruptedException ignored) {
                //Ignore
            }
        }).start();
        return sessionId;
    }

    String getId() {
        return id;
    }

    void turn(String sessionId, Direction direction) throws RemoteException {
        for (Snake snake : snakes) {
            if (snake.ofSession(sessionId)) {
                snake.turn(direction);
                calculateFutureOutcome();
                updateClients();
                break;
            }
        }
    }

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

    private void updateClients() throws RemoteException {
        publisher.inform("snakes", null, snakes);
    }
}
