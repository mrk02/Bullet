package com.mrk02.bullet.repository;

import com.mrk02.bullet.model.Forum;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Forum.class}, version = 1)
public abstract class BulletDatabase extends RoomDatabase {

  public abstract ForumDao forumDao();

}
