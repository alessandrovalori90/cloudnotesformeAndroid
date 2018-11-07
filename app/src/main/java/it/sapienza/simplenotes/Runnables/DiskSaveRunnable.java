package it.sapienza.simplenotes.Runnables;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import it.sapienza.simplenotes.R;
import it.sapienza.simplenotes.model.Note;

public class DiskSaveRunnable implements Runnable{
    private static final String TAG = "DiskSaveRunnable";
    private Context context;
    private Note[] notes;

    public DiskSaveRunnable(Context context, Note[] notes) {
        this.context = context;
        this.notes = notes;
    }

        @Override
    public void run() {
        if(notes == null) return;
        Gson gson = new Gson();
        String fileContents = gson.toJson(notes);
        File file = new File(context.getFilesDir(), context.getResources().getString(R.string.storage_file_name));
        Log.d(TAG, "path: "+file.getPath());
        Log.d(TAG, "input: "+fileContents);
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(context.getResources().getString(R.string.storage_file_name), Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
