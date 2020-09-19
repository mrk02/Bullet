package com.mrk02.bullet.repository;

import com.mrk02.bullet.repository.model.Forum;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ForumDao {

  @Query("SELECT * FROM Forum")
  LiveData<List<Forum>> findAll();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insert(Forum forum);

  @Delete
  void delete(Forum forum);

}
