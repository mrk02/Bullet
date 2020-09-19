package com.mrk02.bullet.ui.forum;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

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
  private static int forumId = -1;

  private final MutableLiveData<Page> livePage = new MutableLiveData<>();

  /**
   * @param application The application.
   */
  public ForumViewModel(@NonNull Application application) {
    super(application);
  }

  @NonNull
  public static Config loadConfig(@NonNull Context context, int forumId) {
    if (ForumViewModel.forumId != forumId) {
      synchronized (ForumViewModel.class) {
        if (ForumViewModel.forumId != forumId) {
          try (InputStream inputStream = new FileInputStream(MainForumDialogViewModel.getConfigFile(context, forumId))) {
            ForumViewModel.config = ConfigLoader.INSTANCE.load(Objects.requireNonNull(inputStream));
            ForumViewModel.forumId = forumId;
          } catch (Exception e) {
            ForumViewModel.config = null;
            ForumViewModel.forumId = -1;
            throw new RuntimeException(e);
          }
        }
      }
    }

    return config;
  }

  /**
   * @param forumId The id of the forum whose config file to load.
   * @param type    The type of the page to load.
   * @param url     The url of the page to load.
   */
  public void loadPage(int forumId, String type, @NonNull String url) {
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
   * @return the page.
   */
  public LiveData<Page> getPage() {
    return livePage;
  }

}