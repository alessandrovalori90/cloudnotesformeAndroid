package it.sapienza.simplenotes;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;

import java.util.concurrent.Semaphore;

import it.sapienza.simplenotes.model.Note;
import it.sapienza.simplenotes.model.Settings;

public class GlobalClass extends Application {
    private static final String TAG = "GlobalClass";
    private Note[] list;
    private Settings settings = new Settings();
    private boolean first = true; //first time loading main activity
    private Semaphore semaphore = new Semaphore(1);
    private boolean modified = false;

    public GlobalClass() { }

    public Note[] getList() {
        return list;
    }

    public synchronized void setList(Note list[]){ this.list =list; }

    public synchronized void update(Note add){
        if(list == null){
            Note[] tmp = new Note[1];
            tmp[0] = add;
            list= tmp;
            return;
        }
        Note[] tmp = new Note[list.length+1];
        for(int i=0;i<list.length;i++) {
            if(add.getId()==list[i].getId()){
                list[i] = add;
                return;
            }
            tmp[i] = list[i];
        }
        tmp[list.length]=add;
        list = tmp;
    }

    public synchronized boolean delete(long delete){
        if(list == null) return false;
        boolean result = false;
        Note[] tmp = new Note[list.length-1];
        for(int i=0;i<list.length;i++){
            if(list[i].getId() == delete)
                result = true;
            else if(i<tmp.length)
                tmp[i]=list[i];
        }
        return result;
    }
    //if cant connect to the server at the moment of the delete the note to be modified is marked with the delete flag
    public synchronized void deleteLater(long delete){
        if(list == null) return;
        for (Note aList : list) {
            if (aList.getId() == delete) {
                aList.setDelete(true);
                Log.d(TAG, "deleteLater: id: " + aList.getId() + " delete: " + aList.isDelete());
            }
        }
        Gson gson = new Gson();
        String tmp = gson.toJson(getList());
        Log.d(TAG, "deleteLater: json: "+tmp);
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings){
        this.settings = settings;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    //if syncronized does not work somehow threads block each other
    public void lock() {
        try {
            semaphore.acquire();
            Log.d(TAG, "locked!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unlock() {
        semaphore.release();
        Log.d(TAG, "unlocked!");
    }
    //obsolete
    public synchronized boolean isModified() {
        return modified;
    }
    //obsolete
    public synchronized void setModified(boolean modified) {
        this.modified = modified;
    }
}
