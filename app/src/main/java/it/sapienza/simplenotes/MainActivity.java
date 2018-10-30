package it.sapienza.simplenotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private WorkManager mWorkManager;
    private RecyclerView recycler;
    private GlobalClass global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Simple Notes");
        //show menu
        setSupportActionBar(toolbar);

        FloatingActionButton newNote = findViewById(R.id.newNote);
        newNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(MainActivity.this, NoteActivity.class);
                newIntent.putExtra("new",true); //true for new notes false for old notes
                startActivity(newIntent);
            }
        });
        global = (GlobalClass) getApplicationContext();
        recycler = findViewById(R.id.recycler);
        DownloadNotesRunnable runnable = new DownloadNotesRunnable(recycler, this, global);
        new Thread(runnable).start();

        //mWorkManager.enqueue(OneTimeWorkRequest.from(CloudSaveRunnable.class));
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
            Intent newIntent = new Intent(MainActivity.this, Facebook.class);
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

        RecyclerViewAdapter recycleAdapter = new RecyclerViewAdapter(global.getList());
        recycler.setAdapter(recycleAdapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
    }
}
