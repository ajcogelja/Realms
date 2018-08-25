package com.Realms.Client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.*;

import java.io.FileInputStream;
import java.io.IOException;

public class Client extends Application {

    //initialize image for the background
    Image clientBackground;
    ImageView background;

    //Pane and components
    Pane pane;
    TextField usernameEntry;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene mainScene;
        pane = new Pane();
        usernameEntry = new TextField();

        //load the background file
        try{
            clientBackground = new Image(new FileInputStream("Res/Sprites/Realms_Working_Title_Screen.png"));
        }catch (IOException e){
            System.out.println("Client background image failed to load");
        }

        //background and pane components
        background = new ImageView(clientBackground);

        //add components to scene in order
        pane.getChildren().add(background);

        primaryStage.setTitle("Realms Beta");
        mainScene = new Scene(pane);
        primaryStage.setScene(mainScene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
