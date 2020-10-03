package com.mrk02.bullet.ui.main;

import android.app.Application;
import android.os.AsyncTask;

import com.mrk02.bullet.repository.BookmarkDao;
import com.mrk02.bullet.repository.BulletDatabase;
import com.mrk02.bullet.repository.model.Bookmark;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MainBookmarkDialogViewModel extends AndroidViewModel {

  private final BookmarkDao bookmarkDao;
  private Bookmark bookmark;

  public MainBookmarkDialogViewModel(@NonNull Application application, @NonNull Bookmark bookmark) {
    super(application);

    this.bookmark = bookmark;

    final BulletDatabase database = BulletDatabase.instance(application);
    bookmarkDao = database.bookmarkDao();
  }

  /**
   * @param bookmark The bookmark to insert.
   */
  public void insertBookmark(Bookmark bookmark) {
    AsyncTask.execute(() -> {
      bookmarkDao.insert(bookmark);
      this.bookmark = bookmark;
    });
  }

  /**
   *
   */
  public void deleteBookmark() {
    AsyncTask.execute(() -> {
      bookmarkDao.delete(bookmark);
      bookmark = null;
    });
  }

  /**
   * @return The bookmark.
   */
  @NonNull
  public Bookmark getBookmark() {
    return bookmark;
  }

}