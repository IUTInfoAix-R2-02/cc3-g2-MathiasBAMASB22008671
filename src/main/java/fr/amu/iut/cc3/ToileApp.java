package fr.amu.iut.cc3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ToileApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("toile.fxml"));
        primaryStage.setTitle("Résultats aux différentes compétences du BUT");
        primaryStage.setResizable(false);
        primaryStage.setScene( new Scene(root) );
        primaryStage.show();
    }

}

