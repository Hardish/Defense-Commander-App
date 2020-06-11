package com.example.defencecommanderapp2;

import android.animation.AnimatorSet;
import android.util.Log;
import java.util.ArrayList;
import static com.example.defencecommanderapp2.Missile.INTERCEPTOR_BLAST;
import static com.example.defencecommanderapp2.MainActivity.levelValue;

public class MissileMaker implements Runnable{

    private static final String TAG = "MissileMaker";
    private MainActivity mainActivity;
    private int screenWidth, screenHeight;
    private boolean isRunning;
    private ArrayList<Missile> activeMissiles = new ArrayList<>();
    private static final int Missile_PER_Level = 7;
    private int level_count = 0;
    private long delay;
    private AnimatorSet as;

    public MissileMaker(MainActivity mainActivity, int screenWidth, int screenHeight)
    {
        this.mainActivity = mainActivity;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void setRunning(boolean running) {
        isRunning = running;
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);
        for(Missile m: temp)
        {
            m.stop();
        }
    }
    @Override
    public void run() {
        setRunning(true);
        delay = Missile_PER_Level * 1000;
        sleepForFirstTime(delay);

        while (isRunning)
        {
            int resId = pickMissile();
            CreateMissleMaker(screenHeight,screenWidth,delay,mainActivity,resId);
            checkMissileCount();
            long updatedDelay = getSleepTime(delay);
            sleepForFirstTime(updatedDelay);
        }

    }

    private void CreateMissleMaker(int screenHeight, int screenWidth, long delay, MainActivity mainActivity, int resId) {
        long missileTime = (long) ((delay * 0.5));
        final Missile missile = new Missile(screenWidth,screenHeight,missileTime,mainActivity);
        activeMissiles.add(missile);
        as = missile.setData(resId);
        SoundPlayer.getInstance().start("launch_missile"); //playing sound
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                as.start();
            }
        });
    }

    private void sleepForFirstTime(long delay)
    {
        try {
            Thread.sleep((long) (delay * 0.5));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private long getSleepTime(long delay) {
        float random_numbe = (float) Math.random();
        if(random_numbe < 0.1)
        {
            Log.d(TAG, "getSleepTime: < 0.1" + 1);
            return 1;
        }
        else if(random_numbe <0.2)
        {
            Log.d(TAG, "getSleepTime: < 0.2 " + (long) (0.5 * delay));
           return (long) (0.5 * delay);
        }
        else
        {
            Log.d(TAG, "getSleepTime: else condition" +delay);
             return delay;
        }

    }

    private void checkMissileCount() {
        if(level_count > Missile_PER_Level)
        {
            increaseLevel(level_count);
            level_count = 0;
        }
        else
        {
            level_count = level_count + 1;
        }

    }

    private void increaseLevel(int level_count) {
        level_count++;
        long delayResult = delay - 500;
        Log.d(TAG, "increaseLevel: Subtraction " +delayResult);
        if(delayResult <= 0)
        {
            delay = 1;
            Log.d(TAG, "increaseLevel: Delay 1");
        }
        else
        {
            //nothing
            delay = delayResult;
            Log.d(TAG, "increaseLevel: level count is not above 7" + delay );
        }

        displayOnScreen(levelValue);
        getSleepTime(2000);
    }

    private void displayOnScreen(int level_count) {
        mainActivity.setLevel(level_count);
    }

    private int pickMissile() {
        return R.drawable.missile;
    }


    public void applyInterceptorBlast(Interceptor interceptor, int id) {
        //increase score
        Log.d(TAG, "applyInterceptorBlast: INCREASE IN SCORE");

        float x1 = interceptor.getX();
        float y1 = interceptor.getY();

        Log.d(TAG, "applyInterceptorBlast: INTERCEPTOR: " + x1 + ", " + y1);

        ArrayList<Missile> nowGone = new ArrayList<>();
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);

        for(Missile m: temp)
        {
            //x1 y1 is missile
            float x2 = (int) (m.getX() + (0.5 * m.getWidth()));
            float y2 = (int) (m.getY() + (0.5 * m.getHeight()));

            Log.d(TAG, "applyInterceptorBlast:    Missile: " + x2 + ", " + y2);
            float f = (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
            Log.d(TAG, "applyInterceptorBlast:    DIST: " + f);

            if (f < INTERCEPTOR_BLAST) {
                SoundPlayer.getInstance().start("interceptor_blast");
                int previousScore = mainActivity.getScore();
                mainActivity.incrementScore(previousScore+1);
                Log.d(TAG, "applyInterceptorBlast:    Hit: " + f);
                m.interceptorBlast(x2, y2);
                nowGone.add(m);  //add for remove

            }

        }

        for (Missile m : nowGone) {
            activeMissiles.remove(m);
        }
    }

    public void removeMissile(Missile missile) {
        activeMissiles.remove(missile);
    }
}
