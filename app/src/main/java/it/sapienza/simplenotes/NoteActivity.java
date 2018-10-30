package it.sapienza.simplenotes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import java.util.Date;

public class NoteActivity extends AppCompatActivity {
    private static final String TAG = "NoteActivity";
    private NoteManager mManager;
    private EditText title;
    private EditText text;
    private boolean newNote;
    private GlobalClass global;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        global = (GlobalClass) getApplicationContext();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("New Note");
        //show backbutton
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        newNote = getIntent().getBooleanExtra("new", true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title = findViewById(R.id.title);
        text = findViewById(R.id.text);

        mManager = new NoteManager(global);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: text="+text.getText().toString());
                mManager.save(title.getText().toString() , text.getText().toString(), new Date(), newNote, -1);
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp: "+keyCode);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void finish() {
        super.finish();
        Log.d(TAG, "finish: list title = "+ title.getText().toString());
        Log.d(TAG, "finish: list text = "+ text.getText().toString());
        mManager.saveAll(title.getText().toString() , text.getText().toString(), new Date(), newNote, -1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: stoppped!");
        mManager.saveAll(title.getText().toString() , text.getText().toString(), new Date(), newNote, -1);
    }
}
