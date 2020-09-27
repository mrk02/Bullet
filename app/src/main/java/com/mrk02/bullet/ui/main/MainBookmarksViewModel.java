package com.mrk02.bullet.ui.main;

import android.app.Application;

import com.mrk02.bullet.repository.BulletDatabase;
import com.mrk02.bullet.repository.model.Bookmark;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MainBookmarksViewModel extends AndroidViewModel {

  private final LiveData<List<Bookmark.WithForum>> bookmarks;

  public MainBookmarksViewModel(@NonNull Application application) {
    super(application);

    final BulletDatabase database = BulletDatabase.instance(application);
    bookmarks = database.bookmarkDao().findAll();
  }

  /**
   * @return all bookmarks.
   */
  public LiveData<List<Bookmark.WithForum>> getBookmarks() {
    return bookmarks;
  }

}