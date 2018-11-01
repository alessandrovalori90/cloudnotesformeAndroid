package it.sapienza.simplenotes.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import it.sapienza.simplenotes.GlobalClass;
import it.sapienza.simplenotes.R;
import it.sapienza.simplenotes.Runnables.DownloadNotesRunnable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recycler;
    private GlobalClass global;
    private Executor executor;
    private AccessToken facebooktoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        //set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Simple Notes");
        setSupportActionBar(toolbar);
        //set Floating button
        FloatingActionButton newNote = findViewById(R.id.newNote);
        newNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(MainActivity.this, NoteActivity.class);
                startActivity(newIntent);
            }
        });

        global = (GlobalClass) getApplicationContext();
        recycler = findViewById(R.id.recycler);
        //Dowload notes from server
        executor = Executors.newSingleThreadExecutor();
        facebooktoken = AccessToken.getCurrentAccessToken();
        DownloadNotesRunnable runnable = new DownloadNotesRunnable(recycler, this, global,facebooktoken.getUserId());
        executor.execute(runnable);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.log_out) {
            LoginManager.getInstance().logOut();
            Intent newIntent = new Intent(MainActivity.this, FacebookActivity.class);
            startActivity(newIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        Log.d(TAG, "finish: finished!");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: stoppped!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Resumed!");
        DownloadNotesRunnable runnable = new DownloadNotesRunnable(recycler, this, global,facebooktoken.getUserId());
        executor.execute(runnable);
    }
}
