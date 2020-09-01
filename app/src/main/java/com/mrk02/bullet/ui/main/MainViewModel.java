package com.mrk02.bullet.ui.main;

import android.app.Application;
import android.os.AsyncTask;

import com.mrk02.bullet.model.Forum;
import com.mrk02.bullet.repository.BulletDatabase;
import com.mrk02.bullet.repository.ForumDao;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

public class MainViewModel extends AndroidViewModel {

  private final ForumDao forumDao;

  public MainViewModel(@NonNull Application application) {
    super(application);

    final BulletDatabase database = Room.databaseBuilder(application, BulletDatabase.class, "bullet-db").build();
    forumDao = database.forumDao();
  }

  /**
   * @return All forums.
   */
  public LiveData<List<Forum>> findAllForums() {
    return forumDao.findAll();
  }

  /**
   * @param forum The forum to insert.
   */
  public void insertForum(Forum forum) {
    AsyncTask.execute(() -> forumDao.insert(forum));
  }

  /**
   * @param forum The forum to delete.
   */
  public void deleteForum(Forum forum) {
    AsyncTask.execute(() -> forumDao.delete(forum));
  }
}