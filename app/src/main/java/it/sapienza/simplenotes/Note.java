package it.sapienza.simplenotes;

import java.util.Date;

public class Note implements Comparable<Note> {
    private String title;
    private String text;
    private Date date;
    private int ID;

    Note(String title, String text, Date date, int ID){
        this.title = title;
        this.text = text;
        this.date = date;
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getID() { return ID; }

    @Override
    public int compareTo(Note n) {
        return this.date.compareTo(n.date);
    }
}
