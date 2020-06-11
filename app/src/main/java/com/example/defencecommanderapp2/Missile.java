package com.example.defencecommanderapp2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.Random;

public class Missile {
    private static final String TAG = "Missile";
    private MainActivity mainActivity;
    private ImageView imageView;
    private AnimatorSet aSet = new AnimatorSet();
    private int screenHeight;
    private int screenWidth;
    private long screenTime;
    private boolean hit = false;
    private float endX, endY;
    private float startX,startY;
    private float pps = (float) (MainActivity.getScreenHeight() * 0.85);
    private ObjectAnimator moveX, moveY;
    static final int INTERCEPTOR_BLAST = 120;

    public Missile(int screenWidth, int screenHeight, long missileTime, final MainActivity mainActivity) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.mainActivity = mainActivity;
        this.screenTime = missileTime;


        imageView = new ImageView(mainActivity);
        imageView.setX(-500);
        Random rand = new Random();
       // startX = (float)(imageView.getX() + (0.5 * imageView.getWidth())) * 0.85f * rand.nextFloat();
        startX = (float) (Math.random() * screenWidth * 0.8);
       // int startX = (int) (Math.random() * screenWidth * 0.8);
        startY = (float) (imageView.getY() + (0.5 * imageView.getHeight()));
        endX = ((rand.nextFloat() * screenWidth) + (Math.random() < 0.5 ? 150 : -150));
        endY = (screenHeight+ (Math.random() < 0.5 ? 800 : -150));

        float a = calang(startX, startY, endX, endY);
        imageView.setRotation(a);
        imageView.setZ(-15);
        /*imageView.setX(startX);
        imageView.setY(startY);
        imageView.setZ(-10);*/
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.getLayout().addView(imageView);
            }
        });
    }

    private float calang(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1)); //Math.atan2(x2 - x1, y2 - y1) return radiant
        // Keep angle between 0 and 360
        angle = angle + Math.ceil(-angle / 360) * 360;  //stay between 0 to 360
        return (float) (190.0f - angle); //normally 120 work but it makes look
    }

    public AnimatorSet setData(final int drawId) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(drawId);
            }
        });

/*        int startY = (int) (Math.random() * screenHeight * 0.8);
        int endY = (startY + (Math.random() < 0.5 ? 150 : -150));*/

        final ObjectAnimator yAnim = ObjectAnimator.ofFloat(imageView, "y", -200, (screenHeight + 200));
        yAnim.setInterpolator(new LinearInterpolator());
        yAnim.setDuration(screenTime);
        yAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!hit) {
                            mainActivity.getLayout().removeView(imageView);
                            mainActivity.removeMissile(Missile.this);
                        }
                        Log.d(TAG, "run: NUM VIEWS " +
                                mainActivity.getLayout().getChildCount());
                    }
                });



            }
        });

        yAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if((float)(yAnim.getAnimatedValue()) > screenHeight * 0.85f)
                {
                    makeGroundBlast();
                }

            }
        });

        ObjectAnimator xAnim = ObjectAnimator.ofFloat(imageView, "x", (int) (startX), (int) (endX));
        xAnim.setInterpolator(new LinearInterpolator());
        xAnim.setDuration(screenTime);

        aSet.playTogether(xAnim, yAnim);
        return aSet;
    }
    float getX() {
        int xVar = imageView.getWidth() / 2;
        return imageView.getX() + xVar;
    }

    float getY() {
        int yVar = imageView.getHeight() / 2;
        return imageView.getY() + yVar;
    }

    float getWidth() {
        return imageView.getWidth();
    }

    float getHeight() {
        return imageView.getHeight();
    }

    private void makeGroundBlast() {
        Log.d(TAG, "makeGroundBlast: called");
        aSet.cancel();
        // SoundPlayer.getInstance().start("interceptor_blast"); //playing sound
        //imageview before it get display have no width or height
        final ImageView explodeView = new ImageView(mainActivity);
        // but we have work around it. drawable have size
        explodeView.setImageResource(R.drawable.i_explode);

        explodeView.setTransitionName("Interceptor blast");

        float w = explodeView.getDrawable().getIntrinsicWidth();  //width and height from drawable. but as it square, width is enough

        explodeView.setX(this.getX() - (w/2));

        explodeView.setY(this.getY() - (w/2));

        explodeView.setZ(-30);
        mainActivity.getLayout().removeView(imageView);
        mainActivity.getLayout().addView(explodeView);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(explodeView, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(explodeView);
            }
        });
        alpha.start();
        mainActivity.applyMissileBlast(this,imageView.getId());
    }


    public void stop() {
        aSet.cancel();
    }

    public void interceptorBlast(float x2, float y2) {

        final ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.explode);  //bigger on explosion image

        iv.setTransitionName("Missile Intercepted Blast");

        int w = imageView.getDrawable().getIntrinsicWidth();
        int offset = (int) (w * 0.5);

        iv.setX(x2 - offset);
        iv.setY(y2 - offset);
        iv.setRotation((float) (360.0 * Math.random()));   //randomly rotation of blast image

        aSet.cancel();  //stop current movement

        mainActivity.getLayout().removeView(imageView);  //remove the plain from screen
        mainActivity.getLayout().addView(iv);  //replacing with explosion image

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(imageView);
            }
        });
        alpha.start();

    }

}