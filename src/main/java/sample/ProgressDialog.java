package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ProgressDialog {
    private Stage stage;
    public ProgressDialog(){
        stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/loading.fxml"));
        loader.setController(this);
        try {
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(loader.load(),150 ,150);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void show() {
        stage.show();
    }

    public void hide() {
        stage.close();
    }
}
