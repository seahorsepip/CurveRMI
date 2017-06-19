package com.seapip.thomas.curvermi.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(loader.load(getClass().getResource("curvermi.fxml").openStream()),
                400, 400));
        primaryStage.show();
        //Set global key listener
        primaryStage.getScene().setOnKeyPressed(event -> {
            try {
                ((Controller) loader.getController()).onKeyPressed(event);
            } catch (RemoteException ignored) {
                //Ignore
            }
        });
        primaryStage.getScene().setOnKeyReleased(event -> {
            try {
                ((Controller) loader.getController()).onKeyReleased(event);
            } catch (RemoteException ignored) {
                //Ignore
            }
        });
    }
}
