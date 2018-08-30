package com.Realms.Game;

import java.awt.*;

public class Entity extends Canvas{

    public String name;
    public Image[] sprites;
    public int x;
    public int y;
    public int width;
    public int height;
    public int length;

    public Entity(String name, Image[] sprites, int x, int y){
        this.name = name;
        this.sprites = sprites;
        this.x = x;
        this.y = y;
        length = sprites.length;
    }

    public Image[] getSprites(){
        return sprites;
    }
    public void paint(Graphics g){
        final Graphics2D g2 = (Graphics2D) g.create();
        g2.drawImage(getSprites()[0], 100, 100, null);
    }

}
