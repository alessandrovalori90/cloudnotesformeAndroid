package it.sapienza.simplenotes;

import android.app.Application;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GlobalClass extends Application {
    private List<Note> list;

    public GlobalClass() {
        list = new LinkedList<Note>();
    }

    public List<Note> getList() {
        return list;
    }

    public boolean add(Note add){
        boolean result = list.add(add);
        sort();
        return result;
    }

    public boolean addList(List<Note> add){
        if(add.size()<1 || add == null) return false;
        Iterator it = add.iterator();
        boolean result;
        while (it.hasNext()) if(list.add((Note) it.next()) == false) return false;
        sort();
        return true;
    }
    //returns first available id
    public int obtainID(){
        if(list == null || list.size() == 0) return 1;
        Iterator<Note> it = list.iterator();
        int result = 1;
        while(it.hasNext()){
            Note temp = it.next();
            if(result < temp.getID()) result = temp.getID();
        }
        return result+1;
    }

    private void sort(){
        Collections.sort(list, Collections.reverseOrder());
    }
}
