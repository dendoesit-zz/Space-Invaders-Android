package com.example.popoutanu.invaders.models;

import android.graphics.RectF;

/**
 * Created by Popoutanu on 2/3/2018.
 */

public class Bullet {

    private float x;
    private float y;

    private RectF rect;

    // Direction of shooting
    public static final int UP = 0;
    public static final int DOWN = 1;

    // Going nowhere
    int heading = -1;
    float speed =  350;

    private int width = 5;
    private int height = 2;

    private boolean isActive;
    //size of bullet
    public Bullet(int screenY) {

        height = screenY / 20;
        isActive = false;

        rect = new RectF();
    }

    public RectF getRect(){
        return  rect;
    }

    public boolean getStatus(){
        return isActive;
    }

    public void setInactive(){
        isActive = false;
    }

    public float getImpactPointY(){
        if (heading == DOWN){
            return y + height;
        }else{
            return  y;
        }

    }
    //shooting function
    public boolean shoot(float startX, float startY, int direction) {
        x = startX;
        y = startY;
        heading = direction;
        isActive = true;
        return true;
    }

    public void update(long fps){

        // Just move up or down
        if(heading == UP){
            y = y - speed / fps;
        }else{
            y = y + speed / fps;
        }

        // Update rect
        rect.left = x;
        rect.right = x + width;
        rect.top = y;
        rect.bottom = y + height;

    }

}

