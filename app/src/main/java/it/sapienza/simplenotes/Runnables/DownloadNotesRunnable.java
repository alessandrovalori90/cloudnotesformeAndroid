package it.sapienza.simplenotes.Runnables;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import it.sapienza.simplenotes.GlobalClass;
import it.sapienza.simplenotes.RecyclerViewAdapter;
import it.sapienza.simplenotes.model.Notes;

public class DownloadNotesRunnable implements Runnable {
    private RecyclerView recycler;
    private Context context;
    private GlobalClass global;
    private String user_id;

    private static final String TAG = "DownloadNotesRunnable";
    private final String URL ="http://10.0.2.2:3000/"; //device is running on a VM so localhost is not recognized
    private Notes notes;

    public DownloadNotesRunnable(RecyclerView recycler, Context context, GlobalClass global, String user_id){
        this.recycler = recycler;
        this.context = context;
        this.global = global;
        this.user_id = user_id;
    }

    @Override
    public void run() {
        URL url = null;
        HttpURLConnection urlConnection = null;

        try {
            //create connection
            url = new URL(URL+"/notes/"+user_id);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            //read server responce
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) { out.append(line); }
            //create object from server responce
            Gson gson = new Gson();
            notes = gson.fromJson(out.toString(),Notes.class);
            Log.d(TAG, "run: " +out.toString());
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        //update interface
        if(notes == null) return;
        recycler.post(new Runnable() {
            @Override
            public void run() {
                RecyclerViewAdapter recycleAdapter = new RecyclerViewAdapter(notes.getNotes());
                recycler.setAdapter(recycleAdapter);
                recycler.setLayoutManager(new LinearLayoutManager(context));
            }
        });
    }
}
