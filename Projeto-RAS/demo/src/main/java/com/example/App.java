package com.example;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/* How to run:
    - Change dir to demo
    - Run cmd: "mvn clean javafx:run"
 */

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    public static RASBetDL dl;

    @Override
    public void start(Stage stage) throws Exception {
        
		System.out.print("Opening database... ");
		dl = new RASBetDL("database.db");
		System.out.println("OK");


		System.out.print("Initializing database... ");
		dl.init();
		System.out.println("OK");


		System.out.print("Populating database... ");
		dl.populate();
		System.out.println("OK");


		System.out.print("Starting sync threads... ");
		dl.startSyncing();
		System.out.println("OK\n");
        
        //dl.close();
		//System.out.println("\nDatabase closed");
		//System.out.println("Goodbye :(");

        scene = new Scene(loadFXML("initialPage"), 1280, 720);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}