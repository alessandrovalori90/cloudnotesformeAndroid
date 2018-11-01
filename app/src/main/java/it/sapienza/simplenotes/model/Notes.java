package it.sapienza.simplenotes.model;

public class Notes {
    private String result;
    private Note[] notes;

    public Notes(String result, Note[] notes){
        this.result = result;
        this.notes = notes;
    }

    public String getResult() { return result; }

    public void setResult(String result) { this.result = result; }

    public Note[] getNotes() { return notes; }

    public void setNotes(Note[] notes) { this.notes = notes; }
}
