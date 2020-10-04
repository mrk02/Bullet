package com.mrk02.bullet.ui.forum;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.mrk02.bullet.R;
import com.mrk02.bullet.repository.BookmarkDao;
import com.mrk02.bullet.repository.BulletDatabase;
import com.mrk02.bullet.repository.model.Bookmark;
import com.mrk02.bullet.service.Config;
import com.mrk02.bullet.service.ConfigLoader;
import com.mrk02.bullet.service.model.Page;
import com.mrk02.bullet.ui.main.MainForumDialogViewModel;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

  private final Section sectionBoards = new Section(R.string.forum_section_boards);

  private final MutableLiveData<Page> livePage = new MutableLiveData<>();
  private final MutableLiveData<List<Object>> liveItems = new MutableLiveData<>(Collections.emptyList());
  private final MutableLiveData<Exception> liveException = new MutableLiveData<>();
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

    livePage.observeForever(page -> updateItems());

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
  public void updateItems() {
    final Page page = livePage.getValue();
    final List<Object> items;
    if (page == null) {
      items = Collections.emptyList();
    } else {
      items = new ArrayList<>();
      updateItemsSection(items, sectionBoards, page.boards());
    }
    liveItems.postValue(items);
  }

  private void updateItemsSection(List<Object> items, Section section, List<?> sectionItems) {
    if (!sectionItems.isEmpty()) {
      items.add(section);
      if (section.expanded) {
        items.addAll(sectionItems);
      }
    }
  }

  /**
   *
   */
  public void loadPage() {
    livePage.setValue(null);
    liveException.setValue(null);
    AsyncTask.execute(() -> {
      try {
        final Config config = loadConfig(getApplication(), forumId);
        final Page page = config.parse(type, url);
        livePage.postValue(page);
      } catch (Exception e) {
        liveException.postValue(e);
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
   * @return The id of the forum this page belongs to.
   */
  public int getForumId() {
    return forumId;
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
   * @return any exception that may occur.
   */
  public LiveData<Exception> getException() {
    return liveException;
  }

  /**
   * @return the bookmark of this page.
   */
  public LiveData<Bookmark> getBookmark() {
    return liveBookmark;
  }

  /**
   * @return the items of this page.
   */
  public LiveData<List<Object>> getItems() {
    return liveItems;
  }

  /**
   *
   */
  public static final class Section {
    public final int text;
    public boolean expanded;

    private Section(int text) {
      this.text = text;
      this.expanded = true;
    }
  }

}