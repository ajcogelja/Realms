package com.Realms.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {
    public static int frame = 0;
    public final int framesLength;

    public Player(String name, BufferedImage[] sprites, int x, int y) {
        super(name, sprites, x, y);
        framesLength = sprites.length;
    }

    @Override
    public Image[] getSprites() {
        return super.getSprites();
    }

    public void moveX(int delta){
        this.x += delta;
    }
    public void moveY(int delta){
        this.y += delta;
    }

    public int nextFrame(){
        if (frame < framesLength - 1){
            return frame++;
        } else {
            frame = 0;
            return frame;
        }
    }
    public int getFrame(){
        return frame;
    }

    public int getFramesLength(){
        return framesLength;
    }

    public Image getCurrentFrame(){
        return sprites[frame];
    }

    public void setFrame(int newFrame){
        frame = newFrame;
    }

}
