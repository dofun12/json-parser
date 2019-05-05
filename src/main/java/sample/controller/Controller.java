package sample.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public interface Controller {
    URL getXML();
    void init(Stage primaryStage);
    void onStart();
    void setScene(Scene scene);
    double getHeight();
    double getWidth();
}
