package sample.controller;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.Activitys;
import sample.Constants;

import java.net.URL;

public class DefaultController implements Controller{
    protected Stage primaryStage;
    protected Scene scene;

    public DefaultController(){

    }

    public void runAsync(Task task){
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    protected URL getXML(String path){
        return getClass().getResource(path);
    }


    public void load(Controller controller){
        Activitys.load(primaryStage,controller);
    }

    public double getHeight(){
        return Constants.DEFAULT_HEIGHT;
    }

    public double getWidth(){
        return Constants.DEFAULT_WIDTH;
    }

    public URL getXML() {
        return null;
    }

    public void init(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void onStart() {}

    @Override
    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
