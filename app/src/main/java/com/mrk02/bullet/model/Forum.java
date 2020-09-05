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
  public final int id;
  @ColumnInfo
  public final String name;
  @ColumnInfo
  public final String icon;
  @ColumnInfo
  public final String url;
  @ColumnInfo
  public final String config;

  public Forum(int id, String name, String icon, String url, String config) {
    this.id = id;
    this.name = name;
    this.icon = icon;
    this.url = url;
    this.config = config;
  }

  protected Forum(Parcel in) {
    this.id = in.readInt();
    this.name = in.readString();
    this.icon = in.readString();
    this.url = in.readString();
    this.config = in.readString();
  }

  public static Builder builder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeString(name);
    dest.writeString(icon);
    dest.writeString(url);
    dest.writeString(config);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public static final class Builder {

    private int id;
    private String name;
    private String icon;
    private String url;
    private String config;

    private Builder() {

    }

    private Builder(Forum forum) {
      this.id = forum.id;
      this.name = forum.name;
      this.icon = forum.icon;
      this.url = forum.url;
      this.config = forum.config;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setIcon(String icon) {
      this.icon = icon;
      return this;
    }

    public Builder setUrl(String url) {
      this.url = url;
      return this;
    }

    public Builder setConfig(String config) {
      this.config = config;
      return this;
    }

    public Forum build() {
      return new Forum(id, name, icon, url, config);
    }
  }

}
