package com.example.popoutanu.invaders.activities;
 // Project made by Dan Popoutanu - Student ID : 1704805
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.PopupMenu;
import android.text.InputType;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.example.popoutanu.invaders.R;
import com.example.popoutanu.invaders.listeners.MyListener;
import com.example.popoutanu.invaders.views.SpaceInvadersView;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

// Lifecycle and dialogue of the game
public class SpaceInvadersActivity extends Activity implements MyListener {
    private MenuView menuView;
    private SpaceInvadersView spaceInvadersView;
    private static int notificationId = 0;
    private MediaPlayer ring;
    private Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // play music
        ring = MediaPlayer.create(SpaceInvadersActivity.this, R.raw.background);
        ring.start();
        // Get a Display object to access screen details
        display = getWindowManager().getDefaultDisplay();
        createHome();
    }

    @Override
    public void recreate() {
        super.recreate();
        // background music is added when the game starts
        ring = MediaPlayer.create(SpaceInvadersActivity.this, R.raw.background);
        ring.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        spaceInvadersView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        spaceInvadersView.pause();
    }
    // creating the game
    public void createHome() {
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);
        // Initialize gameView and set it as the view
        spaceInvadersView = new SpaceInvadersView(getApplicationContext(), size.x, size.y, this);
        setContentView(R.layout.menu);

        // play button
        final Button playbutton = (Button) findViewById(R.id.play_button);
        playbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // stops music when game starts
                if (ring != null) {
                    ring.stop();
                }
                setContentView(spaceInvadersView);
            }
        });
        // scoreboard button
        final Button scoreboardButton = (Button) findViewById(R.id.scoreboard_button);
        scoreboardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ring != null) {
                    ring.stop();
                }
                showLeaderboard();
            }
        });
    }

    // dialog when quitting the application by pressing the Back button
    AlertDialog alertDialog;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onPause();

            alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Don't leave us. There are more aliens coming.");
            alertDialog.setMessage("Are you sure?");

            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });

            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onResume();
                        }
                    });

            alertDialog.show();
            return true; //meaning you've dealt with the keyevent
        }
        return super.onKeyDown(keyCode, event);
    }

    // create a notification
    public void notifyUser() {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, SpaceInvadersActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "")
                .setSmallIcon(R.drawable.alien)
                .setContentTitle("HEEEELPPPPP!!")
                .setContentText("The aliens are coming back, we need you !!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId++, mBuilder.build());
    }

    @Override
    // what happens after we quit the app
    protected void onDestroy() {
        super.onDestroy();
        if (ring != null) {
            ring.stop();
        }

        MyTimerTask myTask = new MyTimerTask();
        Timer myTimer = new Timer();
        // send a notification after 5 minutes with a 24 hour delay
        myTimer.schedule(myTask, 360000, 86400000);
    }


    @Override
    public void onStartLevel(String message) {
        runOnUiThread(new ShowToastRunnable(this, message));
    }

    @Override
    public void popup() {
        runOnUiThread(new ShowPopupRunnable(this, spaceInvadersView));
    }


    // add a timer on notifications
    class MyTimerTask extends TimerTask {
        public void run() {
            notifyUser();
        }
    }

    private String text = "";

    @Override
    public void onEndGame(int score) {
        getPlayerName(score);
    }

    // Add a dialogue asking the name of the user
    public void getPlayerName(int score) {
        runOnUiThread(new GetPlayerNameRunnable(this, score));
    }

    class GetPlayerNameRunnable implements Runnable {
        private Activity activity;
        private int score;

        public GetPlayerNameRunnable(Activity activity, int score) {
            this.activity = activity;
            this.score = score;
        }

        @Override
        public void run() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
            builder.setTitle("What is your name great savior?");
            // Set up the input
            final EditText input = new EditText(this.activity);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    text = input.getText().toString();
                    // adding user to database
                    DBHandler db = new DBHandler(SpaceInvadersActivity.this);
                    db.addUser(new User(0, text, score));
                    showLeaderboard();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    showLeaderboard();
                }
            });
            builder.create().show();
        }
    }
    // display a toast message with the level number in the start of each level
    class ShowToastRunnable implements Runnable {
        private Activity activity;
        private String message;

        public ShowToastRunnable(Activity activity, String message) {
            this.activity = activity;
            this.message = message;
        }

        @Override
        public void run() {
            Toast.makeText(this.activity, message,
                    Toast.LENGTH_LONG).show();
        }
    }
    // popup menu
    class ShowPopupRunnable implements Runnable {
        View view;
        Activity activity;

        public ShowPopupRunnable(Activity activity, View view) {
            this.view = view;
            this.activity = activity;
        }

        @Override
        public void run() {
            // Anchor popoup with layout to "center" menu
            PopupMenu popup = new PopupMenu(activity, view, Gravity.RIGHT, R.attr.actionOverflowMenuStyle, 0);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.resume:
                            spaceInvadersView.resume();
                            return true;
                        case R.id.restart:
                            spaceInvadersView.restart();
                            return true;
                        case R.id.quit:
                            recreate();
                            return true;
                    }

                    return true;
                }
            });
            popup.getMenuInflater().inflate(R.menu.my_menu, popup.getMenu());
            popup.show();
        }
    }

    public void showLeaderboard() {
        DBHandler db = new DBHandler(SpaceInvadersActivity.this);

        List<User> leaderboard = db.getLeaderboard();
        setContentView(R.layout.list);

        //back button
        final Button backButton = (Button) findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recreate();
            }
        });

        ListView listContent = (ListView) findViewById(R.id.contentlist);

        ArrayAdapter<User> arrayAdapter =
                new ArrayAdapter<User>(getApplicationContext(), R.layout.row, leaderboard);
        // Set The Adapter
        listContent.setAdapter(arrayAdapter);
    }
}

