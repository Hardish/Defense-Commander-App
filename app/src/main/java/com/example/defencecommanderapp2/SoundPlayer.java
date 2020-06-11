package com.example.defencecommanderapp2;

import android.content.Context;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;

class SoundPlayer {

    private static final String TAG = "SoundPlayer";
    private static SoundPlayer instance;
    private SoundPool soundPool;
    private static final int MAX_STREAMS = 30;
    private HashSet<Integer> loaded = new HashSet<>();
    private HashMap<String, Integer> soundNameToResource = new HashMap<>();
    private HashSet<String> loopList = new HashSet<>();

    static int loadCount;
    static int doneCount;
    private SoundPlayer() {

        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(MAX_STREAMS);
        this.soundPool = builder.build();

        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.d(TAG, "onLoadComplete: #" + sampleId + "  " + status);
                loaded.add(sampleId);
                doneCount++;
            }
        });

    }

    static SoundPlayer getInstance() {
        if (instance == null)
            instance = new SoundPlayer();
        return instance;
    }

    void setupSound(Context context, String id, int resource, boolean loop) {
        int soundId = soundPool.load(context, resource, 1);
        soundNameToResource.put(id, soundId);
        if (loop)
            loopList.add(id);
        Log.d(TAG, "setupSound Loading: " + id + ": #" + soundId);
        loadCount++;  //its load initiatave count
        Log.d(TAG, "setupSound: " + id + ": #" + soundId);
       /* try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }



    void start(final String id) {

        if (!loaded.contains(soundNameToResource.get(id))) {
            Log.d(TAG, "start: SOUND NOT LOADED: " + id);
            return;
        }
        int loop = 0;
        if (loopList.contains(id))
            loop = -1;  //kind of like infinite looping

        Integer resId = soundNameToResource.get(id);
        if (resId == null)
            return;
        soundPool.play(resId, 1f, 1f, 1, loop, 1f);
    }

}
