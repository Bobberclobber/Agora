package se.liu.ida.josfa969.tddd80.item_records;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Josef on 27/07/14.
 *
 * A class used to contain info regarding
 * messages which are displayed at
 * different places throughout the app
 *
 * Implements Parcelable so that ArrayLists of
 * this object can be sent with an intent
 */
public class MessageRecord implements Parcelable {

    public String sender;
    public String receiver;
    public String messageText;

    public MessageRecord(String sender, String receiver, String messageText) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageText = messageText;
    }

    private MessageRecord(Parcel in) {
        // This order must match the order in writeToParcel()
        sender = in.readString();
        receiver = in.readString();
        messageText = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(sender);
        out.writeString(receiver);
        out.writeString(messageText);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MessageRecord> CREATOR = new Creator<MessageRecord>() {
        @Override
        public MessageRecord createFromParcel(Parcel in) {
            return new MessageRecord(in);
        }

        @Override
        public MessageRecord[] newArray(int size) {
            return new MessageRecord[size];
        }
    };
}
