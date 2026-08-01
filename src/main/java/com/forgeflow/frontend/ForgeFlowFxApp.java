package com.forgeflow.frontend;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ForgeFlowFxApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("ForgeFlow Enterprise Productivity Platform v1.0");

        MainController mainController = new MainController();
        Scene scene = new Scene(mainController.getView(), 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());

        stage.setScene(scene);
        stage.setMinWidth(1024);
        stage.setMinHeight(700);
        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
