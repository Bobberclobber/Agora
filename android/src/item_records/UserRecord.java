package se.liu.ida.josfa969.tddd80.item_records;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Josef on 03/08/14.
 *
 * A class used to contain info regarding users
 * which are displayed at different
 * places throughout the app
 *
 * Implements Parcelable so that ArrayLists of
 * this object can be sent with an intent
 */
public class UserRecord implements Parcelable {
    public String userName;
    public String eMail;
    public String country;
    public String city;
    public String followers;
    public String image;

    public UserRecord(String userName, String eMail, String country, String city, String followers, String image) {
        this.userName = userName;
        this.eMail = eMail;
        this.country = country;
        this.city = city;
        this.followers = followers;
        this.image = image;
    }

    private UserRecord(Parcel in) {
        // This order must match the order in writeToParcel()
        userName = in.readString();
        eMail = in.readString();
        country = in.readString();
        city = in.readString();
        followers = in.readString();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(userName);
        out.writeString(eMail);
        out.writeString(country);
        out.writeString(city);
        out.writeString(followers);
        out.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserRecord> CREATOR = new Creator<UserRecord>() {
        @Override
        public UserRecord createFromParcel(Parcel in) {
            return new UserRecord(in);
        }

        @Override
        public UserRecord[] newArray(int size) {
            return new UserRecord[size];
        }
    };
}
