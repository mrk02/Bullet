package com.mrk02.bullet.ui.main;

import android.app.Application;

import com.mrk02.bullet.model.Forum;
import com.mrk02.bullet.repository.BulletDatabase;
import com.mrk02.bullet.repository.ForumDao;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MainForumsViewModel extends AndroidViewModel {

  private final ForumDao forumDao;

  public MainForumsViewModel(@NonNull Application application) {
    super(application);

    final BulletDatabase database = BulletDatabase.instance(application);
    forumDao = database.forumDao();
  }

  /**
   * @return All forums.
   */
  public LiveData<List<Forum>> findAllForums() {
    return forumDao.findAll();
  }

}