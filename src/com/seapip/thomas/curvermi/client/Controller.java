package com.seapip.thomas.curvermi.client;

import com.seapip.thomas.curvermi.shared.*;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
    private ILobbyPublisher lobbyPublisher;
    private String userToken;
    private String gameToken;
    private String lobbyToken;
    private List<Player> players;
    private boolean keyDown;
    @FXML
    private AnchorPane loginPane;
    @FXML
    private AnchorPane lobbiesPane;
    @FXML
    private AnchorPane addLobbyPane;
    @FXML
    private AnchorPane lobbyPane;
    @FXML
    private Pane gamePane;
    @FXML
    private Canvas canvas;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ListView<Lobby> lobbiesList;
    @FXML
    private TextField lobbyNameField;
    @FXML
    private PasswordField lobbyPasswordField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginPane.setVisible(true);
        try {
            Registry registry = LocateRegistry.getRegistry();
            userPublisher = (IUserPublisher) registry.lookup("UserPublisher");
            lobbyPublisher = (ILobbyPublisher) registry.lookup("LobbyPublisher");
            gamepublisher = (IGamePublisher) registry.lookup("GamePublisher");
            userToken = userPublisher.login("seahorsepip", "12345");
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

    @FXML
    void onLoginRegister(ActionEvent event) throws RemoteException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        userToken = userPublisher.login(username, password);
        if (userToken == null) {
            userPublisher.register(username, password);
            userToken = userPublisher.login(username, password);
        }
        loginPane.setVisible(false);
        lobbiesPane.setVisible(true);
        loadLobbies();
    }

    @FXML
    void onRefreshLobbies(ActionEvent event) throws RemoteException {
        loadLobbies();
    }

    @FXML
    void onAddLobbyScreen(ActionEvent event) throws RemoteException {
        lobbiesPane.setVisible(false);
        addLobbyPane.setVisible(true);
    }

    @FXML
    void onAddLobby(ActionEvent event) throws RemoteException, NotLoggedInException, NotBoundException {
        String name = lobbyNameField.getText();
        String password = lobbyPasswordField.getText();
        password = password.length() > 0 ? password : null;
        addLobbyPane.setVisible(false);
        joinLobby(lobbyPublisher.create(userToken, name, password), password);
    }

    private void joinLobby(String lobbyToken, String password) throws RemoteException, NotBoundException {
        this.lobbyToken = lobbyToken;
        lobbyPublisher.join(lobbyToken, userToken, password);
        new LobbyListener(lobbyToken, userToken, new LobbyListener.Callback() {
            @Override
            public void onHost(User value) {
                System.out.println("New host: " + value.getUsername());
            }

            @Override
            public void onUsers(List<User> values) {

            }

            @Override
            public void onGameToken(String value) {
                try {
                    gameToken = value;
                    gamepublisher.connect(gameToken, userToken);
                    new GameListener(gameToken, new GameListener.Callback() {
                        @Override
                        public void onPlayers(List<Player> values) {
                            players = values;
                        }

                        @Override
                        public void onPlayer(Player value) {
                            for (int i = 0; i < players.size(); i++) {
                                if (players.get(i).getUser().getId() == value.getUser().getId()) {
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
                    lobbyPane.setVisible(false);
                    gamePane.setVisible(true);
                } catch (RemoteException | NotBoundException ignored) {
                }
            }
        });
        lobbyPane.setVisible(true);
    }

    @FXML
    void onLeaveLobby(ActionEvent event) throws RemoteException, NotLoggedInException, NotBoundException {
        lobbyPublisher.leave(lobbyToken, userToken);
        lobbyPane.setVisible(false);
        lobbiesPane.setVisible(true);
        loadLobbies();
    }

    @FXML
    void onKickLobby(ActionEvent event) throws RemoteException, NotLoggedInException, NotBoundException {
        int userId = 342;
        lobbyPublisher.kick(lobbyToken, userToken, userId);
    }

    @FXML
    void onStartLobby(ActionEvent event) throws RemoteException, NotLoggedInException, NotBoundException {
        lobbyPublisher.start(lobbyToken, userToken);
    }

    private void loadLobbies() throws RemoteException {
        lobbiesList.setItems(FXCollections.observableArrayList(lobbyPublisher.get()));
    }
}
