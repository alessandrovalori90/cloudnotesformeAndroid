package it.sapienza.simplenotes.Runnables;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import it.sapienza.simplenotes.model.ServerAnswer;

public class UserSaveRunnable implements Runnable {
    private String user_id;

    private static final String TAG = "UserSaveRunnable";
    private final String URL="http://10.0.2.2:3000/users";
    private ServerAnswer answer;

    public UserSaveRunnable(String user_id){
        this.user_id = user_id;
    }

    @Override
    public void run() {
        String json = "{\"id\":\""+user_id+"\"}";

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
