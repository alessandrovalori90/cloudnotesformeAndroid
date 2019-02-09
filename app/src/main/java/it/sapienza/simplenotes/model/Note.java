package it.sapienza.simplenotes.model;

import java.util.Date;

public class Note implements Comparable<Note> {
    private String title;
    private String text;
    private Date date;
    private long id;
    private String user_id;
    private boolean delete = false;

    public Note(String title, String text, Date date, long id, String user_id){
        this.title = title;
        this.text = text;
        this.date = date;
        this.id = id;
        this.user_id = user_id;
    }

    public String getTitle() {
        if(title==null) return "";
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        if(text==null) return "";
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

    public long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getUser_id() { return user_id; }

    public void setUser_id(String user_id) { this.user_id = user_id; }

    @Override
    public int compareTo(Note n) {
        return n.date.compareTo(this.date);
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isDelete() {
        return delete;
    }
}
