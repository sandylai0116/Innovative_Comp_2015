package com.project.piano;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.twobard.pianoview.Piano;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.util.PitchConverter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private Piano piano;
    private PianoSheet pianoSheet;
    private Button clearNotes;
    private Button playButton;
    private ToggleButton recording;
    private  HorizontalScrollView pianoSheetLayout;
    private SoundPoolPlayer soundForPlayButton;
    private SoundPoolPlayer soundForKeyboard;
    private List<Integer> list = new ArrayList<>();
    private AudioDispatcher dispatcher;
    private PitchDetectionHandler pdh;
    private AudioProcessor p;
    private Thread main;
    private List<Integer> buffer = new ArrayList<>();
    private Boolean isMonitoring = false;
    private HashMap <Integer,Integer> midiMap = new HashMap<>();
    private List<Integer> flatlist = new ArrayList<>();
    private TextView text;
    //control value
    private int noSoundCount = -1;
    private int lastMostFreqMidi = -1;
    public MainActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        piano = (Piano) view.findViewById(R.id.piano_keyboard);
        pianoSheet = (PianoSheet) view.findViewById(R.id.music_sheet);
        clearNotes = (Button)view.findViewById(R.id.clear_notes);
        playButton = (Button)view.findViewById(R.id.play);
        recording = (ToggleButton)view.findViewById(R.id.recording);
        pianoSheetLayout = (HorizontalScrollView)view.findViewById(R.id.music_sheet_layout);
        soundForPlayButton = new SoundPoolPlayer(getActivity());
        soundForKeyboard = new SoundPoolPlayer(getActivity());
        text = (TextView) view.findViewById(R.id.midiKey);
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 512);

        // voice to midi value map
        midiMap.put(60,0);
        midiMap.put(62,1);
        midiMap.put(64,2);
        midiMap.put(65,3);
        midiMap.put(67,4);
        midiMap.put(69,5);
        midiMap.put(71,6);
        midiMap.put(72,7);

        midiMap.put(74,1);
        midiMap.put(76,2);
        midiMap.put(77,3);
        midiMap.put(79,4);
        midiMap.put(81,5);
        midiMap.put(83,6);
        midiMap.put(84,7);



