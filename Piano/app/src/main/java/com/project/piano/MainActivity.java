package com.project.piano;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    Thread main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        AudioDispatcher dispatcher =
//                AudioDispatcherFactory.fromDefaultMicrophone(11025, 2048, 0);
//
//        PitchDetectionHandler pdh = new PitchDetectionHandler() {
//            @Override
//            public void handlePitch(PitchDetectionResult result,AudioEvent e) {
//                final float pitchInHz = result.getPitch();
//                final int midi = PitchConverter.hertzToMidiKey(new Float(pitchInHz).doubleValue());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        TextView text = (TextView) findViewById(R.id.midiKey);
//                        text.setText((String.valueOf(midi)));
//                    }
//                    /* MIDI KEY TO MUSICAL NOTE
//                     * http://www.electronics.dit.ie/staff/tscarff/Music_technology/midi/midi_note_numbers_for_octaves.htm
//                     */
//                });
//            }
//        };
//        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.MPM, 11025, 2048, pdh);
//        dispatcher.addAudioProcessor(p);
//        main = new Thread(dispatcher,"Audio Dispatcher");
//        main.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        getDelegate().onStop();
//        //main.interrupt();
//    }


}
