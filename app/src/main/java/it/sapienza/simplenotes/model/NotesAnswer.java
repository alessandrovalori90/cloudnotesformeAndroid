package it.sapienza.simplenotes.model;

public class NotesAnswer {
    private String result;
    private Note[] notes;

    public NotesAnswer(String result, Note[] notes){
        this.result = result;
        this.notes = notes;
    }

    public String getResult() { return result; }

    public void setResult(String result) { this.result = result; }

    public Note[] getNotes() { return notes; }

    public void setNotes(Note[] notes) { this.notes = notes; }
}
