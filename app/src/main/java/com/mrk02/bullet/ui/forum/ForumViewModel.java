package com.mrk02.bullet.ui.forum;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

public class ForumViewModel extends AndroidViewModel {

  private static Config config;
  private static int configForumId = -1;

  private final int forumId;
  private final String type;
  private final String url;

  private final BookmarkDao bookmarkDao;

  private final List<Section> sections = Arrays.asList(
      new Section(R.string.forum_section_boards, Page::boards),
      new Section(R.string.forum_section_threads, Page::threads));

  private final MutableLiveData<Page> livePage = new MutableLiveData<>();
  private final MutableLiveData<List<Object>> liveItems = new MutableLiveData<>(Collections.emptyList());
  private final MutableLiveData<Exception> liveException = new MutableLiveData<>();
  private final LiveData<Bookmark> liveBookmark;

  private final DateFormat dateFormat;

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

    livePage.observeForever(page -> buildItems());

    loadPage();

    final SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(application);
    final String dateFormat = sharedPreferences.getString(
        application.getString(R.string.settings_forum_date_format_key),
        application.getString(R.string.settings_forum_date_format_default));
    this.dateFormat = new SimpleDateFormat(dateFormat,
        application.getResources().getConfiguration().locale);
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
  public void buildItems() {
    final Page page = livePage.getValue();
    final List<Object> items;
    if (page == null) {
      items = Collections.emptyList();
    } else {
      items = new ArrayList<>();
      for (Section section : sections) {
        section.buildItems(items, page);
      }
    }
    liveItems.postValue(items);
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
   * @return the date format.
   */
  public DateFormat getDateFormat() {
    return dateFormat;
  }

  /**
   *
   */
  public static final class Section {
    public final int text;
    private final Function<Page, List<?>> extractor;
    public boolean expanded;

    private Section(int text, Function<Page, List<?>> extractor) {
      this.text = text;
      this.extractor = extractor;
      this.expanded = true;
    }

    private void buildItems(List<Object> items, Page page) {
      final List<?> sectionItems = extractor.apply(page);
      if (!sectionItems.isEmpty()) {
        items.add(this);
        if (expanded) {
          items.addAll(sectionItems);
        }
      }
    }
  }

}