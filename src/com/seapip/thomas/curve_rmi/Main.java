package com.seapip.thomas.curve_rmi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        primaryStage.getScene().setOnKeyPressed(event -> ((Controller) loader.getController()).onKeyPressed(event));
    }
}
