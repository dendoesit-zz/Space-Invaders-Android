package com.example.popoutanu.invaders.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.nfc.Tag;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.popoutanu.invaders.R;

/**
 * Created by Popoutanu on 2/3/2018.
 */

public class PlayerShip {

    RectF rect;

    private Bitmap bitmap;

    // size of the ship
    private float length;
    private float height;

    // the most far left point
    private float x;

    // the most top point
    private float y;
    private float shipSpeed;
    private final float baseSpeed = 205;

    // Which ways can the ship move
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    // Is the ship moving and in which direction
    private int shipMoving = STOPPED;

    private float width;
    // the constructor that initializez the ship and size
    public PlayerShip(Context context, int screenX, int screenY){

        // Initialize a blank RectF
        rect = new RectF();

        length = screenX / 10;
        height = screenY / 10;

        // Start ship position on the bottom of the screen and middle
        x = screenX / 2 - 125;
        y = screenY / 9 * 8;
        width = screenX;

        // Initialize the bitmap
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        // stretch the bitmap to a size for the screen resolution
        bitmap = Bitmap.createScaledBitmap(bitmap,
                (int) (length),
                (int) (height),
                false);

        // speed of the ship in pixels per second
        shipSpeed = baseSpeed;
    }
    public RectF getRect(){
        return rect;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getLength(){
        return length;
    }

    // Change the direction the ship is going
    public void setMovementState(int state){
        shipMoving = state;
    }

    //Where the player ship needs to move
    public void update(long fps){
        if(shipMoving == LEFT && x - shipSpeed / fps > 0){
            x = x - shipSpeed / fps;
        }

        if(shipMoving == RIGHT && x + length + shipSpeed / fps < width){
            x = x + shipSpeed / fps;
        }

        // Update rect which is used to detect hits
        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + length;

    }

    public void increaseSpeed(int level) {
        this.shipSpeed = baseSpeed + (level - 1) * 10;
    }
}

