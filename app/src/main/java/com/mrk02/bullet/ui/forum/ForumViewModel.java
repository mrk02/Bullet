package com.mrk02.bullet.ui.forum;

import android.app.Application;

import com.mrk02.bullet.repository.BulletDatabase;
import com.mrk02.bullet.repository.ForumDao;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class ForumViewModel extends AndroidViewModel {

  private final ForumDao forumDao;

  public ForumViewModel(@NonNull Application application) {
    super(application);

    final BulletDatabase database = BulletDatabase.instance(application);
    forumDao = database.forumDao();
  }
  
}