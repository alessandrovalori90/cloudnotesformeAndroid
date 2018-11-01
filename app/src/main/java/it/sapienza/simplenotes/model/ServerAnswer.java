package it.sapienza.simplenotes.model;

public class ServerAnswer {
    private String result;
    private String message;
    private int id;

    public ServerAnswer(String result, String message, int id){
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
