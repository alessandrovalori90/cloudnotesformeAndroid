package it.sapienza.simplenotes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import java.util.Date;

import it.sapienza.simplenotes.GlobalClass;
import it.sapienza.simplenotes.R;
import it.sapienza.simplenotes.model.Note;

public class NoteActivity extends AppCompatActivity {
    private static final String TAG = "NoteActivity";
    private EditText title;
    private EditText text;
    private long idExtra;
    private GlobalClass global;
    private AccessToken accessToken;
    private boolean deleted = false;

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
        //obtain information about the note
        String titleExtra = getIntent().getStringExtra("title");
        String textExtra = getIntent().getStringExtra("text");
        long timeid = (int) new Date().getTime();
        timeid = -Math.abs(timeid); //make negative
        idExtra =getIntent().getLongExtra("id",timeid);
        //retrieve UI items adn update
        title = findViewById(R.id.title);
        text = findViewById(R.id.text);
        title.setText(titleExtra);
        text.setText(textExtra);

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
                finish();
                return true;
            case R.id.delete_note:
                if(idExtra<0) onBackPressed();
                deleted = true;
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
        if(deleted) {
            UpdateRunnable delete = new UpdateRunnable("DELETE",null);
            Thread t1 = new Thread(delete);
            t1.start();
            return;
        }
        Note note = new Note(title.getText().toString(),text.getText().toString(),new Date(),idExtra,accessToken.getUserId());
        UpdateRunnable update = new UpdateRunnable("UPDATE",note);
        Thread t1 = new Thread(update);
        t1.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stoppped!");
    }

    public class UpdateRunnable implements Runnable{
        private String command;
        private Note note;

        UpdateRunnable(String c, Note n){
            this.command = c;
            this.note = n;
        }
        @Override
        public void run() {

            if(command.equals("DELETE")){
                global.lock();
                global.deleteLater(idExtra);
                global.unlock();
            } else if(command.equals("UPDATE")){
                global.lock();
                global.update(note);
                global.unlock();
            }

        }
    }
}
