package se.liu.ida.josfa969.tddd80.item_records;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Josef on 10/08/14.
 *
 * A class used to display comments to an idea
 *
 * Implements Parcelable so that ArrayLists of
 * this object can be sent with an intent
 */
public class CommentRecord implements Parcelable {
    public String user;
    public String commentText;
    public String image;

    public CommentRecord(String user, String commentText, String image) {
        this.user = user;
        this.commentText = commentText;
        this.image = image;
    }

    private CommentRecord(Parcel in) {
        // This order must match the order in writeToParcel()
        user = in.readString();
        commentText = in.readString();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(user);
        out.writeString(commentText);
        out.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CommentRecord> CREATOR = new Creator<CommentRecord>() {
        @Override
        public CommentRecord createFromParcel(Parcel in) {
            return new CommentRecord(in);
        }

        @Override
        public CommentRecord[] newArray(int size) {
            return new CommentRecord[size];
        }
    };
}
