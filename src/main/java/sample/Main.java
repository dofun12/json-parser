package sample;

import javafx.application.Application;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import sample.controller.InitialController;
import sample.controller.LoadingController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Main extends Application {
    private static Properties properties = null;
    private static File propertiesFile;
    private static File databaseFile;
    private static SessionFactory sessionFactory;
    private static Session session;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World");
        Activitys.load(primaryStage, new InitialController());
    }


    public static Session getLocalDB() {
        if (properties != null) {
            File dbFile = new File(properties.getProperty("database"));
            if (sessionFactory == null) {
                sessionFactory = HibernateConnection.getLocalSession(dbFile.getAbsolutePath());
            }
            if (session == null) {
                session = sessionFactory.openSession();
            }else{
                if(!session.isOpen()){
                    session = sessionFactory.openSession();
                }
            }
            return session;


        }
        return null;
    }

    public static void main(String[] args) {

        String basePath = System.getProperty("user.home");
        File dir = new File(basePath + File.separator + "json-parser");
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            properties = new Properties();
            final String configname = "config.properties";
            final String databaseName = "jsonparser.db";

            propertiesFile = new File(dir.getAbsoluteFile() + File.separator + configname);


            if (!propertiesFile.exists()) {
                databaseFile = new File(dir.getAbsoluteFile() + File.separator + databaseName);
                properties.setProperty("criado", new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss").format(new Date()));
                properties.setProperty("database", databaseFile.getAbsolutePath());
                properties.store(new FileOutputStream(propertiesFile), "criado");
            }

            properties.load(new FileInputStream(propertiesFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
        launch(args);
    }

    public static String getPropertie(String propertie) {
        return properties.getProperty(propertie);
    }

    public static void setProperties(String propertie, String value) {
        properties.setProperty(propertie, value);
        try {
            properties.store(new FileOutputStream(propertiesFile), "Null");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
