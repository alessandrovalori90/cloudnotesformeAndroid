package it.sapienza.simplenotes.model;

public class NoteAnswer {
    private String result;
    private String message;
    private long id;

    public NoteAnswer(String result, String message, long id){
        this.result = result;
        this.message = message;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
