package com.example.defencecommanderapp2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import static com.example.defencecommanderapp2.MainActivity.screenHeight;
import static com.example.defencecommanderapp2.MainActivity.screenWidth;


public class ScrollingCloud {

    private static final String TAG = "ScrollingCloud";
    private Context context;
    private ViewGroup layout;
    private ImageView backImageA;
    private ImageView backImageB;
    private long duration;
    private int resId;

    public ScrollingCloud(MainActivity context, ViewGroup layout, int resId, long duration) {
        this.context = context;
        this.layout = layout;
        this.resId = resId;
        this.duration = duration;  //one full image in 4 second(time in which you want to scroll whole one image)
        setupBackground();
    }

    public ScrollingCloud(EndActivity endActivity, ConstraintLayout layout, int resId, int duration) {
        context = endActivity;
        this.layout = layout;
        this.resId = resId;
        this.duration = duration;  //one full image in 4 second(time in which you want to scroll whole one image)
        setupBackground();
    }

    private void setupBackground() {
        backImageA = new ImageView(context);
        backImageB = new ImageView(context);

        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(screenWidth + getBarHeight(),screenHeight);  //size for imageview to layout
        backImageA.setLayoutParams(params);  //setting size of imageview
        backImageB.setLayoutParams(params);

        layout.addView(backImageA);
        layout.addView(backImageB);
        
        backImageA.setImageResource(resId);
        backImageB.setImageResource(resId);

        backImageA.setScaleType(ImageView.ScaleType.FIT_XY);
        backImageB.setScaleType(ImageView.ScaleType.FIT_XY);

        animateBack();
        fadeCloudImage();
    }

    private void animateBack() {

        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {


            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d(TAG, "onAnimationUpdate: "+animation.getAnimatedValue());

                final float progress = (float) animation.getAnimatedValue();  //current value came out the value
                float width = screenWidth + getBarHeight();

                float a_translationX = width * progress; //how far the image a
                float b_translationX = width * progress - width;  //this one is off screen.

                backImageA.setTranslationX(a_translationX); //how much to move it setting delta x
                backImageB.setTranslationX(b_translationX);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fadeCloudImage();

            }

        });
        //fadeCloudImage();
        animator.start();
    }

    private void fadeCloudImage() {
        final ObjectAnimator alphaA = ObjectAnimator.ofFloat(backImageA, "alpha", 0.9f,0.25f);
        alphaA.setInterpolator(new LinearInterpolator());
        alphaA.setDuration(10000);
        alphaA.setRepeatCount(ValueAnimator.INFINITE);
        alphaA.setRepeatMode(ValueAnimator.REVERSE);


        final ObjectAnimator alphaB = ObjectAnimator.ofFloat(backImageB, "alpha", 0.9f,0.25f);
        alphaB.setInterpolator(new LinearInterpolator());
        alphaB.setDuration(10000);
        alphaB.setRepeatCount(ValueAnimator.INFINITE);
        alphaB.setRepeatMode(ValueAnimator.REVERSE);

        alphaB.start();
        alphaA.start();
    }

    private int getBarHeight() {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
