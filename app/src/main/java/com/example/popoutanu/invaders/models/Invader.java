package com.example.popoutanu.invaders.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.util.Log;

import com.example.popoutanu.invaders.R;

import java.util.Random;

/**
 * Created by Popoutanu on 2/3/2018.
 */

public class Invader {

    RectF rect;

    Random generator = new Random();

    // The player ship and aliens are represented using bitmaps
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmap3;

    // Size of invader
    private float length;
    private float height;

    // the most left point of the invader
    private float x;

    // the top most point of the invader
    private float y;

    private float shipSpeed;
    private final float baseSpeed = 50;

    public final int LEFT = 1;
    public final int RIGHT = 2;
    private int shipMoving = RIGHT;

    // before the bullet hits the alien
    private boolean exploded = false;
    private long explodedTime = 0;

    // to check if the aliens are still visibile, called after they are shot
    boolean isVisible;

    public Invader(Context context, int row, int column, int screenX, int screenY, int level) {

        rect = new RectF();

        length = screenX / 20;
        height = screenY / 20;

        isVisible = true;

        int padding = screenX / 25;

        x = column * (length + padding);
        y = row * (length + padding / 2);

        // Initialize the bitmap
        bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.alien);
        bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.alien);
        bitmap3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion1);

        // stretch the  bitmap to a size for screen resolution
        bitmap1 = Bitmap.createScaledBitmap(bitmap1,
                (int) (length),
                (int) (height),
                false);

        bitmap2 = Bitmap.createScaledBitmap(bitmap2,
                (int) (length),
                (int) (height),
                false);

        bitmap3 = Bitmap.createScaledBitmap(bitmap3,
                (int) (length),
                (int) (height),
                false);

        // the speed in pixels per second
        shipSpeed = baseSpeed + (level - 1) * 10;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public void explode(){
        explodedTime = System.currentTimeMillis();
        exploded = true;
    }

    public boolean isExploded() {
        return exploded;
    }

    public long getExplodedTime() {
        return explodedTime;
    }

    public boolean getVisibility(){
        return isVisible;
    }

    public RectF getRect(){
        return rect;
    }

    public Bitmap getBitmap(){
        return bitmap1;
    }

    public Bitmap getBitmap2(){
        return bitmap2;
    }

    public Bitmap getBitmap3(){
        return bitmap3;
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

    //moving funciton
    public void update(long fps){
        if(shipMoving == LEFT){
            x = x - shipSpeed / fps;
        }

        if(shipMoving == RIGHT){
            x = x + shipSpeed / fps;
        }

        // Detect hits
        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + length;
    }

    public void dropDownAndReverse(){
        if(shipMoving == LEFT){
            shipMoving = RIGHT;
        }else{
            shipMoving = LEFT;
        }
        y = y + height;

    }

    //shoot at the player
    public boolean takeAim(float playerShipX, float playerShipLength){
        int randomNumber = -1;
        // If near the player
        if((playerShipX + playerShipLength > x &&
                playerShipX + playerShipLength < x + length) || (playerShipX > x && playerShipX < x + length)) {
            // A 1 in 500 chance to shoot
            randomNumber = generator.nextInt(150);
            if(randomNumber == 0) {
                return true;
            }
        }
        // If firing randomly (not near the player) a 1 in 5000 chance
        randomNumber = generator.nextInt(2000);
        if(randomNumber == 0){
            return true;
        }
        return false;
    }

}

