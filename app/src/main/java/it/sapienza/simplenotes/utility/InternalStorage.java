package it.sapienza.simplenotes.utility;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import it.sapienza.simplenotes.GlobalClass;
import it.sapienza.simplenotes.R;
import it.sapienza.simplenotes.model.Note;
import it.sapienza.simplenotes.model.NotesAnswer;
import it.sapienza.simplenotes.model.Settings;

public class InternalStorage {
    private static final String TAG = "InternalStorage";

    public static NotesAnswer readNotesInternalStorage(Context context){
        FileInputStream fis;
        String line;
        StringBuilder sb = new StringBuilder();
        NotesAnswer answer;
        try {
            fis = context.openFileInput(context.getResources().getString(R.string.storage_notes_file_name));
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(sb.toString().equals("")) return null;
        Log.d(TAG, "readDisk result: "+sb.toString());
        Gson gson = new Gson();
        Note temporary[] = gson.fromJson(sb.toString(),Note[].class);
        if(temporary==null) return null;
        answer = new NotesAnswer("",temporary);
        return answer;
    }

    public static void writeNotesInternalStorage(GlobalClass global, Context context){
        if(global.getList() == null) return;
        Gson gson = new Gson();
        String fileContents = gson.toJson(global.getList() );
        new File(context.getFilesDir(), context.getResources().getString(R.string.storage_notes_file_name));
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(context.getResources().getString(R.string.storage_notes_file_name), Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Settings readSettingsInternalStorage(Context context){
        FileInputStream fis;
        String line;
        StringBuilder sb = new StringBuilder();
        Settings settings;
        try {
            fis = context.openFileInput(context.getResources().getString(R.string.storage_settings_file_name));
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(sb.toString().equals("")) return null;
        Log.d(TAG, "readDisk result: "+sb.toString());
        Gson gson = new Gson();
        settings = gson.fromJson(sb.toString(),Settings.class);
        return settings;
    }

    public static void writeSettingsInternalStorage(GlobalClass global, Context context){
        Gson gson = new Gson();
        String fileContents = gson.toJson(global.getSettings() );
        new File(context.getFilesDir(), context.getResources().getString(R.string.storage_settings_file_name));
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(context.getResources().getString(R.string.storage_settings_file_name), Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
