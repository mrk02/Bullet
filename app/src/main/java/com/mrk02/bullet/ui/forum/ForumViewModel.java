package com.mrk02.bullet.ui.forum;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.mrk02.bullet.repository.BookmarkDao;
import com.mrk02.bullet.repository.BulletDatabase;
import com.mrk02.bullet.repository.model.Bookmark;
import com.mrk02.bullet.service.Config;
import com.mrk02.bullet.service.ConfigLoader;
import com.mrk02.bullet.service.model.Page;
import com.mrk02.bullet.ui.main.MainForumDialogViewModel;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ForumViewModel extends AndroidViewModel {

  private static Config config;
  private static int configForumId = -1;

  private final int forumId;
  private final String type;
  private final String url;

  private final BookmarkDao bookmarkDao;

  private final MutableLiveData<Page> livePage = new MutableLiveData<>();
  private final LiveData<Bookmark> liveBookmark;

  /**
   * @param application The application.
   * @param forumId     The forumId.
   * @param type        The type.
   * @param url         The url.
   */
  public ForumViewModel(@NonNull Application application, int forumId, String type, String url) {
    super(application);

    this.forumId = forumId;
    this.type = type;
    this.url = url;

    final BulletDatabase database = BulletDatabase.instance(application);
    bookmarkDao = database.bookmarkDao();
    liveBookmark = bookmarkDao.find(forumId, url);

    loadPage();
  }

  @NonNull
  public static Config loadConfig(@NonNull Context context, int forumId) {
    if (ForumViewModel.configForumId != forumId) {
      synchronized (ForumViewModel.class) {
        if (ForumViewModel.configForumId != forumId) {
          try (InputStream inputStream = new FileInputStream(MainForumDialogViewModel.getConfigFile(context, forumId))) {
            ForumViewModel.config = ConfigLoader.INSTANCE.load(Objects.requireNonNull(inputStream));
            ForumViewModel.configForumId = forumId;
          } catch (Exception e) {
            ForumViewModel.config = null;
            ForumViewModel.configForumId = -1;
            throw new RuntimeException(e);
          }
        }
      }
    }

    return config;
  }

  /**
   *
   */
  public void loadPage() {
    livePage.setValue(null);
    AsyncTask.execute(() -> {
      try {
        final Config config = loadConfig(getApplication(), forumId);
        final Page page = config.parse(type, url);
        livePage.postValue(page);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  /**
   *
   */
  public void insertBookmark() {
    AsyncTask.execute(() -> bookmarkDao.insert(Bookmark.builder()
        .setForumId(forumId)
        .setUrl(url)
        .setType(type)
        .setName(Objects.requireNonNull(livePage.getValue()).title())
        .build()));
  }

  /**
   *
   */
  public void deleteBookmark() {
    AsyncTask.execute(() -> bookmarkDao.delete(forumId, url));
  }

  /**
   * @return The url of the page managed by this view model.
   */
  public String getUrl() {
    return url;
  }

  /**
   * @return the page managed by this view model.
   */
  public LiveData<Page> getPage() {
    return livePage;
  }

  /**
   * @return the bookmark of this page.
   */
  public LiveData<Bookmark> getBookmark() {
    return liveBookmark;
  }

}