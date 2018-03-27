package com.example.popoutanu.invaders.listeners;

/**
 * Created by Popoutanu on 3/15/2018.
 */

public interface MyListener {
        void onEndGame(int score);

        void onStartLevel(String message);

        void popup();
}
