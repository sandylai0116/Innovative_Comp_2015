package com.project.piano;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.twobard.pianoview.Piano;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Piano piano = (Piano) view.findViewById(R.id.piano_keyboard);
        piano.setPianoKeyListener(onPianoKeyPress);
        return view;
    }

    private Piano.PianoKeyListener onPianoKeyPress=
            new Piano.PianoKeyListener() {

                @Override
                public void keyPressed(int id, int action) {
                    Log.i("Keyboard", "Key pressed: " + id);
                }
            };
}
