package in.co.chicmic.contactapp.dataModels;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {
    private final int mPersonImageId;
    private final String mPersonName;
    private final String mPersonCompanyName;
    private final Uri mImageUri;

    public Person(int pPersonImageId, String pPersonName, String pPersonCompanyName
            , Uri pImageUri) {
        this.mPersonImageId = pPersonImageId;
        this.mPersonName = pPersonName;
        this.mPersonCompanyName = pPersonCompanyName;
        this.mImageUri = pImageUri;
    }


    private Person(Parcel in) {
        mPersonImageId = in.readInt();
        mPersonName = in.readString();
        mPersonCompanyName = in.readString();
        mImageUri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public int getPersonImageId() {
        return mPersonImageId;
    }

    public String getPersonName() {
        return mPersonName;
    }

    public String getPersonCompanyName() {
        return mPersonCompanyName;
    }

    public Uri getImageUri() {
        return mImageUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(mPersonImageId);
        parcel.writeString(mPersonName);
        parcel.writeString(mPersonCompanyName);
        parcel.writeParcelable(mImageUri, i);
    }


}
