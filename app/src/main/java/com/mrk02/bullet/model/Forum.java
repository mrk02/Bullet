package com.mrk02.bullet.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Forum implements Parcelable {

  public static final Creator<Forum> CREATOR = new Creator<Forum>() {
    @Override
    public Forum createFromParcel(Parcel in) {
      return new Forum(in);
    }

    @Override
    public Forum[] newArray(int size) {
      return new Forum[size];
    }
  };
  @PrimaryKey(autoGenerate = true)
  public int id;
  @ColumnInfo
  public String name;
  @ColumnInfo
  public String icon;
  @ColumnInfo
  public String url;

  public Forum() {

  }

  protected Forum(Parcel in) {
    id = in.readInt();
    name = in.readString();
    icon = in.readString();
    url = in.readString();
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeString(name);
    dest.writeString(icon);
    dest.writeString(url);
  }

  @Override
  public int describeContents() {
    return 0;
  }
}
