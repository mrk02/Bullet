package com.mrk02.bullet.ui.main;

import android.app.Application;

import com.mrk02.bullet.repository.BulletDatabase;
import com.mrk02.bullet.repository.model.Forum;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MainForumsViewModel extends AndroidViewModel {

  private final LiveData<List<Forum>> forums;

  public MainForumsViewModel(@NonNull Application application) {
    super(application);

    final BulletDatabase database = BulletDatabase.instance(application);
    forums = database.forumDao().findAll();
  }

  /**
   * @return all forums.
   */
  public LiveData<List<Forum>> getForums() {
    return forums;
  }

}