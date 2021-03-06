package com.mrk02.bullet.repository.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

@Entity(foreignKeys =
@ForeignKey(entity = Forum.class, parentColumns = "id", childColumns = "forumId", onDelete = ForeignKey.CASCADE))
public class Bookmark implements Parcelable {

  public static final Creator<Bookmark> CREATOR = new Creator<Bookmark>() {
    @Override
    public Bookmark createFromParcel(Parcel in) {
      return new Bookmark(in);
    }

    @Override
    public Bookmark[] newArray(int size) {
      return new Bookmark[size];
    }
  };
  @PrimaryKey(autoGenerate = true)
  public final int id;
  @ColumnInfo
  public final int forumId;
  @ColumnInfo
  public final String name;
  @ColumnInfo
  public final String type;
  @ColumnInfo
  public final String url;

  public Bookmark(int id, int forumId, String name, String type, String url) {
    this.id = id;
    this.forumId = forumId;
    this.name = name;
    this.type = type;
    this.url = url;
  }

  protected Bookmark(Parcel in) {
    id = in.readInt();
    forumId = in.readInt();
    name = in.readString();
    type = in.readString();
    url = in.readString();
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeInt(forumId);
    dest.writeString(name);
    dest.writeString(type);
    dest.writeString(url);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  public static final class Builder {

    private int id;
    private int forumId;
    private String name;
    private String type;
    private String url;

    private Builder() {

    }

    private Builder(Bookmark bookmark) {
      this.id = bookmark.id;
      this.forumId = bookmark.forumId;
      this.name = bookmark.name;
      this.type = bookmark.type;
      this.url = bookmark.url;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setForumId(int forumId) {
      this.forumId = forumId;
      return this;
    }

    public Builder setUrl(String url) {
      this.url = url;
      return this;
    }

    public Builder setType(String type) {
      this.type = type;
      return this;
    }

    public Bookmark build() {
      return new Bookmark(id, forumId, name, type, url);
    }
  }

  /**
   *
   */
  public static final class WithForum extends Bookmark {

    @Relation(parentColumn = "forumId", entityColumn = "id")
    public final Forum forum;

    public WithForum(int id, int forumId, String name, String type, String url, Forum forum) {
      super(id, forumId, name, type, url);
      this.forum = forum;
    }
  }

}
