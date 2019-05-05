package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.controller.Controller;

import java.io.IOException;

public class Activitys {
    public static void load(Stage primaryStage,Controller controller){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(controller.getXML());
        loader.setController(controller);


        try {
            Parent root = loader.load();
            Scene scene = new Scene(root,controller.getWidth(),controller.getHeight());
            scene.getStylesheets().add(controller.getClass().getResource("/default.css").toExternalForm());
            primaryStage.setScene(scene);
            controller.setScene(scene);
            controller.init(primaryStage);
            primaryStage.show();
            controller.onStart();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
