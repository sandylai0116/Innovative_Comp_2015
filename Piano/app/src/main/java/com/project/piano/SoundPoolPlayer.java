package com.project.piano;

/**
 * Created by katung on 9/11/2015.
 */
import android.content.Context;
import android.media.*;
import android.util.Log;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class SoundPoolPlayer {
    private SoundPool mShortPlayer= null;
    private HashMap mSounds = new HashMap();
    private VariableChangeListener variableChangeListener;

    private boolean isPlaying = false;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setVariableChangeListener(VariableChangeListener variableChangeListener) {
        this.variableChangeListener = variableChangeListener;
    }

    public interface VariableChangeListener {
        public void onVariableChanged(boolean isPlaying, int count);
    }

    public SoundPoolPlayer(Context pContext)
    {
        // setup Soundpool
        this.mShortPlayer = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
        mSounds.put(1, this.mShortPlayer.load(pContext, R.raw.s40, 1));
        mSounds.put(2, this.mShortPlayer.load(pContext, R.raw.s42, 1));
        mSounds.put(3, this.mShortPlayer.load(pContext, R.raw.s44, 1));
        mSounds.put(4, this.mShortPlayer.load(pContext, R.raw.s45, 1));
        mSounds.put(5, this.mShortPlayer.load(pContext, R.raw.s47, 1));
        mSounds.put(6, this.mShortPlayer.load(pContext, R.raw.s49, 1));
        mSounds.put(7, this.mShortPlayer.load(pContext, R.raw.s51, 1));
        mSounds.put(8, this.mShortPlayer.load(pContext, R.raw.s52, 1));
    }

    public void playShortResource(int piResource) {
        int iSoundId = (Integer) mSounds.get(piResource);
        this.mShortPlayer.play(iSoundId, 0.99f, 0.99f, 0, 0, 1);
    }
    public void playPiano(String notes) {
        Timer timer = new Timer(true);
        String[] note = notes.split(" ");
        int[] noteIDs = new int[note.length];
        for (int i = 0;i<note.length;i++){
            noteIDs[i] = (Integer) mSounds.get(Integer.parseInt(note[i]));
        }
        timer.schedule(new task(this.mShortPlayer,noteIDs, timer),0,500);
    }
    private class task extends TimerTask
    {
        private int[] resourceID;
        private SoundPool innerPlayer = null;
        private int count = 0;
        private Timer timer = null;
        task(SoundPool player,int[] ID, Timer timer){
            this.resourceID=ID;
            this.innerPlayer=player;
            this.timer = timer;
        }
        public void run(){
            if (count < resourceID.length) {
                innerPlayer.play(resourceID[count], 0.99f, 0.99f, 0, 0, 1);
                count++;
                isPlaying = true;
            }
            else {
                timer.cancel();
                count = 0;
                isPlaying = false;
            }
            if(variableChangeListener!=null)variableChangeListener.onVariableChanged(isPlaying,count);
        }
    }
    // Cleanup
    public void release() {
        this.mShortPlayer.release();
        this.mShortPlayer = null;
    }
}

