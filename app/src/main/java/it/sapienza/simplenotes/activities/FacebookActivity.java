package it.sapienza.simplenotes.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.sapienza.simplenotes.GlobalClass;
import it.sapienza.simplenotes.R;
import it.sapienza.simplenotes.model.GenericAnswer;
import it.sapienza.simplenotes.model.Settings;
import it.sapienza.simplenotes.utility.InternalStorage;

public class FacebookActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private ExecutorService executor;
    private Context context;
    private GlobalClass global;
    private static final String TAG = "FacebookActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        context = this;
        global = (GlobalClass) getApplicationContext();
        //loginButton.setReadPermissions("email");
        executor = Executors.newSingleThreadExecutor();
        //check if already logged in
        accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn){
            Intent newIntent = new Intent(FacebookActivity.this, MainActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //cancels this activity and launches new one
            startActivity(newIntent);
            finish();
        }

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: ");
                accessToken = AccessToken.getCurrentAccessToken();
                UserSaveRunnable saveUser = new UserSaveRunnable(accessToken.getUserId());
                executor.submit(saveUser);


            }

            @Override
            public void onCancel() {
                // App code
                Log.d(TAG, "onCancel: ");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d(TAG, "onError: ");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
    * Register the user on the server. If is already reagistered the server ignores the request.
    * Saves the state of registration in the internal storage. This allows to avoid check registration next time.
    */
    public class UserSaveRunnable implements Runnable {
        private String user_id;
        private static final String TAG = "UserSaveRunnable";
        private Settings settings = null;

        private UserSaveRunnable(String user_id){
            this.user_id = user_id;
        }

        @Override
        public void run() {
            settings = InternalStorage.readSettingsInternalStorage(context);
            if(settings!=null && settings.isRegistered())
                global.setSettings(settings);
            else
                registerUser();

            //if the internal storage knows the user is already registered or the server answered with a positive answer continue else log-out of facebook to retry
            if(global.getSettings() != null && global.getSettings().isRegistered()){
                Intent newIntent = new Intent(FacebookActivity.this, MainActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //cancels this activity and launches new one
                startActivity(newIntent);
                finish();
            } else
                LoginManager.getInstance().logOut();
        }

        private void registerUser(){
            GenericAnswer answer = null;
            final String URL="http://10.0.2.2:3000/users";
            String json = "{\"id\":\""+user_id+"\"}";

            URL url;
            try {
                url = new URL(URL);
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setDoOutput(true);
                httpCon.setConnectTimeout(context.getResources().getInteger(R.integer.timeout));
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
                Gson gson = new Gson();
                answer = gson.fromJson(output.toString(),GenericAnswer.class);
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(answer != null && (answer.getResult().equals("SUCCESS")|| answer.getResult().equals("WARNING"))){
                settings = new Settings();
                settings.setRegistered(true);
                global.setSettings(settings);
                InternalStorage.writeSettingsInternalStorage(global, context);
            }
        }
    }
}
