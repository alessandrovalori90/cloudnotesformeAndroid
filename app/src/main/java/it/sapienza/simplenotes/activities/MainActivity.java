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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import it.sapienza.simplenotes.GlobalClass;
import it.sapienza.simplenotes.R;
import it.sapienza.simplenotes.RecyclerViewAdapter;
import it.sapienza.simplenotes.model.Note;
import it.sapienza.simplenotes.model.NotesAnswer;
import it.sapienza.simplenotes.utility.HttpConn;
import it.sapienza.simplenotes.utility.InternalStorage;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recycler;
    private GlobalClass global;
    private ExecutorService executor;
    private AccessToken facebooktoken;
    private NotesAnswer answer;
    private boolean internet; //true if internet is available
    private Timer timer;
    private Future future;
    private boolean timerStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        global = (GlobalClass) getApplicationContext();
        //set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Simple Notes");
        setSupportActionBar(toolbar);
        //set Floating button
        FloatingActionButton newNote = findViewById(R.id.newNote);
        newNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //quando la nota è finita si ritorna a questa activity ma se ci sono ancora tentativi di riconessione rallentano
                // l'aggiornamento dell'interfaccia dato che la coda è di un singolo thread
                stopTimer();
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
        } else if(id == R.id.Refresh) {
            RetrieveNotesRunnable runnable = new RetrieveNotesRunnable(recycler, this, global,facebooktoken.getUserId());
            future = executor.submit(runnable);
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
        Gson gson = new Gson();
        RetrieveNotesRunnable runnable = new RetrieveNotesRunnable(recycler, this, global,facebooktoken.getUserId());
        future = executor.submit(runnable);
    }

    private void setTimer(final Context context){
        if(timerStarted) return;
        Log.d(TAG, "timer set");
        if(timer==null) {
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if(future != null ){
                        future.cancel(true);
                        global.unlock();
                    }
                    RetrieveNotesRunnable tmp = new RetrieveNotesRunnable(recycler,context,global,facebooktoken.getUserId());
                    future = executor.submit(tmp);
                }
            };
            timer.scheduleAtFixedRate(task,0,10000);
            timerStarted = true;
        }
    }

    private void stopTimer(){
        if(timerStarted==false) return;
        Log.d(TAG, "timer stopped");
        if(timer!=null){
            timer.cancel();
            timer.purge();
            timer = null;
            timerStarted = false;
        }
    }

    //-------------------------
    public class RetrieveNotesRunnable implements Runnable {
        private RecyclerView recycler;
        private Context context;
        private GlobalClass global;
        private String user_id;

        private static final String TAG = "RetrieveNotesRunnable";
        //private final String URL ="http://10.0.2.2:3000/"; //device is running on a VM so localhost is not recognized
        private final String URL ="https://powerful-hamlet-43118.herokuapp.com/"; //device is running on a VM so localhost is not recognized

        private RetrieveNotesRunnable(RecyclerView recycler, Context context, GlobalClass global, String user_id){
            this.recycler = recycler;
            this.context = context;
            this.global = global;
            this.user_id = user_id;
        }

        @Override
        public void run() {
            Gson gson = new Gson();
            //String tmp = gson.toJson(global.getList());
            //Log.d(TAG, "run: json test:"+tmp);ù
            //for more reactive behaviour
            if(global.isFirst()) readDisk();
            recycler.post(new Runnable() {
                @Override
                public void run() {
                    RecyclerViewAdapter recycleAdapter = new RecyclerViewAdapter(global.getList());
                    recycler.setAdapter(recycleAdapter);
                    recycler.setLayoutManager(new LinearLayoutManager(context));
                }
            });

            checkConnection();
            if(internet) cloud();
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            String id = accessToken.getUserId();
            InternalStorage.writeNotesInternalStorage(global,context, id);

        }
        //checks open networks. These networks do not necessarely have internet connection.
        private void checkConnection(){
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            internet = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            Log.d(TAG, "checkConnection: "+internet);
        }
        //dowloads notes from cloud
        private void cloud(){
            Gson gson = new Gson();
            global.lock();
            String tmp = gson.toJson(global.getList());
            global.unlock();
            String json;
            if(tmp==null || tmp.equals("null")) json = "{\"list\":[]}";
            else json = "{\"list\":"+tmp+"}";
            Log.d(TAG, "cloud: json string="+json);


            try {
                answer = HttpConn.syncList(context,URL+"users/"+user_id,"PUT",json);
            } catch (IOException e) {
                e.printStackTrace();
                internet = false;
                setTimer(context);
                return;
            }
            //update interface
            if(answer == null) {
                internet = false;
                setTimer(context);
                return;
            }

/*
            if(global.isModified()) {
                global.setModified(false);
                global.unlock();
                return;
            }
            */
            global.lock();
            global.setList(answer.getNotes());
            global.unlock();
            recycler.post(new Runnable() {
                @Override
                public void run() {
                    RecyclerViewAdapter recycleAdapter = new RecyclerViewAdapter(answer.getNotes());
                    recycler.setAdapter(recycleAdapter);
                    recycler.setLayoutManager(new LinearLayoutManager(context));
                }
            });
            deleteFlaggedFromInternal();
            internet = true;
            stopTimer();
        }
        /*
        * Deletes notes flagged for delete fromt the internal storage
         */
        private void deleteFlaggedFromInternal(){
            Note[] list = global.getList();
            boolean deleted = false;
            for(Note note:list){
                if(note.isDelete()) {
                    global.delete(note.getId());
                    deleted = true;
                }
            }
            if(deleted) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                String id = accessToken.getUserId();
                InternalStorage.readNotesInternalStorage(context, id);
            }


        }
        private void readDisk(){
            global.setFirst(false);
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            String id = accessToken.getUserId();
            answer = InternalStorage.readNotesInternalStorage(context, id);
            if(answer ==null) return;
            global.setList(answer.getNotes());
        }
    }


}
