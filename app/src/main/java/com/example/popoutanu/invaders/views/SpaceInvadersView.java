package com.example.popoutanu.invaders.views;
// Project made by Dan Popoutanu - Student ID : 1704805
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.popoutanu.invaders.R;
import com.example.popoutanu.invaders.listeners.MyListener;
import com.example.popoutanu.invaders.models.Bullet;
import com.example.popoutanu.invaders.models.Invader;
import com.example.popoutanu.invaders.models.PlayerShip;

import java.util.concurrent.CopyOnWriteArrayList;

public class SpaceInvadersView extends SurfaceView implements Runnable {

    MyListener myListener;

    Context context;
    //invaders army
    CopyOnWriteArrayList<Invader> invaders = new CopyOnWriteArrayList<>();
    // The score
    int score = 0;
    private Thread gameThread = null;
    private SurfaceHolder ourHolder;
    //are we playing or not ?
    private volatile boolean playing;
    // game is paused when the application starts and endgame is set to false
    private boolean paused = true;
    private boolean endGame = false;
    private Canvas canvas;
    private Paint paint;
    // framerate per second
    private long fps;
    // calculate the fps
    private long timeThisFrame;
    // size of the screen
    private int screenX;
    private int screenY;
    // our ship
    private PlayerShip playerShip;
    // arraylist of bullets so that more bullets can be active in the same time
    private CopyOnWriteArrayList<Bullet> bullets = new CopyOnWriteArrayList<>();
    private long lastBulletFiredTime;
    // arraylist of bullets from invaders
    private CopyOnWriteArrayList<Bullet> invadersBullets = new CopyOnWriteArrayList<>();
    // Lives and level
    private int lives = 3;
    private int level = 1;
    //sounds for explosion on player death and bullet fire
    final MediaPlayer death = MediaPlayer.create(getContext(), R.raw.explosion);
    final MediaPlayer shoot = MediaPlayer.create(getContext(), R.raw.shoot);


    public SpaceInvadersView(Context context, int x, int y, MyListener myListener) {
        //set up the object
        super(context);

        playing = true;
        endGame = false;

        this.myListener = myListener;

        // make context global
        this.context = context;

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        screenX = x;
        screenY = y;

        lives = 3;
        score = 0;
        level = 1;

        prepareLevel();
    }


