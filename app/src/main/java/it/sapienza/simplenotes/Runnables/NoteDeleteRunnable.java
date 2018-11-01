package it.sapienza.simplenotes.Runnables;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import it.sapienza.simplenotes.model.ServerAnswer;

public class NoteDeleteRunnable implements Runnable{
    private static final String TAG = "NoteDeleteRunnable";
    private final String URL ="http://10.0.2.2:3000/notes/";
    private int id;

    public NoteDeleteRunnable(int id){
        this.id = id;
    }

    @Override
    public void run() {
        Log.d(TAG, "create note!");
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
            ServerAnswer answer = gson.fromJson(output.toString(),ServerAnswer.class);
            if(answer.getResult().equals("SUCCESS")){

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
