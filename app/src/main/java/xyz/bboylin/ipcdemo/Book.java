package xyz.bboylin.ipcdemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lin on 2017/6/7.
 * 注意要和book.aidl在同一个包下
 */

public class Book implements Parcelable{
    public int id;
    public String name;
    private String tag;

    public Book(int id, String name) {
        this.id = id;
        this.name = name;
        tag="正常";
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    protected Book(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    @Override
    public String toString() {
        return "book info : "+id+"/"+name+"/"+tag;
    }
}
