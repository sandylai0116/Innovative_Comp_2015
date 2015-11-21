package com.project.piano;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by lam on 21/11/15.
 */
public class SaveTxt {
    public void save(String text){
        File download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(download, "MC.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(text);
            writer.close();
        }catch (Exception e){

        }
    }
}
