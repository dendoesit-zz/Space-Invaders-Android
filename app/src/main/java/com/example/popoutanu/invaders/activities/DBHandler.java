package com.example.popoutanu.invaders.activities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Popoutanu on 3/14/2018.
 */

public class DBHandler extends SQLiteOpenHelper {

    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "userDB.db";
    public static final String TABLE_NAME = "Users";
    public static final String COLUMN_ID = "UserID";
    public static final String COLUMN_NAME = "UserName";
    public static final String COLUMN_SCORE = "UserScore";

    //initialize the database
    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_SCORE + " INTEGER " + " ) ";
        db.execSQL(CREATE_TABLE);

        String INSERT_DUMMY = "INSERT INTO " + TABLE_NAME + "(UserName, UserScore) VALUES ('Mike' ,500 )";
        db.execSQL(INSERT_DUMMY);

        INSERT_DUMMY = "INSERT INTO " + TABLE_NAME + "(UserName, UserScore) VALUES ('Mike1' ,5000 )";
        db.execSQL(INSERT_DUMMY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Creating tables again
        onCreate(db);
    }

    // Adding new user
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getUserName()); // Shop Name
        values.put(COLUMN_SCORE, user.getUserScore()); // Shop Phone Number

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    // get user
    public User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID,
                        COLUMN_NAME, COLUMN_SCORE}, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        User user = new User(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getInt(2));
        // return user
        return user;
    }


    //get all users
    public List<User> getLeaderboard() {
        List<User> UserList = new ArrayList<User>();
        String selectQuery = "SELECT " + COLUMN_ID + ", " + COLUMN_NAME  + ", " + COLUMN_SCORE + " FROM " + TABLE_NAME + " order by " + COLUMN_SCORE + " desc limit 10";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setUserId(cursor.getInt(0));
                user.setUserName(cursor.getString(1));
                user.setUserScore(cursor.getInt(2));
                // Adding contact to list
                UserList.add(user);
            } while (cursor.moveToNext());
        }

        // return contact list
        return UserList;
    }

    public Cursor queueAll(){
        String[] columns = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_SCORE};
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, columns,
                null, null, null, null, null);

        return cursor;
    }

}


