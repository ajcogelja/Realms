package com.Realms.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity extends JPanel {

    public String name;
    public BufferedImage[] sprites;
    public int x = 100;
    public int y = 100;
    public int width;
    public int height;
    public int length;

    public Entity(String name, BufferedImage[] sprites, int x, int y){
        this.name = name;
        this.sprites = sprites;
        this.x = x;
        this.y = y;
        length = sprites.length;
        width = sprites[0].getWidth();
        height = sprites[0].getHeight();
    }

    public Image[] getSprites(){
        return sprites;
    }
    public void paintComponent(Graphics g){
        Entity player = new Player(name, sprites, x, y);
        final Graphics2D g2 = (Graphics2D) g.create();
        g2.drawImage(((Player) player).getCurrentFrame(), x, y, null);
    }

}
