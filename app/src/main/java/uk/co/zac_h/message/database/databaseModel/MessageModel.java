package uk.co.zac_h.message.database.databaseModel;

public class MessageModel {

    private String number;
    private String body;

    public MessageModel() {}

    public MessageModel(String number, String body) {
        this.number = number != null? number : this.number;
        this.body = body != null? body : this.body;
    }

    public String getNumber() {
        return number;
    }

    public String getBody() {
        return body;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
