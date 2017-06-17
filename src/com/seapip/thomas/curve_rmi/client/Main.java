package com.seapip.thomas.curve_rmi.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(loader.load(getClass().getResource("curve_rmi.fxml").openStream()),
                400, 400));
        primaryStage.show();
        //Set global key listener
        primaryStage.getScene().setOnKeyPressed(event -> {
            try {
                ((Controller) loader.getController()).onKeyPressed(event);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }
}
