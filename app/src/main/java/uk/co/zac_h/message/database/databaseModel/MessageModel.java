package uk.co.zac_h.message.database.databaseModel;

public class MessageModel {

    private String number;
    private String body;
    private Integer date;
    private Integer read;

    public MessageModel() {}

    public MessageModel(String number, String body, Integer date, Integer read) {
        this.number = number != null? number : this.number;
        this.body = body != null? body : this.body;
        this.date = date != null? date : this.date;
        this.read = read != null? read : this.read;
    }

    public String getNumber() {
        return number;
    }

    public String getBody() {
        return body;
    }

    public Integer getDate() {
        return date;
    }

    public Integer getRead() {
        return read;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public void setRead(Integer read) {
        this.read = read;
    }
}
