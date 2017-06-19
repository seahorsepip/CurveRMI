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

    private IUserService userService;
    private ILobbyService lobbyService;
    private IGameService gameService;
    private String userToken;
    private String gameToken;
    private List<Player> players;
    @FXML
    private Canvas canvas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            userService = (IUserService) registry.lookup("UserPublisher");
            lobbyService = (ILobbyService) registry.lookup("LobbyPublisher");
            gameService = (IGameService) registry.lookup("GamePublisher");
            System.out.println(userService);
            System.out.println(lobbyService);
            System.out.println(gameService);
            userToken = userService.login("seahorsepip", "12345");
            gameToken = gameService.create(userToken);
            gameService.connect(gameToken, userToken);
            gameService.start(gameToken);
            new GameServiceListener(gameToken, new GameServiceListener.Callback() {
                @Override
                public void onPlayers(List<Player> values) {
                    players = values;
                    System.out.println("New players :D");
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
                    System.out.println("Player left?");
                }

                @Override
                public void onTurn(Turn value) {
                    for (Player player : players) {
                        if (player.getUser().getUsername().equals(value.getUser().getUsername())) {
                            player.getSnake().turn(value.getCurve().getDirection(), value.getCurve().getDate());
                        }
                    }
                    System.out.println("Player changed direction?");
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
        event.consume();
        gameService.turn(gameToken, userToken, direction);
        System.out.println("Left/Right?");
    }

    @FXML
    void onKeyReleased(KeyEvent event) throws RemoteException {
        if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
            event.consume();
            gameService.turn(gameToken, userToken, Direction.FORWARD);
            System.out.println("KeyUp?");
        }
    }
}
