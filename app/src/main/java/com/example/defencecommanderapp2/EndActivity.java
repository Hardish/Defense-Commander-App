package com.example.defencecommanderapp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

import static com.example.defencecommanderapp2.MainActivity.scoreValue;
import static com.example.defencecommanderapp2.MainActivity.levelValue;

public class EndActivity extends AppCompatActivity {
    private static final int CODE_FOR_PRINT_ACTIVITY = 123 ;
    private int screenWidth, screenHeight;
    private static final String TAG = "EndActivity";
    private ConstraintLayout layout;
    private int current_score;
    private int current_level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        layout = findViewById(R.id.endlayout);
        new ScrollingCloud(this,layout,R.drawable.clouds,9000);
        setupFullScreen();
        getScreenDimensions();

        checkTop10Score();
    }

    private void checkTop10Score()
    {
        current_score = scoreValue;
        GameDatabaseHandler gameDatabaseHandler = new GameDatabaseHandler(this);
        gameDatabaseHandler.execute(String.format(Locale.getDefault(), "%d", current_score));
        //pass your score to gameDatabasehandler
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

    public ConstraintLayout getLayout() {
        return layout;
    }

    public void setResult(String s) {
        Intent selected_intent = new Intent(EndActivity.this, DisplayScoreActivity.class);
        selected_intent.putExtra("print",s);
        startActivityForResult(selected_intent,CODE_FOR_PRINT_ACTIVITY);
    }

    public void updateScoreInTable() {
        current_level = levelValue;
        // Single input value dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please enter your name initials(up to 3 characters)");
        builder.setTitle("You are Top-Player!");
        // Create an edittext and set it to be the builder's view
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        //convert all into upper character
        et.setFilters(new InputFilter[]
                {
                        new InputFilter.AllCaps(),
                        new InputFilter.LengthFilter(3)
                });

        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Log.d(TAG, "onClick: "+ et.getText().toString());
                //send data back to GameDatabaseHandler
                insertScore(et.getText().toString(),current_level);
            }
        });
        builder.setNegativeButton("NO WAY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(EndActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();
                //tv1.setText(R.string.no_way);
            }
        });



        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void insertScore(String initalize, int current_level) {
        current_score = scoreValue;
        GameDatabaseHandler gameDatabaseHandler = new GameDatabaseHandler(this);
        gameDatabaseHandler.execute(String.valueOf(current_score),initalize,String.valueOf(current_level));
    }
}
