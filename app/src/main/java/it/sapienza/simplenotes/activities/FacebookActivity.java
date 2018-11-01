package it.sapienza.simplenotes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import it.sapienza.simplenotes.R;
import it.sapienza.simplenotes.Runnables.UserSaveRunnable;

public class FacebookActivity extends AppCompatActivity {
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private static final String TAG = "FacebookActivity";
    private AccessToken accessToken;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        //loginButton.setReadPermissions("email");
        executor = Executors.newSingleThreadExecutor();
        //check if already logged in
        accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn){
            Intent newIntent = new Intent(FacebookActivity.this, MainActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //cancels this activity and launches new one
            startActivity(newIntent);
        }

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = AccessToken.getCurrentAccessToken();
                UserSaveRunnable saveuser = new UserSaveRunnable(accessToken.getUserId());
                executor.execute(saveuser);
                Intent newIntent = new Intent(FacebookActivity.this, MainActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //cancels this activity and launches new one
                startActivity(newIntent);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
