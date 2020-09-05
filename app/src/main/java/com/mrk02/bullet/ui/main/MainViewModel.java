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
   * @param forum The forum to update.
   */
  public void updateForum(Forum forum) {
    AsyncTask.execute(() -> forumDao.update(forum));
  }

  /**
   * @param forum The forum to delete.
   */
  public void deleteForum(Forum forum) {
    AsyncTask.execute(() -> forumDao.delete(forum));
  }

  /**
   * @param configUri
   * @throws Exception
   */
  public void insertForum(Uri configUri) throws Exception {
    AsyncTask.execute(() -> {
      final ContentResolver contentResolver = getApplication().getContentResolver();
      try (final InputStream inputStream = contentResolver.openInputStream(configUri)) {
        final Config config = ConfigLoader.INSTANCE.load(Objects.requireNonNull(inputStream));
        final Page page = config.parse(Config.MAIN, null);

        forumDao.insert(Forum.builder()
            .setName(page.getTitle())
            .setConfig(configUri.toString())
            .build());

      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }
}