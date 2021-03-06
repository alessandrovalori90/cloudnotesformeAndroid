package it.sapienza.simplenotes.utility;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import it.sapienza.simplenotes.R;
import it.sapienza.simplenotes.model.GsonUTCDateAdapter;
import it.sapienza.simplenotes.model.Note;
import it.sapienza.simplenotes.model.NotesAnswer;


public class HttpConn {
    private static final String TAG = "HttpConn";

    public static NotesAnswer syncList(Context context, String URL, String METHOD, String json) throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonUTCDateAdapter()).create();
        //Gson gson = new Gson();
        URL url;
        NotesAnswer answer;
        Log.d(TAG, "syncList body: "+json);
        //create connection
        url = new URL(URL);
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setConnectTimeout(context.getResources().getInteger(R.integer.timeout));
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod(METHOD);
        httpCon.setRequestProperty("Content-Type", "application/json");
        httpCon.setRequestProperty("Accept", "application/json");
        OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
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
        //create object from server responce
        Log.d(TAG, "syncList answer: " +output.toString());

        answer = gson.fromJson(output.toString(),NotesAnswer.class);
        Note[] tmp = answer.getNotes();

        reader.close();
        return answer;
    }


}
