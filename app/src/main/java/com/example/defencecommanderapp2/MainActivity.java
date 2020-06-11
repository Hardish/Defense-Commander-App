package com.example.defencecommanderapp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ConstraintLayout layout;
    public static int screenHeight;
    public static int screenWidth;
    private MissileMaker missileMaker;
    private ImageView launcher1;
    private ImageView launcher2;
    private ImageView launcher3;
  //  private ArrayList<ImageView> activeBase = new ArrayList<ImageView>();
    public static int levelValue = 1;
    private BaseLauncher baseLauncher;
    public static int scoreValue = 0;
    private static final int CODE_FOR_CHECK_SCORE = 111;
    private TextView score, level;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.layout);
        score = findViewById(R.id.score);
        level = findViewById(R.id.levelText);
        level.setText("Level 0");
        incrementScore(0);
        if(launcher1 == null || launcher2 == null || launcher3 == null) {
            launcher1 = findViewById(R.id.launcher1);
            launcher2 = findViewById(R.id.launcher2);
            launcher3 = findViewById(R.id.launcher3);
        }

        baseLauncher = new BaseLauncher(this,launcher1,launcher2,launcher3);

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    handleTouch(motionEvent.getX(), motionEvent.getY());
                }
                return false;
            }
        });
        setupFullScreen();
        getScreenDimensions();

        new ScrollingCloud(this,layout,R.drawable.clouds,9000);
        missileMaker = new MissileMaker(this,screenWidth,screenHeight);
        new Thread(missileMaker).start();

        if (checkNotReady())  //if sound is not loaded then simply dont act on the click
            return;
        SoundPlayer.getInstance().start("background");
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    public ConstraintLayout getLayout() {
        return layout;
    }
    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    private void setupFullScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void handleTouch(float x1, float y1) {
        if(baseLauncher.activeBase.size() == 0)
        {
            doStop();
        }
        else {
            baseLauncher.setBaseImageOnScreen(x1,y1);
        }
    }

    public void doStop()
    {
        Intent selected_intent = new Intent(MainActivity.this, EndActivity.class);
        startActivityForResult(selected_intent, CODE_FOR_CHECK_SCORE);
        missileMaker.setRunning(false);
        finish();
    }
    public void callInterceptor(double startX, double startY, float x1, float y1)
    {
        Interceptor i = new Interceptor(this,  (float) (startX + 50), (float) (startY+20), x1, y1);
        SoundPlayer.getInstance().start("launch_interceptor"); //play the boom sound
        i.launch();
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public void removeMissile(Missile missile)
    {
        missileMaker.removeMissile(missile);
    }

    public void setLevel(final int levelNum) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                level.setText(String.format(Locale.getDefault(), "Level: %d", levelNum));
                levelValue = levelNum;
            }
        });
    }

  /*  public int getLevel()
    {
        return levelValue;
    }*/


    public void applyMissileBlast(Missile missile, int id) {
        baseLauncher.applyMissileBlast(missile,id);
    }



    public void applyInterceptorBlast(Interceptor interceptor, int id) {
        missileMaker.applyInterceptorBlast(interceptor,id);
    }

    public int getScore()
    {
        return scoreValue;
    }
    public void incrementScore(int scoreValue) {
        Log.d(TAG, "incrementScore: "+scoreValue);
        this.scoreValue = scoreValue;
        score.setText(String.format(Locale.getDefault(), "%d", scoreValue));
    }

    private boolean checkNotReady() {
        if (SoundPlayer.loadCount != SoundPlayer.doneCount) {
            String msg = String.format(Locale.getDefault(),
                    "Sound loading not complete (%d of %d),\n" +
                            "Please try again in a moment",
                    SoundPlayer.doneCount, SoundPlayer.loadCount);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_FOR_CHECK_SCORE) {
            if(requestCode == RESULT_OK)
            {

            }
        }
    }*/
}
