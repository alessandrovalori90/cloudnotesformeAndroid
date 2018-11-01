package it.sapienza.simplenotes.model;

import java.util.Date;

public class Note implements Comparable<Note> {
    private String title;
    private String text;
    private Date date;
    private int id;
    private String user_id;

    public Note(String title, String text, Date date, int id, String user_id){
        this.title = title;
        this.text = text;
        this.date = date;
        this.id = id;
        this.user_id = user_id;
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

    public int getId() { return id; }

    public String getUser_id() { return user_id; }

    public void setUser_id(String user_id) { this.user_id = user_id; }

    @Override
    public int compareTo(Note n) {
        return this.date.compareTo(n.date);
    }
}