/*
        flatlist.add(61);
        flatlist.add(63);
        flatlist.add(68);
        flatlist.add(70);
        flatlist.add(73);
        flatlist.add(75);
        flatlist.add(78);
        flatlist.add(80);
        flatlist.add(82);*/

        pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result,AudioEvent e) {
                final float pitchInHz = result.getPitch();
                final int midi = PitchConverter.hertzToMidiKey(new Float(pitchInHz).doubleValue()) ;
                //int midiForProcess = midi % 12;
                if (isMonitoring) {
                    //Log.i("MIDI", String.valueOf(midi));
                    //add midi to buffer
                    if(buffer.size()<150) buffer.add(midi);
                    else{
                        buffer = buffer.subList(buffer.size()-75, buffer.size());
                        buffer.add(midi);
                    }
                    if (midi != 0) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                text.setTextColor(Color.BLACK);
                                text.setTypeface(null, Typeface.NORMAL);

                                if (flatlist.contains(midi)) {
                                    text.setTextColor(Color.RED);
                                    text.setText(String.valueOf("MIDI: " + midi));
                                    text.setTypeface(null, Typeface.BOLD);
                                } else {
                                    text.setText(String.valueOf("MIDI: "+midi));
                                }
                            }
                        });
                        //Takes 5 samples from buffer
                        Log.i("arr", Arrays.toString(buffer.toArray())+"");
                        if (buffer.size() >= 5) {
                            List<Integer> subList = buffer.subList(buffer.size()-5, buffer.size());
                            //Log.i("sub_arr", Arrays.toString(subList.toArray())+"");
                            //find common frequency
                            final int mostFreqMidi = mostCommon(subList);
                            //calculate the total number of the common frequency
                            int totalNumOfMostFreqMidi =0;
                            for(int i=0;i<subList.size();i++){
                                if(subList.get(i).intValue()==mostFreqMidi) totalNumOfMostFreqMidi++;
                            }

                            //require at least the number of the common frequency
                            //Log.i("MIDI---data", String.valueOf(totalNumOfMostFreqMidi+" "+noSoundCount));
                            if(totalNumOfMostFreqMidi<4) {
                                //Log.i("MIDI---totalNumOfMostFreqMidi", String.valueOf(totalNumOfMostFreqMidi));
                                noSoundCount=0;
                                return;
                            }
                            //require no sound delay when repeat the same key
                            if(noSoundCount<5 && mostFreqMidi==lastMostFreqMidi) {
                                //Log.i("MIDI---no sound delay", String.valueOf(noSoundCount+" "+mostFreqMidi+" "+lastMostFreqMidi));
                                noSoundCount=0;
                                return;
                            }
                            noSoundCount=0;

                            Log.i("MIDI---COMMON", String.valueOf(mostFreqMidi));
                            if(pianoSheet.getNoteList().size() <= pianoSheet.getCurrentPositionForRecording()) {
                                isMonitoring = false;
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        recording.setChecked(false);
                                    }}
                                );
                            }
                            if (midiMap.containsKey(mostFreqMidi)) {
                                lastMostFreqMidi = mostFreqMidi;

                                if(pianoSheet.getNoteList().get(pianoSheet.getCurrentPositionForRecording()) == midiMap.get(mostFreqMidi))
                                    pianoSheet.setCurrentPositionForRecording(pianoSheet.getCurrentPositionForRecording()+1);
                                else{
                                    List<Integer> correctnessList = pianoSheet.getCorrectnessList();
                                    correctnessList.add(pianoSheet.getCurrentPositionForRecording());
                                    pianoSheet.setCorrectnessList(correctnessList);
                                    pianoSheet.setCurrentPositionForRecording(pianoSheet.getCurrentPositionForRecording()+1);
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        pianoSheet.invalidate();
                                    }
                                });

                                if ((pianoSheet.getCurrentPositionForRecording() - 1) % 6 == 0 && pianoSheet.getCurrentPositionForRecording() != 1)
                                    pianoSheetLayout.scrollBy(150 * 6, 0);
                            }
                        }
                    }
                    else {
                        if (noSoundCount < 100) noSoundCount++;
                        if (noSoundCount > 15) lastMostFreqMidi =-1;
                    }
                }
            }
        };
        p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.MPM, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(p);

        main = new Thread(dispatcher,"main");
        //main.start();


        //listener
        piano.setPianoKeyListener(onPianoKeyPress);
        soundForPlayButton.setVariableChangeListener(onSoundChange);
        playButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMonitoring = false;
                recording.setChecked(false);
                if(!soundForPlayButton.isPlaying()) {
                    pianoSheet.setCurrentPositionForRecording(-1);
                    pianoSheetLayout.scrollTo(0, 0);
                    String notes = "";
                    String[] doremi = {"C","D","E","F","G","A","B","C'"};
                    String writeTo = "";
                    Iterator<Integer> it = pianoSheet.getNoteList().iterator();
                    while (it.hasNext()) {
                        int musicIndexTransform = it.next() + 1;
                        notes += musicIndexTransform + " ";
                        writeTo += doremi[musicIndexTransform - 1] + " ";
                    }
                    if (!notes.equals("")) {
                        soundForPlayButton.playPiano(notes);
                        SaveTxt save = new SaveTxt();
                        save.save(writeTo);
                    }

                }
            }
        });
        clearNotes.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> correctnessList = pianoSheet.getCorrectnessList();
                correctnessList.clear();
                pianoSheet.setCorrectnessList(correctnessList);
                pianoSheet.setCurrentPosition(-1);
                soundForPlayButton.timerCancel();
                List<Integer> noteList = new ArrayList<>();
                pianoSheet.setNoteList(noteList);
                pianoSheet.invalidate();
                pianoSheetLayout.scrollTo(0,0);
                list.clear();
            }
        });
        recording.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

               if(pianoSheet.getNoteList().isEmpty()) {
                   buttonView.setChecked(false);
                   return;
               }

                Log.i("recording button", "isChecked" + isChecked);
                if (isChecked) {
                    List<Integer> correctnessList = pianoSheet.getCorrectnessList();
                    correctnessList.clear();
                    pianoSheet.setCorrectnessList(correctnessList);
                    pianoSheet.setCurrentPositionForRecording(0);
                    pianoSheetLayout.scrollTo(0,0);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pianoSheet.invalidate();
                        }
                    });

                    isMonitoring = true;
                    noSoundCount = -1;
                    lastMostFreqMidi = -1;

                    if (!main.getState().equals(Thread.State.RUNNABLE)) {
                        main.start();
                    }
                } else {
                    isMonitoring = false;
                    //Save key to text file for future use
                    SaveTxt savetxt = new SaveTxt();
                    savetxt.save(Arrays.toString(pianoSheet.getNoteList().toArray()));
                }
            }
        });


        return view;
    }


    private SoundPoolPlayer.VariableChangeListener onSoundChange=
            new SoundPoolPlayer.VariableChangeListener() {
                @Override
                public void onVariableChanged(final boolean isPlaying, final int count){
                    final Map<Integer,Integer> keyMap = new HashMap<>();
                    keyMap.put(0,0);
                    keyMap.put(1,2);
                    keyMap.put(2,4);
                    keyMap.put(3,5);
                    keyMap.put(4,7);
                    keyMap.put(5, 9);
                    keyMap.put(6, 11);
                    keyMap.put(7, 12);
                    pianoSheet.setCurrentPosition(count - 1);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pianoSheet.invalidate();
                            if ((count - 1) % 6 == 0 && count != 1)
                                pianoSheetLayout.scrollBy(150 * 6, 0);
                            if(isPlaying) {
                                final int keyMapValue = keyMap.get(pianoSheet.getNoteList().get(count - 1));
                                piano.pushKeyDownManually(keyMapValue);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        piano.pushKeyUpManually(keyMapValue);
                                    }
                                }, 390);
                            }
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
                            //soundForKeyboard.playPiano(String.valueOf(musicMap.get(id)));
                            soundForKeyboard.playShortResource(musicMap.get(id));
                            List<Integer> noteList = pianoSheet.getNoteList();
                            noteList.add(keyMap.get(id));
                            pianoSheet.setNoteList(noteList);
                            pianoSheet.invalidate();
                            if((noteList.size()-1)%6==0 && noteList.size()!=1)pianoSheetLayout.scrollBy(150*6, 0);
                        }
                    }
                }
            };

    public static <T> T mostCommon(List<T> list) {
        Map<T, Integer> map = new HashMap<>();

        for (T t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Map.Entry<T, Integer> max = null;

        for (Map.Entry<T, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        return max.getKey();
    }
}
