package it.sapienza.simplenotes.Runnables;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import it.sapienza.simplenotes.GlobalClass;
import it.sapienza.simplenotes.R;
import it.sapienza.simplenotes.model.Note;
import it.sapienza.simplenotes.model.NoteAnswer;

public class NoteDeleteRunnable implements Runnable{
    private static final String TAG = "NoteDeleteRunnable";
    private final String URL ="http://10.0.2.2:3000/notes/";
    private long id;
    private GlobalClass global;
    private Context context;

    public NoteDeleteRunnable(Context context, GlobalClass global,long id){
        this.context = context;
        this.global = global;
        this.id = id;
    }

    @Override
    public void run() {
        deleteFromDisk();
        deleteFromCloud();
    }
    private void deleteFromDisk(){
        boolean del = global.delete(id);
        Log.d(TAG, "deleteFromDisk: del="+del+" id:"+id);
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

    private void deleteFromCloud(){
        Log.d(TAG, "delete note id:"+id);
        Gson gson = new Gson();

        URL url;
        try {
            url = new URL(URL+id);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("DELETE");
            httpCon.setRequestProperty("Content-Type", "application/json");
            httpCon.setRequestProperty("Accept", "application/json");

            InputStream responce = httpCon.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(responce));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            Log.d(TAG, "delete responce: "+output.toString());
            NoteAnswer answer = gson.fromJson(output.toString(),NoteAnswer.class);
            if(answer.getResult().equals("SUCCESS")){

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
