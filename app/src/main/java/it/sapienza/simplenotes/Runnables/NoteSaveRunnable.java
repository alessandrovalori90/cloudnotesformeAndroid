package it.sapienza.simplenotes.Runnables;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import it.sapienza.simplenotes.model.Note;
import it.sapienza.simplenotes.model.ServerAnswer;

public class NoteSaveRunnable implements Callable<Integer> {
    private static final String TAG = "NoteSaveRunnable";
    private Note note;
    private final String URL = "http://10.0.2.2:3000/notes";
    private final int def = -1;

    public NoteSaveRunnable(Note note) {
        this.note = note;
    }

    private int create(){
        Log.d(TAG, "create note!");
        Gson gson = new Gson();
        String json = gson.toJson(note);

        URL url;
        try {
            url = new URL(URL);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");
            httpCon.setRequestProperty("Content-Type", "application/json");
            httpCon.setRequestProperty("Accept", "application/json");
            OutputStreamWriter out = new OutputStreamWriter(
                    httpCon.getOutputStream());
            out.write(json);
            out.flush();
            out.close();

            InputStream responce = httpCon.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(responce));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            Log.d(TAG, "create responce: "+output.toString());
            ServerAnswer answer = gson.fromJson(output.toString(),ServerAnswer.class);
            if(answer.getResult().equals("SUCCESS")){
                return answer.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    private int update(){
        Log.d(TAG, "update note!");
        Gson gson = new Gson();
        String json = gson.toJson(note);

        URL url;
        try {
            url = new URL(URL+"/"+note.getId());
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");
            httpCon.setRequestProperty("Content-Type", "application/json");
            httpCon.setRequestProperty("Accept", "application/json");
            OutputStreamWriter out = new OutputStreamWriter(
                    httpCon.getOutputStream());
            out.write(json);
            out.flush();
            out.close();

            InputStream responce = httpCon.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(responce));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            Log.d(TAG, "update responce: "+output.toString());
            ServerAnswer answer = gson.fromJson(output.toString(),ServerAnswer.class);
            if(answer.getResult().equals("SUCCESS")){
                return answer.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    @Override
    public Integer call() throws Exception {
        if(note.getId()==-1) return create();
        else return update();
    }
}
