package it.sapienza.simplenotes.activities;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import it.sapienza.simplenotes.GlobalClass;
import it.sapienza.simplenotes.R;
import it.sapienza.simplenotes.RecyclerViewAdapter;
import it.sapienza.simplenotes.Utility.HttpConn;
import it.sapienza.simplenotes.Utility.InternalStorage;
import it.sapienza.simplenotes.model.NotesAnswer;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recycler;
    private GlobalClass global;
    private Executor executor;
    private AccessToken facebooktoken;
    private NotesAnswer answer;
    private boolean internet; //true if internet is available
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        global = (GlobalClass) getApplicationContext();
        //set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Simple NotesAnswer");
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

        recycler = findViewById(R.id.recycler);
        //Dowload notes from server
        executor = Executors.newSingleThreadExecutor();
        facebooktoken = AccessToken.getCurrentAccessToken();
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
        RetrieveNotesRunnable runnable = new RetrieveNotesRunnable(recycler, this, global,facebooktoken.getUserId());
        stopTimer(); //per evitare di avere il timer e l'executor che lanciano lo stesso runnable contemporaneamente dato che è inutile
        executor.execute(runnable);
    }

    private void setTimer(final Context context){
        Log.d(TAG, "timer set");
        if(timer==null) {
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    RetrieveNotesRunnable tmp = new RetrieveNotesRunnable(recycler,context,global,facebooktoken.getUserId());
                    executor.execute(tmp);
                }
            };
            timer.scheduleAtFixedRate(task,0,10000);
        }
    }

    private void stopTimer(){
        Log.d(TAG, "timer stopped");
        if(timer!=null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    //-------------------------
    public class RetrieveNotesRunnable implements Runnable {
        private RecyclerView recycler;
        private Context context;
        private GlobalClass global;
        private String user_id;

        private static final String TAG = "RetrieveNotesRunnable";
        private final String URL ="http://10.0.2.2:3000/"; //device is running on a VM so localhost is not recognized

        public RetrieveNotesRunnable(RecyclerView recycler, Context context, GlobalClass global, String user_id){
            this.recycler = recycler;
            this.context = context;
            this.global = global;
            this.user_id = user_id;
        }

        @Override
        public void run() {
            Log.d(TAG, "run: dowload thread");
            if(answer ==null) readDisk();
            recycler.post(new Runnable() {
                @Override
                public void run() {
                    RecyclerViewAdapter recycleAdapter = new RecyclerViewAdapter(global.getList());
                    recycler.setAdapter(recycleAdapter);
                    recycler.setLayoutManager(new LinearLayoutManager(context));
                }
            });
            Log.d(TAG, "run updated interface: ");
            checkConnection();
            if(internet) cloud();
            InternalStorage.writeInternalStorage(global,context);

        }
        //checks open networks. These networks do not necessarely have internet connection.
        public void checkConnection(){
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            internet = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            Log.d(TAG, "checkConnection: "+internet);
        }
        //dowloads notes from cloud
        private boolean cloud(){
            Gson gson = new Gson();
            String tmp = gson.toJson(global.getList());
            String json;
            if(tmp==null || tmp.equals("null")) json = "{\"list\":[]}";
            else json = "{\"list\":"+tmp+"}";
            Log.d(TAG, "cloud: json string="+json);


            try {
                answer = HttpConn.syncList(context, global,URL+"users/"+user_id,"PUT",json);
            } catch (IOException e) {
                e.printStackTrace();
                internet = false;
                setTimer(context);
                return false;
            }
            //update interface
            if(answer == null) {
                internet = false;
                setTimer(context);
                return false;
            }
            global.setList(answer.getNotes());
            recycler.post(new Runnable() {
                @Override
                public void run() {
                    RecyclerViewAdapter recycleAdapter = new RecyclerViewAdapter(answer.getNotes());
                    recycler.setAdapter(recycleAdapter);
                    recycler.setLayoutManager(new LinearLayoutManager(context));
                }
            });
            internet = true;
            stopTimer();
            return true;
        }


        private void readDisk(){
            answer = InternalStorage.readInternalStorage(context);
            if(answer ==null) return;
            global.setList(answer.getNotes());
            recycler.post(new Runnable() {
                @Override
                public void run() {
                    RecyclerViewAdapter recycleAdapter = new RecyclerViewAdapter(answer.getNotes());
                    recycler.setAdapter(recycleAdapter);
                    recycler.setLayoutManager(new LinearLayoutManager(context));
                }
            });

        }
    }


}
