package com.example.defencecommanderapp2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;


public class BaseLauncher {
    private static final String TAG = "BaseLauncher";
    private MainActivity mainActivity;
    private ImageView launcher1;
    private ImageView launcher2;
    private ImageView launcher3;
    public ArrayList<ImageView> activeBase = new ArrayList<ImageView>();

    public BaseLauncher(MainActivity mainActivity, ImageView launcher1, ImageView launcher2, ImageView launcher3) {
        this.mainActivity = mainActivity;
        this.launcher1 = launcher1;
        this.launcher2 = launcher2;
        this.launcher3 = launcher3;
        setupActiveImage(launcher1,launcher2,launcher3);
    }

    private void setupActiveImage(ImageView launcher1, ImageView launcher2, ImageView launcher3) {
        activeBase.add(launcher1);
        activeBase.add(launcher2);
        activeBase.add(launcher3);
    }

    public void setBaseImageOnScreen(float x1, float y1) {

            ImageView nearestLaunchingBase = checkClosestLauncherbase(x1,y1);
            if(nearestLaunchingBase == null)
            {
                Log.d(TAG, "setBaseImageOnScreen: All base are destroyed");
            }
            else
            {
                double startX = nearestLaunchingBase.getX();   //taking launcher x and y
                double startY = nearestLaunchingBase.getY();
                mainActivity.callInterceptor(startX,startY,x1,y1);
            }


    }

    private ImageView checkClosestLauncherbase(float x, float y) {
        float distanceForLuncherBase1 = 0.0f;
        float distanceForLuncherBase2 = 0.0f;
        float distanceForLuncherBase3 = 0.0f;
        float clickedX = x;
        float clickedY = y;

        if(activeBase.size() != 0)
        {
            for(int i = 0; i<activeBase.size();i++)
            {
                ImageView temp = activeBase.get(i);
                float launcherX = temp.getX();
                float launcherY = temp.getY();

                if(temp.getId() == launcher1.getId())
                {
                    distanceForLuncherBase1 = findDistanceFromClick(clickedX,clickedY,temp,launcherX,launcherY);
                }
                if(temp.getId() == launcher2.getId())
                {
                    distanceForLuncherBase2 = findDistanceFromClick(clickedX,clickedY,temp,launcherX,launcherY);
                }
                if(temp.getId() == launcher3.getId())
                {
                    distanceForLuncherBase3 = findDistanceFromClick(clickedX,clickedY,temp,launcherX,launcherY);
                }

            }
            float result_distance = findMinimumFromAllBase(distanceForLuncherBase1,distanceForLuncherBase2,distanceForLuncherBase3);
            ImageView result_launcher = findNearestBaseStationImage(result_distance,distanceForLuncherBase1,distanceForLuncherBase2,distanceForLuncherBase3);
            return result_launcher;
        }
        else {
            return null;
        }
       
    }

    private ImageView findNearestBaseStationImage(float resultDistance, float distanceForLuncherBase1, float distanceForLuncherBase2, float distanceForLuncherBase3) {
        if(resultDistance == distanceForLuncherBase1 && distanceForLuncherBase1 != 0.0f)
        {
           // if(launcher1.getId() == activeBase.get(0).getId())
            return launcher1;
        }
        else if(resultDistance == distanceForLuncherBase2 && distanceForLuncherBase2 != 0.0f )
        {
          //  if(launcher2.getId() == activeBase.get(1).getId())
            return launcher2;
        }
        else if(resultDistance == distanceForLuncherBase3 && distanceForLuncherBase3 != 0.0f)
        {
           // if(launcher3.getId() == activeBase.get(2).getId())
            return launcher3;
        }
        return null;
    }

    private float findMinimumFromAllBase(float distanceForLuncherBase1, float distanceForLuncherBase2, float distanceForLuncherBase3) {
        float distance;

        if(distanceForLuncherBase1 != 0.0f && distanceForLuncherBase2 != 0.0f && distanceForLuncherBase3 != 0.0f)
        {
            distance = Math.min(Math.min(distanceForLuncherBase1, distanceForLuncherBase2), distanceForLuncherBase3);
            return distance;
        }
        else if(distanceForLuncherBase1 == 0.0f && distanceForLuncherBase2 != 0.0f && distanceForLuncherBase3 != 0.0f)
        {
            distance = Math.min(distanceForLuncherBase2,distanceForLuncherBase3);
            return distance;
        }
        else if(distanceForLuncherBase2 == 0.0f && distanceForLuncherBase1 != 0.0f && distanceForLuncherBase3 != 0.0f)
        {
            distance = Math.min(distanceForLuncherBase1,distanceForLuncherBase3);
            return distance;
        }
        else if(distanceForLuncherBase3 == 0.0f && distanceForLuncherBase2 != 0.0f && distanceForLuncherBase1 != 0.0f)
        {
            distance = Math.min(distanceForLuncherBase2,distanceForLuncherBase1);
            return distance;
        }
        else if(distanceForLuncherBase1 == 0.0f && distanceForLuncherBase2 == 0.0f )
        {
            //one base alive 3
            return  distanceForLuncherBase3;
        }
        else if(distanceForLuncherBase1 == 0.0f && distanceForLuncherBase3 == 0.0f)
        {
            return distanceForLuncherBase2;
        }
        else if(distanceForLuncherBase2 == 0.0f && distanceForLuncherBase3 == 0.0f)
        {
            return distanceForLuncherBase1;
        }
        return 0.0f;
    }

    private float findDistanceFromClick(float clickedX, float clickedY, ImageView temp, float launcherX, float launcherY) {
       return (float) Math.sqrt((clickedY - launcherY) * (clickedY - launcherY) +
                (clickedX - launcherX) * (clickedX - launcherX));
    }

    public void applyMissileBlast(Missile missile, int id) {
        float x1 = missile.getX();
        float y1 = missile.getY();
        for(int i =0 ;i <activeBase.size(); i++)
        {
            float x2 = (int) (activeBase.get(i).getX() + (0.5 * activeBase.get(i).getWidth()));
            float y2 = (int) (activeBase.get(i).getY() + (0.5 * activeBase.get(i).getHeight()));

            float f = (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));

            if(f< 250)
            {
                SoundPlayer.getInstance().start("base_blast");
                baseBlastMissile(x2,y2,activeBase.get(i));
                activeBase.remove(i);
                checkBasePresent();

            }
        }
    }

    private void checkBasePresent() {
        if(activeBase.size() == 0)
        {
            mainActivity.doStop();
        }
    }

    private void baseBlastMissile(float x2, float y2, final ImageView imageView) {
        //final ImageView iv = new ImageView(this);
        ImageView iv = imageView;
        iv.setImageResource(R.drawable.explode);  //bigger on explosion image

        iv.setTransitionName("Missile Intercepted Blast");

        int w = imageView.getDrawable().getIntrinsicWidth();
        int offset = (int) (w * 0.5);

        iv.setX(x2 - offset);
        iv.setY(y2 - offset);
        iv.setRotation((float) (360.0 * Math.random()));   //randomly rotation of blast image

        //aSet.cancel();  //stop current movement

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
