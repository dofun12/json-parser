package sample.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.net.URL;

public class LoadingController extends DefaultController {

    @FXML
    Label loadingText;

    private int etapa=0;
    @Override
    public void onStart() {
        System.out.println("Iniciado");
        loadingText.setText("Iniciando");
        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() {
                for(etapa=0;etapa<=5;etapa++){
                    try {

                        Platform.runLater(new Runnable() {
                            public void run() {
                                loadingText.setText("Etapa "+etapa+"/5");
                            }
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Platform.runLater(new Runnable() {
                    public void run() {
                        load(new InitialController());
                    }
                });

                return null;
            }
        };
        runAsync(task);
    }

    @Override
    public URL getXML() {
        return getXML("/loading.fxml");
    }
}
