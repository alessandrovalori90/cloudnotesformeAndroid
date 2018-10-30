package it.sapienza.simplenotes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class CloudSaveRunnable implements Runnable {
    private static final String TAG = "CloudSaveRunnable";
    private Note note;

    public CloudSaveRunnable(Note note) {
        this.note = note;
    }

    @Override
    public void run() {
        /*
        Date date = new Date();
        Note note = new Note("A","B", date);
        global.add(note);
        global.sort();
        Log.d(TAG, "run: "+global.getList().toString());
        */
    }
}
