package com.Realms.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {
    public static int frame = 0;
    public final int framesLength;

    public Player(String name, BufferedImage[] sprites, float x, float y) {
        super(name, sprites, x, y);
        framesLength = sprites.length;
    }

    @Override
    public Image[] getSprites() {
        return super.getSprites();
    }

    public int getX(){
        return (int) this.x;
    }
    public int getY(){
        return (int) this.y;
    }

    public void moveX(float delta){
        this.x += delta;
    }
    public void moveY(float delta){
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
