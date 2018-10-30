package it.sapienza.simplenotes;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NoteManager {
    private Thread thread;
    private CloudSaveRunnable runnable;
    private GlobalClass global;
    private ExecutorService service;

    public NoteManager(GlobalClass global) {
        this.global = global;
        service = new ThreadPoolExecutor(1, 1,
                5000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(1, true), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    //saves on cloud
    public void save(String title, String text, Date date, boolean newNote, int position){
        Note note;
        if(newNote) note = new Note(title,text,date,global.obtainID());
        else note = new Note(title,text,date,global.getList().get(position).getID());

        CloudSaveRunnable run = new CloudSaveRunnable(note);
        service.execute(run);
    }

    //saves on cloud and disk
    public void saveAll(String title, String text, Date date, boolean newNote, int position){
        Note note;
        if(newNote){
            note = new Note(title,text,date,global.obtainID());
        } else{
            note = new Note(title,text,date,global.getList().get(position).getID());
            global.getList().remove(position);
        }
        global.getList().add(note);
        CloudSaveRunnable run = new CloudSaveRunnable(note);
        service.execute(run);
    }
}
