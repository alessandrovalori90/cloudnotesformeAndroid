package it.sapienza.simplenotes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import it.sapienza.simplenotes.GlobalClass;
import it.sapienza.simplenotes.R;
import it.sapienza.simplenotes.Runnables.NoteDeleteRunnable;
import it.sapienza.simplenotes.Runnables.NoteSaveRunnable;
import it.sapienza.simplenotes.model.Note;

public class NoteActivity extends AppCompatActivity {
    private static final String TAG = "NoteActivity";
    private EditText title;
    private EditText text;
    private String titleExtra;
    private String textExtra;
    private int idExtra;
    private GlobalClass global;
    private ExecutorService executor;
    private AccessToken accessToken;
    private final int def = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        global = (GlobalClass) getApplicationContext();
        //set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Note");
        setSupportActionBar(toolbar);
        //show back-button
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        accessToken = AccessToken.getCurrentAccessToken();
        executor = Executors.newSingleThreadExecutor();
        titleExtra = getIntent().getStringExtra("title");
        textExtra = getIntent().getStringExtra("text");
        idExtra =getIntent().getIntExtra("id",-1);

        //retrieve UI items
        title = findViewById(R.id.title);
        text = findViewById(R.id.text);
        //

        if(idExtra!=def) {
            title.setText(titleExtra);
            text.setText(textExtra);
        }

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: text="+text.getText().toString());
                Note note = new Note(title.getText().toString(),text.getText().toString(),new Date(),idExtra,accessToken.getUserId());
                NoteSaveRunnable save = new NoteSaveRunnable(note);
                Future<Integer> future = executor.submit(save);
                try {
                    idExtra = future.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        text.addTextChangedListener(watcher);
        title.addTextChangedListener(watcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.log_out:
                LoginManager.getInstance().logOut();
                Intent newIntent = new Intent(NoteActivity.this, FacebookActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //cancels this activity and launches new one
                startActivity(newIntent);
                return true;
            case R.id.delete_note:
                if(idExtra==-1) onBackPressed();
                Runnable delete = new NoteDeleteRunnable(idExtra);
                executor.submit(delete);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp: "+keyCode);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void finish() {
        super.finish();
        Log.d(TAG, "finished!");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stoppped!");
    }
}
