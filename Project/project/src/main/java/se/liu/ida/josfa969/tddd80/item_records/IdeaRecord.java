package se.liu.ida.josfa969.tddd80.item_records;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Josef on 27/07/14.
 *
 * A class used to contain info regarding ideas
 * which are displayed at different
 * places throughout the app
 * 
 * Implements Parcelable so that ArrayLists of
 * this object can be sent with an intent
 */
public class IdeaRecord implements Parcelable {
    public String ideaId;
    public String ideaText;
    public String poster;
    public String approvalNum;
    public boolean isApproving;
    public ArrayList tags;

    public IdeaRecord(String ideaId, String ideaText, String poster, String approvalNum, boolean isApproving, ArrayList<String> tags) {
        this.ideaId = ideaId;
        this.ideaText = ideaText;
        this.poster = poster;
        this.approvalNum = approvalNum;
        this.isApproving = isApproving;
        this.tags = tags;
    }

    private IdeaRecord(Parcel in) {
        // This order must match the order in writeToParcel()
        ideaId = in.readString();
        ideaText = in.readString();
        poster = in.readString();
        approvalNum = in.readString();
        isApproving = in.readByte() != 0; // isApproving = true if byte != 0
        tags = in.readArrayList(null);
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(ideaId);
        out.writeString(ideaText);
        out.writeString(poster);
        out.writeString(approvalNum);
        out.writeByte((byte) (isApproving ? 1 : 0)); // if isApproving == true, byte = 1
        out.writeList(tags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IdeaRecord> CREATOR = new Creator<IdeaRecord>() {
        @Override
        public IdeaRecord createFromParcel(Parcel in) {
            return new IdeaRecord(in);
        }

        @Override
        public IdeaRecord[] newArray(int size) {
            return new IdeaRecord[size];
        }
    };
}
