package uk.co.zac_h.message.database.databaseModel;

public class MessageModel {

    private String number;
    private String body;
    private String date;
    private String read;
    private String messageType;

    public MessageModel() {}

    public MessageModel(String number, String body, String date, String read, String messageType) {
        this.number = number != null? number : this.number;
        this.body = body != null? body : this.body;
        this.date = date != null? date : this.date;
        this.read = read != null? read : this.read;
        this.messageType = messageType != null? messageType : this.messageType;
    }

    public String getNumber() {
        return number;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }

    public String getRead() {
        return read;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
