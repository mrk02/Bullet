package com.mrk02.bullet.ui.main;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.mrk02.bullet.model.Forum;
import com.mrk02.bullet.repository.BulletDatabase;
import com.mrk02.bullet.repository.ForumDao;
import com.mrk02.bullet.service.Config;
import com.mrk02.bullet.service.ConfigLoader;
import com.mrk02.bullet.service.Page;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainForumDialogViewModel extends AndroidViewModel {

  private final ForumDao forumDao;

  public MainForumDialogViewModel(@NonNull Application application) {
    super(application);

    final BulletDatabase database = BulletDatabase.instance(application);
    forumDao = database.forumDao();
  }

  public static File getConfigFile(Context context, long forumId) {
    return new File(context.getFilesDir(), "config/" + forumId);
  }

  /**
   * @param forum The forum to insert.
   */
  public void insertForum(Forum forum) {
    AsyncTask.execute(() -> {
      final long forumId = forumDao.insert(forum);

      final File file = getConfigFile(getApplication(), forumId);
      final File directory = Objects.requireNonNull(file.getParentFile());
      //noinspection ResultOfMethodCallIgnored
      directory.mkdirs();
      try (InputStream inputStream = openUri(Uri.parse(forum.config));
           OutputStream outputStream = new FileOutputStream(file)) {
        final byte[] buffer = new byte[4 * 1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
          outputStream.write(buffer, 0, read);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  /**
   * @param forum The forum to delete.
   */
  public void deleteForum(Forum forum) {
    AsyncTask.execute(() -> {
      final File file = getConfigFile(getApplication(), forum.id);
      if (!file.delete()) {
        throw new IllegalStateException("Could not delete file: " + file);
      }

      forumDao.delete(forum);
    });
  }

  /**
   * @param configUri The uri from which to load the config file.
   * @param url       The url of the page to load.
   */
  public LiveData<Page> loadPage(@NonNull Uri configUri, @NonNull String url) {
    final MutableLiveData<Page> liveData = new MutableLiveData<>();

    AsyncTask.execute(() -> {
      try (InputStream inputStream = openUri(configUri)) {
        final Config config = ConfigLoader.INSTANCE.load(inputStream);
        final Page page = config.parse(Config.MAIN, url);
        liveData.postValue(page);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    return liveData;
  }

  @NonNull
  private InputStream openUri(@NonNull Uri uri) throws FileNotFoundException {
    final ContentResolver contentResolver = getApplication().getContentResolver();
    return Objects.requireNonNull(contentResolver.openInputStream(uri));
  }

}