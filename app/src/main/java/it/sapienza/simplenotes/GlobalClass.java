package it.sapienza.simplenotes;

import android.app.Application;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import it.sapienza.simplenotes.model.Note;

public class GlobalClass extends Application {
    private Note[] list;

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

    public synchronized boolean delete(Note delete){
        if(list == null) return false;
        boolean result = false;
        Note[] tmp = new Note[list.length-1];
        for(int i=0;i<list.length;i++){
            if(list[i].equals(delete)) {
                result = true;
                continue;
            } else if(i<tmp.length) tmp[i]=list[i];
        }
        return result;
    }

    public synchronized boolean delete(long delete){
        if(list == null) return false;
        boolean result = false;
        Note[] tmp = new Note[list.length-1];
        for(int i=0;i<list.length;i++){
            if(list[i].getId() == delete) {
                result = true;
                continue;
            } else if(i<tmp.length) tmp[i]=list[i];
        }
        return result;
    }
}
