package com.mrk02.bullet.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Forum {

  @PrimaryKey(autoGenerate = true)
  public int id;

  @ColumnInfo
  public String name;

  @ColumnInfo
  public String icon;

  @ColumnInfo
  public String url;

}
