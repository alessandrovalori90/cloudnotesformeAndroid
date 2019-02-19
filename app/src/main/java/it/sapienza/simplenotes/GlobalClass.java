package it.sapienza.simplenotes;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;

import java.util.concurrent.Semaphore;

import it.sapienza.simplenotes.model.Note;
import it.sapienza.simplenotes.model.Settings;

public class GlobalClass extends Application {
    private static final String TAG = "GlobalClass";
    private Note[] list; //use array instead of list because in the Recycler view we need to acces precise positions due to the cancel label
    private Settings settings = new Settings();
    private boolean first = true; //first time loading main activity
    private Semaphore semaphore = new Semaphore(1);
    private boolean modified = false;

    public GlobalClass() { }

    public Note[] getList() {
        return list;
    }

    public synchronized void setList(Note serverList[]) {
        /*
        since the access to the list in synchronized there is a case where:
        - thread for comunicating with server reads
        - note activity writes
        - thread for comunicating with server writes

        in this case the note activity modification is override if dont do this check before saving it.
         */

        if(list!=null & serverList!=null){
            for(int i=0;i<serverList.length;i++) {
                for(int j=0;j<list.length;j++) {
                    if ( Math.abs(list[j].getId()) == Math.abs(serverList[i].getId()) & list[j].compareTo(serverList[i])>0){ //absoulte value to compare ids because the id of new notes is negative.
                        serverList[i]=list[j];
                    }
                }
            }
        }

        this.list = serverList;
        bubbleSort();
    }

    //bubble sort because memory is O(1) is in place and easy to implement
    //the fact that is O(n^2) in time is relevant becasuse we expect small lists
    synchronized void bubbleSort() {
        for (int i = (list.length - 1); i >= 0; i--)
        {
            for (int j = 1; j <= i; j++)
            {
                if (list[j-1].compareTo(list[j])>0) //the first note greater then the one in the argument
                {
                    Note temp = list[j-1];
                    list[j-1] = list[j];
                    list[j] = temp;
                }
            }
        }
    }

    //adds a note to the list
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
        bubbleSort();
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
