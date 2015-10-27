package com.twobard.pianoview;

import android.util.Log;

import java.util.ArrayList;

public class Finger {
    private ArrayList<Key> keys = new ArrayList<Key>();
    
    public Boolean isPressing(Key key){
        return this.keys.contains(key);
    }

    public void press(Key key){
        if(key == null) return;
        if(this.isPressing(key)){
            return;
        }
        key.press(this);
        this.keys.add(key);
    }
    
    public void lift(){
        for(Key key : keys){
            key.depress(this);
        }
        keys.clear();
    }
}