    private void prepareLevel() {
        if (level > 1) {
            myListener.onStartLevel("Starting level " + level);
        }
        // Initialize all objects
        playerShip = new PlayerShip(context, screenX, screenY);
        playerShip.increaseSpeed(level);

        // Initialize the invadersBullets array
        for (Bullet invaderBullet : invadersBullets) {
            invaderBullet = new Bullet(screenY);
        }

        // Build an army of invaders
        invaders = new CopyOnWriteArrayList<>();

        for (int column = 0; column < 6; column++) {
            for (int row = 0; row < (level + 1); row++) {
                invaders.add(new Invader(context, row, column, screenX, screenY, level));
            }
        }
        bullets = new CopyOnWriteArrayList<>();
        invadersBullets = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        while (playing) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            if (!paused && !endGame) {
                update();
            }

            // Draw the frame
            draw();

            // fps calculation
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    private void update() {
        // bumping into the border of the screen
        boolean bumped = false;


        // Move the player's ship
        playerShip.update(fps);

        // Update all the invaders if visible
        for (Invader invader : invaders) {
            if (invader != null && invader.getVisibility()) {
                // Move the next invader
                invader.update(fps);

                // Does he want to take a shot?
                if (invader.takeAim(playerShip.getX(), playerShip.getLength())) {
                    // If so try and spawn a bullet
                    Bullet invaderBullet = new Bullet(screenY);
                    if (invaderBullet.shoot(invader.getX() + invader.getLength() / 2, invader.getY(), Bullet.DOWN)) {
                        invadersBullets.add(invaderBullet);
                    }
                }
                // If that move caused them to bump the screen change bumped to true
                if (invader.getX() > screenX - invader.getLength()
                        || invader.getX() < 0) {

                    bumped = true;
                }
            }

        }

        // Update all the invaders bullets if active
        for (Bullet invaderBullet : invadersBullets) {
            if (invaderBullet.getStatus()) {
                invaderBullet.update(fps);
            }
        }

        // Did an invader bump into the edge of the screen
        if (bumped) {

            // Move all the invaders down and change direction
            for (Invader invader : invaders) {
                invader.dropDownAndReverse();
                // Have the invaders landed
                if (invader.getY() > screenY - screenY / 10) {
                    death.start();
                    if (score > 300) {
                        myListener.onEndGame(score);
                        playing = false;
                    }
                    //paused = true;
                    endGame = true;
                    lives = 3;
                    score = 0;
                    level = 1;
                    prepareLevel();
                }
            }
        }

        int enemyCount = -1;
        // Update the players bullet
        for (Bullet bullet : bullets) {
            enemyCount = 0;
            if (bullet.getStatus()) {
                bullet.update(fps);
            }
            // Has the player's bullet hit the top of the screen
            if (bullet.getImpactPointY() < 0) {
                bullet.setInactive();
                bullets.remove(bullet);
                continue;
            }
            // Has the player's bullet hit an invader
            if (bullet.getStatus()) {
                for (Invader invader : invaders) {
                    if (invader.getVisibility()) {
                        if (RectF.intersects(bullet.getRect(), invader.getRect())) {
                            invader.explode();
                            bullet.setInactive();
                            bullets.remove(bullet);
                            score = score + 10;
                            break;
                        } else {
                            enemyCount++;
                        }
                    }
                }
            }
        }
        // Player wins when there are no more enemyes and endgame happens
        if (enemyCount == 0) {
            level++;
            prepareLevel();
        }

        // When bullets get out of screen they should dissappear.
        for (Bullet invaderBullet : invadersBullets) {
            if (invaderBullet.getImpactPointY() > screenY) {
                invaderBullet.setInactive();
                invadersBullets.remove(invaderBullet);
            }
        }

        // Bullet collision, when a bullet hits the rect of playership player loses one life.
        for (Bullet invaderBullet : invadersBullets) {
            if (invaderBullet.getStatus()) {
                if (RectF.intersects(playerShip.getRect(), invaderBullet.getRect())) {
                    invaderBullet.setInactive();
                    invadersBullets.remove(invaderBullet);
                    lives--;

                    // Is it game over?
                    // If the player scores more than 300 we ask his name and we save it in the database.
                    if (lives == 0) {
                        if (score > 300) {
                            myListener.onEndGame(score);
                            playing = false;
                        }
                        death.start();
                        //paused = true;
                        endGame = true;
                        lives = 3;
                        score = 0;
                        level = 1;
                        prepareLevel();
                    }
                }
            }
        }
    }

    private void draw() {
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // backgroundcolor
            canvas.drawColor(Color.argb(255, 26, 128, 182));
            paint.setColor(Color.argb(255, 255, 255, 255));

            // Draw the player spaceship
            canvas.drawBitmap(playerShip.getBitmap(), playerShip.getX(), playerShip.getY(), paint);

            // Draw the invaders
            for (Invader invader : invaders) {
                if (invader.getVisibility()) {
                    if (invader.isExploded()) {
                        if (System.currentTimeMillis() - invader.getExplodedTime() < 200) {
                            canvas.drawBitmap(invader.getBitmap3(), invader.getX(), invader.getY(), paint);
                        } else {
                            invaders.remove(invader);
                        }
                    } else {
                        canvas.drawBitmap(invader.getBitmap2(), invader.getX(), invader.getY(), paint);
                    }
                }
            }

            // Draw the players bullet if active
            for (Bullet bullet : bullets) {
                if (bullet.getStatus()) {
                    paint.setColor(Color.YELLOW);
                    canvas.drawRect(bullet.getRect(), paint);
                }
            }

            // Alien bullets made red and updated
            paint.setColor(Color.RED);
            for (Bullet invaderBullet : invadersBullets) {
                canvas.drawRect(invaderBullet.getRect(), paint);
            }

            // score and lives remaining
            paint.setColor(Color.argb(255, 249, 129, 0));
            paint.setTextSize(80);
            canvas.drawText("Score: " + score + "   Lives: " + lives, 20, 80, paint);

            if (endGame) {
                // end game screen
                canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.gameover), 200, screenY / 2 - 400, paint);
            }
            // pause button
            canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.pause), screenX - 120, 30, paint);

            // Draw everything to the screen
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    // shutdown our thread.
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    // start our thread.
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void restart() {
        lives = 3;
        score = 0;
        level = 1;
        prepareLevel();
        resume();
    }


    // on touch events, controlling the shooting and moving of the ship
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:
                paused = false;
                if (endGame) {
                    if ((motionEvent.getX() > 200) && (motionEvent.getX() < screenX * 2 - 200) && (motionEvent.getY() > screenY / 3) && (motionEvent.getY() < screenY * 2 / 3)) {
                        endGame = false;
                        //restart();
                        new SpaceInvadersView(context, screenX, screenY, myListener);
                    }
                } else {
                    if ((motionEvent.getX() > screenX - 130) && (motionEvent.getX() < screenX - 30) && (motionEvent.getY() > 30 && (motionEvent.getY() < 130))) {
                        pause();
                        myListener.popup();

                    }
                    if (motionEvent.getY() > screenY * 0.5) {
                        if (motionEvent.getX() > screenX / 2) {
                            playerShip.setMovementState(playerShip.RIGHT);
                        } else {
                            playerShip.setMovementState(playerShip.LEFT);
                        }
                    }
                    Bullet bullet = new Bullet((int) playerShip.getY());
                    if (System.currentTimeMillis() - lastBulletFiredTime > 1000 && bullet.shoot(playerShip.getX() +
                            playerShip.getLength() / 2, screenY, Bullet.UP)) {
                        shoot.start();
                        bullets.add(bullet);
                        lastBulletFiredTime = System.currentTimeMillis();
                    }
                }
                break;

            // When the bottom of the screen is pressed the ship is supposed to stop.
            case MotionEvent.ACTION_UP:
                if (motionEvent.getY() > screenY * 0.8) {
                    playerShip.setMovementState(playerShip.STOPPED);
                }
                break;
        }
        return true;
    }


}
