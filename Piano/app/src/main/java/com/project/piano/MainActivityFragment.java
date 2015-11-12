package com.project.piano;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ToggleButton;

import com.twobard.pianoview.Piano;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private PianoSheet pianoSheet;
    private Button clearNotes;
    private Button playButton;
    private ToggleButton recording;
    private  HorizontalScrollView pianoSheetLayout;
    private SoundPoolPlayer soundForPlayButton;
    private SoundPoolPlayer soundForKeyboard;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Piano piano = (Piano) view.findViewById(R.id.piano_keyboard);
        pianoSheet = (PianoSheet) view.findViewById(R.id.music_sheet);
        clearNotes = (Button)view.findViewById(R.id.clear_notes);
        playButton = (Button)view.findViewById(R.id.play);
        recording = (ToggleButton)view.findViewById(R.id.recording);
        pianoSheetLayout = (HorizontalScrollView)view.findViewById(R.id.music_sheet_layout);
        soundForPlayButton = new SoundPoolPlayer(getActivity());
        soundForKeyboard = new SoundPoolPlayer(getActivity());
        //listener
        piano.setPianoKeyListener(onPianoKeyPress);
        soundForPlayButton.setVariableChangeListener(onSoundChange);
        playButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!soundForPlayButton.isPlaying()) {
                    pianoSheetLayout.scrollTo(0, 0);
                    String notes = "";
                    Iterator<Integer> it = pianoSheet.getNoteList().iterator();
                    while (it.hasNext()) {
                        int musicTransform = it.next() + 1;
                        notes += musicTransform + " ";
                    }
                    if (!notes.equals("")) soundForPlayButton.playPiano(notes);
                }
            }
        });
        clearNotes.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> noteList = new ArrayList<>();
                pianoSheet.setNoteList(noteList);
                pianoSheet.invalidate();
                pianoSheetLayout.scrollTo(0,0);
            }
        });
        recording.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("recording button", "isChecked" + isChecked);
            }
        });


        //generate notes
        /*
            List<Integer> noteList = new ArrayList<>();
            noteList.add(1);
            pianoSheet.setNoteList(noteList);
            pianoSheet.invalidate();
        */




        return view;
    }
    private SoundPoolPlayer.VariableChangeListener onSoundChange=
            new SoundPoolPlayer.VariableChangeListener() {
                @Override
                public void onVariableChanged(boolean isPlaying, final int count){
                    pianoSheet.setCurrentPosition(count - 1);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pianoSheet.invalidate();
                            if((count-1)%6==0 && count!=1)pianoSheetLayout.scrollBy(150*6, 0);
                        }
                    });
                }
            };

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
                        Map<Integer,Integer> musicMap = new HashMap<>();
                        musicMap.put(0,1);
                        musicMap.put(2,2);
                        musicMap.put(4,3);
                        musicMap.put(5,4);
                        musicMap.put(7,5);
                        musicMap.put(9, 6);
                        musicMap.put(11, 7);
                        musicMap.put(12, 8);
                        if(keyMap.containsKey(id)) {
                            soundForKeyboard.playPiano(String.valueOf(musicMap.get(id)));
                            List<Integer> noteList = pianoSheet.getNoteList();
                            noteList.add(keyMap.get(id));
                            pianoSheet.setNoteList(noteList);
                            pianoSheet.invalidate();
                            if((noteList.size()-1)%6==0 && noteList.size()!=1)pianoSheetLayout.scrollBy(150*6, 0);
                        }
                    }
                }
            };
}
