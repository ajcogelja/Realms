package com.Realms.Client;

import javafx.application.Application;
import javafx.scene.*;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.transform.*;
import javafx.animation.AnimationTimer;

import org.mapeditor.core.Map;
import org.mapeditor.core.Tile;
import org.mapeditor.core.TileLayer;
import org.mapeditor.io.TMXMapReader;

import java.util.ArrayList;

import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.util.HashMap;

public class Main extends Application {

    Pane mainPane;
    Scene scene;

    TMXMapReader mapReader = new TMXMapReader();
    Map map = null;
    TileLayer layer = null;

    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();

        mainPane = new Pane();
        root.getChildren().add( mainPane );
        scene = new Scene( root, 640,480);

        primaryStage.setScene( scene );
        primaryStage.show();

        // we how have a mainPane that we can hang stuff on and see it
        // in a window...

        // loading tmx and expanding it into various java structures
        // it also loads the tile map images
        try {
            map = mapReader.readMap("C:\\Users\\op3er\\Documents\\Realms\\src\\com\\Realms\\sewers\\sewers.tmx");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // assume just the one layer
        // you could have different layers on different
        // javafx nodes sitting on top of each other...
        layer = (TileLayer)map.getLayer(0);
        if (layer==null) {
            System.out.println("can't get map layer");
            System.exit(-1);
        }

        int width = layer.getWidth();
        int height = layer.getHeight();

        Tile tile = null;
        int tid;

        // as libtiled provides awt images we must convert them to
        // javafx images but we don't want a new image for every
        // single tile on the map
        HashMap<Integer,Image> tileHash = new HashMap<Integer,Image>();
        Image tileImage = null;

        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                tile = layer.getTileAt(x,y);
                tid = tile.getId();
                if (tileHash.containsKey(tid)) {
                    // if we have already used the image get it from the hashmap
                    tileImage=tileHash.get(tid);
                } else {
                    // if we haven't seen it before convert and cache it
                    try {
                        tileImage=createImage(tile.getImage());
                    } catch (Exception e) {
                        e.printStackTrace(); // TODO!
                    }
                    tileHash.put(tid,tileImage);
                }
                ImageView iv = new ImageView(tileImage);
                // assuming staggered if Y is odd move it right 1/2 a tile
                // also assuming 64x64 tile
                iv.setTranslateX((x*24));
                iv.setTranslateY(y*24);
                mainPane.getChildren().add(iv);
                // at this point you might want to add the ImageView to a custom
                // "tile" class including your own info which you can then place
                // in a 2d array where the index's are the coordinates of the tile

            }
        }
        // give some indication that caching did something!
        System.out.println("tile image hash has "+tileHash.size()+" items");
        // dump all the tmx stuff
        tileHash = null;
        map = null;
        layer = null;
    }

    public static void main(String[] args) {
        launch(args);
    }

    // copy an awt image into a javafx image
    // "borrowed" fragment from part of
    // https://community.oracle.com/message/9655930#9655930
    public static javafx.scene.image.Image createImage(java.awt.Image image) throws Exception {
        if (!(image instanceof RenderedImage)) {
            BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
                    image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics g = bufferedImage.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            image = bufferedImage;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage) image, "png", out);
        out.flush();
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        return new javafx.scene.image.Image(in);
    }
}