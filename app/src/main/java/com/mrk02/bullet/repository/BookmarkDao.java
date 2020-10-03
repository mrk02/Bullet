package com.mrk02.bullet.repository;

import com.mrk02.bullet.repository.model.Bookmark;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface BookmarkDao {

  @Query("SELECT * FROM Bookmark")
  LiveData<List<Bookmark.WithForum>> findAll();

  @Query("SELECT * FROM Bookmark WHERE forumId = :forumId AND url = :url")
  LiveData<Bookmark> find(int forumId, String url);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insert(Bookmark bookmark);

  @Delete
  void delete(Bookmark bookmark);

  @Query("DELETE FROM Bookmark WHERE forumId = :forumId AND url = :url")
  void delete(int forumId, String url);

}
