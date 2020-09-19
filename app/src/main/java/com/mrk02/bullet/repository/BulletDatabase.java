package com.mrk02.bullet.repository;

import android.content.Context;

import com.mrk02.bullet.repository.model.Forum;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Forum.class}, version = 1, exportSchema = false)
public abstract class BulletDatabase extends RoomDatabase {

  private static BulletDatabase database;

  /**
   * @param context
   * @return
   */
  public static BulletDatabase instance(Context context) {
    if (database == null) {
      synchronized (context) {
        if (database == null) {
          database = Room.databaseBuilder(context, BulletDatabase.class, "bullet-db").build();
        }
      }
    }
    return database;
  }

  /**
   * @return
   */
  public abstract ForumDao forumDao();

}
