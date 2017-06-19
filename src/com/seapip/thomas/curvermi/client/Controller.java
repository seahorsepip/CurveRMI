package com.seapip.thomas.curvermi.client;

import com.seapip.thomas.curvermi.shared.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private IUserPublisher userPublisher;
    private IGamePublisher gamepublisher;
    private String userToken;
    private String gameToken;
    private List<Player> players;
    private boolean keyDown;
    @FXML
    private Canvas canvas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Registry registry = LocateRegistry.getRegistry();
            userPublisher = (IUserPublisher) registry.lookup("UserPublisher");
            //lobbyService = (ILobbyService) registry.lookup("LobbyPublisher");
            gamepublisher = (IGamePublisher) registry.lookup("GamePublisher");
            userToken = userPublisher.login("seahorsepip", "12345");
            gameToken = gamepublisher.create(userToken);
            gamepublisher.connect(gameToken, userToken);
            gamepublisher.start(gameToken);
            new GameListener(gameToken, new GameListener.Callback() {
                @Override
                public void onPlayers(List<Player> values) {
                    players = values;
                }

                @Override
                public void onPlayer(Player value) {
                    for (int i = 0; i < players.size(); i++) {
                        Player player = players.get(i);
                        if (players.get(i).getUser().getUsername().equals(value.getUser().getUsername())) {
                            players.remove(i);
                            players.add(i, value);
                        }
                    }
                }

                @Override
                public void onTurn(Turn value) {
                    for (Player player : players) {
                        if (player.getUser().getUsername().equals(value.getUser().getUsername())) {
                            player.getSnake().turn(value.getCurve().getDirection(), value.getCurve().getDate());
                        }
                    }
                }
            });
        } catch (RemoteException | NotBoundException ignored) {
            //Ignore
        }
        (new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw(canvas.getGraphicsContext2D());
            }
        }).start();
    }

    private void draw(GraphicsContext context) {
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        context.setFill(Color.BLACK);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (players != null) {
            for (Player player : players) {
                player.getSnake().draw(canvas.getGraphicsContext2D());
            }
        }
    }

    @FXML
    void onKeyPressed(KeyEvent event) throws RemoteException {
        if (!keyDown) {
            Direction direction;
            switch (event.getCode()) {
                case LEFT:
                    direction = Direction.LEFT;
                    break;
                case RIGHT:
                    direction = Direction.RIGHT;
                    break;
                default:
                    //Ignore event
                    return;
            }
            keyDown = true;
            gamepublisher.turn(gameToken, userToken, direction);
        }
    }

    @FXML
    void onKeyReleased(KeyEvent event) throws RemoteException {
        if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
            event.consume();
            gamepublisher.turn(gameToken, userToken, Direction.FORWARD);
            keyDown = false;
        }
    }
}
