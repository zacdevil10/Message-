package uk.co.zac_h.message.database;

import android.content.Context;

import java.util.List;

import uk.co.zac_h.message.database.databaseModel.MessageModel;

public class ReturnData {

    private DatabaseHelper db;
    private String number;
    private String body;
    private Integer firstRun;

    public String getNumber() {
        return number;
    }

    public String getBody() {
        return body;
    }

    public List getAll(Context context) {
        db = new DatabaseHelper(context);

        List<MessageModel> messageModelList = db.getAllMessages();

        db.close();

        return messageModelList;
    }

    public Integer firstRun(Context context) {
        db = new DatabaseHelper(context);

        return db.getFirstRun();
    }

    public void setFirstRun(Context context) {
        db = new DatabaseHelper(context);

        db.setFirstRun();
        db.close();
    }
}
