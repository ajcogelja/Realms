package com.Realms.Client;

import com.Realms.Game.Game;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Client extends Application {

    //initialize image for the background
    Image clientBackground;
    ImageView background;
    Rectangle flash;
    //Pane and components
    Pane pane;
    TextField usernameEntry;
    String musicFile = "Res/Music/Catacombs.mp3";
    Media sound = new Media(new File(musicFile).toURI().toString());
    MediaPlayer mp = new MediaPlayer(sound);

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        Scene mainScene;
        pane = new Pane();
        usernameEntry = new TextField();
        flash = new Rectangle();

        //load the background file
        try{
            clientBackground = new Image(new FileInputStream("Res/Sprites/Realms_Working_Title_Screen.png"));
        }catch (IOException e){
            System.out.println("Client background image failed to load");
        }

        //background and pane components
        background = new ImageView(clientBackground);
        background.setFitHeight(600);
        background.setFitWidth(800);

        //add components to scene in order
        pane.getChildren().add(background);
        flash.setHeight(60);
        flash.setWidth(300);
        flash.setX(220);
        flash.setY(290);
        flash.setFill(clientBackground.getPixelReader().getColor(221, 301));

        pane.getChildren().add(flash);
        primaryStage.setTitle("Realms Beta");
        mainScene = new Scene(pane);
        primaryStage.setScene(mainScene);

        AnimationTimer clickRegister = new AnimationTimer() {
            long current = 0;
            @Override
            public void handle(long now) {
                if ((now - current)/10 >= 1000){
                    try {
                        Thread.sleep(5);
                        mainScene.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                switch (event.getButton()){
                                    case PRIMARY:
                                        primaryStage.close();
                                        new Game();
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        AnimationTimer flicker = new AnimationTimer() {
            long current = 0;
            @Override
            public void handle(long now) {

                if ((now - current)/10 >= 80000000){
                    flash.setVisible(!flash.isVisible());
                    current = System.nanoTime();
                }

            }
        };

        primaryStage.show();
        clickRegister.start();
        flicker.start();
        mp.play();

    }


    public static void main(String[] args) {
        launch(args);
    }

}
