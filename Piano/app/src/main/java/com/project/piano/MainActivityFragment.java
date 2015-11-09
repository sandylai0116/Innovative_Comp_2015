package com.project.piano;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.twobard.pianoview.Piano;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    PianoSheet pianoSheet;
    Button clearNotes;
    ToggleButton recording;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Piano piano = (Piano) view.findViewById(R.id.piano_keyboard);
        pianoSheet = (PianoSheet) view.findViewById(R.id.music_sheet);
        clearNotes = (Button)view.findViewById(R.id.clear_notes);
        recording = (ToggleButton)view.findViewById(R.id.recording);

        //listener
        piano.setPianoKeyListener(onPianoKeyPress);
        clearNotes.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> noteList = new ArrayList<>();
                pianoSheet.setNoteList(noteList);
                pianoSheet.invalidate();
            }
        });
        recording.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("recording button", "isChecked" + isChecked);
            }
        });


        //generate notes
        List<Integer> noteList = new ArrayList<>();
        noteList.add(1);
        pianoSheet.setNoteList(noteList);
        pianoSheet.invalidate();

        return view;
    }

    private Piano.PianoKeyListener onPianoKeyPress=
            new Piano.PianoKeyListener() {

                @Override
                public void keyPressed(int id, int action) {
                    if(action == 0) {
                        Log.i("Keyboard", "Key pressed: " + id);
                        Map<Integer,Integer> keyMap = new HashMap<>();
                        keyMap.put(0,0);
                        keyMap.put(2,1);
                        keyMap.put(4,2);
                        keyMap.put(5,3);
                        keyMap.put(7,4);
                        keyMap.put(9, 5);
                        keyMap.put(11, 6);
                        keyMap.put(12, 7);

                        if(keyMap.containsKey(id)) {
                            List<Integer> noteList = pianoSheet.getNoteList();
                            noteList.add(keyMap.get(id));
                            pianoSheet.setNoteList(noteList);
                            pianoSheet.invalidate();
                        }
                    }
                }
            };
}
