package it.sapienza.simplenotes.Utility;

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

import it.sapienza.simplenotes.GlobalClass;
import it.sapienza.simplenotes.R;
import it.sapienza.simplenotes.model.Note;
import it.sapienza.simplenotes.model.NotesAnswer;

public class InternalStorage {
    private static final String TAG = "InternalStorage";

    public static NotesAnswer readInternalStorage(Context context){
        FileInputStream fis = null;
        String line="";
        StringBuilder sb = new StringBuilder();
        NotesAnswer answer = null;
        try {
            fis = context.openFileInput(context.getResources().getString(R.string.storage_file_name));
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(sb==null || sb.toString().equals("")) return answer;
        Log.d(TAG, "readDisk result: "+sb.toString());
        Gson gson = new Gson();
        Note temporary[] = gson.fromJson(sb.toString(),Note[].class);
        if(temporary==null) return answer;
        answer = new NotesAnswer("",temporary);
        return answer;
    }

    public static void writeInternalStorage(GlobalClass global, Context context){
        if(global.getList() == null) return;
        Gson gson = new Gson();
        String fileContents = gson.toJson(global.getList() );
        File file = new File(context.getFilesDir(), context.getResources().getString(R.string.storage_file_name));
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
