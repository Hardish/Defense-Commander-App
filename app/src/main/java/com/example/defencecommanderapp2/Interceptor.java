package com.example.defencecommanderapp2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Interceptor {
    private static int count = 0;
    private int id;
    private MainActivity mainActivity;
    private ImageView imageview;
    private static final String TAG = "Interceptor";
    private float startX, startY, endX, endY;
    private ObjectAnimator moveX, moveY;
    private static int idVal = -1;
    static final int INTERCEPTOR_BLAST = 180;  //range of explosion
    // private static final double DISTANCE_TIME = 0.75;
    //pixel per second where 1 second = 1000 ms as here everything is in ms
    private float pps = (float) (MainActivity.getScreenWidth() * 0.85); //time to cross the width

    public Interceptor(MainActivity mainActivity, float startX, float startY, float endX, float endY) {
        this.mainActivity = mainActivity;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.id = count++;
        initialize();
    }

    private void initialize() {
        imageview = new ImageView(mainActivity);
        SoundPlayer.getInstance().start("launch_interceptor"); //playing sound
        //imageview.setId(idVal--);  //ignore this
        imageview.setImageResource(R.drawable.interceptor);
       // imageview.setTransitionName("Interceptor " + id); //ignore as not prof not shown work

        final int www = (int) (imageview.getDrawable().getIntrinsicWidth() * 0.5);  //half way image

        imageview.setX(startX);
        imageview.setY(startY);
        imageview.setZ(-10);  // how far behind compare to other items in screen

        //to look like explosion  happen on top of the click not below the click
        //image3
        endX -= www;
        endY -= www;

        //same pointing at launcher
        float a = calculateAngle(imageview.getX(), imageview.getY(), endX, endY);  //missile image angle

        imageview.setRotation(a);

        mainActivity.getLayout().addView(imageview);

        //below code is movement need to work
        //distance
        double distance =  Math.sqrt((endY - imageview.getY()) * (endY - imageview.getY()) + (endX - imageview.getX()) * (endX - imageview.getX()));
        long duration = (long) (distance / pps * 1000.0);

        moveX = ObjectAnimator.ofFloat(imageview, "x", endX);
        moveX.setInterpolator(new AccelerateInterpolator());  //AccelerateInterpolator: missile use to be slow at launch time but pick up speed after that
        //moveX.setDuration((long) (distance * DISTANCE_TIME));
        moveX.setDuration(duration);


        moveY = ObjectAnimator.ofFloat(imageview, "y", endY);
        moveY.setInterpolator(new AccelerateInterpolator());  //AccelerateInterpolator: missile use to be slow at launch time but pick up speed after that
        //moveY.setDuration((long) (distance * DISTANCE_TIME));
        moveY.setDuration(duration);

        moveX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(imageview); //remove the missile from screen
                makeBlast(); // make blast
            }
        });

    }

    private void makeBlast() {

        final ImageView explodeView = new ImageView(mainActivity);
        // but we have work around it. drawable have size
        explodeView.setImageResource(R.drawable.i_explode);

        explodeView.setTransitionName("Interceptor blast");

        float w = explodeView.getDrawable().getIntrinsicWidth();  //width and height from drawable. but as it square, width is enough

        explodeView.setX(this.getX() - (w/2));

        explodeView.setY(this.getY() - (w/2));

        explodeView.setZ(-15);

        mainActivity.getLayout().addView(explodeView);

        //after explode, it should go away blast
        final ObjectAnimator alpha = ObjectAnimator.ofFloat(explodeView, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000); //within 3 second
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(explodeView); //remove the imageview from screen
            }
        });
        alpha.start();

        mainActivity.applyInterceptorBlast(this, imageview.getId());
    }

    /*private void makeBlast() {
        //SoundPlayer.getInstance().start("interceptor_blast"); //playing sound
        //imageview before it get display have no width or height
        final ImageView explodeView = new ImageView(mainActivity);
        // but we have work around it. drawable have size
        explodeView.setImageResource(R.drawable.i_explode);

        explodeView.setTransitionName("Interceptor blast");

        float w = explodeView.getDrawable().getIntrinsicWidth();  //width and height from drawable. but as it square, width is enough

        explodeView.setX(this.getX() - (w/2));

        explodeView.setY(this.getY() - (w/2));

        explodeView.setZ(-15);

        mainActivity.getLayout().addView(explodeView);

        //after explode, it should go away blast
        final ObjectAnimator alpha = ObjectAnimator.ofFloat(explodeView, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000); //within 3 second
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(explodeView); //remove the imageview from screen
            }
        });
        alpha.start();

        mainActivity.applyInterceptorBlast(this, imageview.getId());
    }*/

    private float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        // Keep angle between 0 and 360
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (180.0f - angle);
    }

    public void launch() {
        moveX.start();
        moveY.start();
    }
    float getX() {
        int xVar = imageview.getWidth() / 2;
        return imageview.getX() + xVar;
    }

    float getY() {
        int yVar = imageview.getHeight() / 2;
        return imageview.getY() + yVar;
    }
}
