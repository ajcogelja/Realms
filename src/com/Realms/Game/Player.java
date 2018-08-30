package com.Realms.Game;

import java.awt.*;

public class Player extends Entity {
    public static int frame = 0;
    public final int framesLength;

    public Player(String name, Image[] sprites, int x, int y) {
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

    public int getFrame(){
        if (frame < framesLength){
            return frame++;
        } else {
            frame = 0;
            return frame;
        }
    }
}
