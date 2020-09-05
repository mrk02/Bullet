package com.mrk02.bullet.ui.main;

import android.app.Application;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.AsyncTask;

import com.mrk02.bullet.model.Forum;
import com.mrk02.bullet.repository.BulletDatabase;
import com.mrk02.bullet.repository.ForumDao;
import com.mrk02.bullet.service.Config;
import com.mrk02.bullet.service.ConfigLoader;
import com.mrk02.bullet.service.Page;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {

  private final ForumDao forumDao;

  public MainViewModel(@NonNull Application application) {
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

  /**
   * @param configUri The uri from which to load the config file.
   */
  public LiveData<Page> loadPage(Uri configUri) {
    final MutableLiveData<Page> liveData = new MutableLiveData<>();

    AsyncTask.execute(() -> {
      final ContentResolver contentResolver = getApplication().getContentResolver();
      try (InputStream inputStream = contentResolver.openInputStream(configUri)) {
        final Config config = ConfigLoader.INSTANCE.load(Objects.requireNonNull(inputStream));
        final Page page = config.parse(Config.MAIN, null);
        liveData.postValue(page);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    return liveData;
  }
}