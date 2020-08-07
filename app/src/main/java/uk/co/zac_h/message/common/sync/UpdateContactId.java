package uk.co.zac_h.message.common.sync;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.telephony.PhoneNumberUtils;

import java.util.ArrayList;
import java.util.List;

import uk.co.zac_h.message.common.utils.Contact;
import uk.co.zac_h.message.database.DatabaseHelper;
import uk.co.zac_h.message.database.databaseModel.MessageModel;

public class UpdateContactId extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private final ArrayList<String> number = new ArrayList<>();
    private DatabaseHelper db;
    private List<MessageModel> messageModels;

    public UpdateContactId(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        db = new DatabaseHelper(context);

        Intent updateConvThread = new Intent("sms.latest");
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(updateConvThread));

        messageModels = db.getAllNumbers();


    }

    @Override
    protected Void doInBackground(Void... params) {
        for (MessageModel messageModel: messageModels) {
            number.add(messageModel.getNumber());
        }

        for (int i = 0; i < number.size(); i++) {
            String id = new Contact(context).getContactIdFromNumber(number.get(i));
            switch (id) {
                case "":
                    for (int j = 0; j < number.size(); j++) {
                        if (PhoneNumberUtils.compare(number.get(i), number.get(j))) {
                            //Use the preferred number
                            if (number.get(i).equals(number.get(j))) {
                                return null;
                            } else if (number.get(i).contains("+")) {
                                db.updateNumber(number.get(i), number.get(j));
                            } else {
                                db.updateNumber(number.get(j), number.get(i));
                            }
                        }
                    }
                    db.updateID("-1", number.get(i));
                    break;
                case "-1":
                    for (int j = 0; j < number.size(); j++) {
                        if (PhoneNumberUtils.compare(number.get(i), number.get(j))) {
                            //Use the preferred number
                            if (number.get(i).equals(number.get(j))) {
                                System.out.println("THEY ARE THE SAME!");
                            } else if (number.get(i).contains("+")) {
                                db.updateNumber(number.get(i), number.get(j));
                            } else {
                                db.updateNumber(number.get(j), number.get(i));
                            }
                        }
                    }
                    break;
                default:
                    db.updateID(id, number.get(i));
                    break;
            }
        }

        db.close();

        return null;
    }
}
