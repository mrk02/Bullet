package com.mrk02.bullet.ui.main;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.mrk02.bullet.repository.BulletDatabase;
import com.mrk02.bullet.repository.ForumDao;
import com.mrk02.bullet.repository.model.Forum;
import com.mrk02.bullet.service.Config;
import com.mrk02.bullet.service.ConfigLoader;
import com.mrk02.bullet.service.model.Page;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainForumDialogViewModel extends AndroidViewModel {

  private final ForumDao forumDao;

  private final MutableLiveData<Page> livePage = new MutableLiveData<>();
  private Forum forum;

  public MainForumDialogViewModel(@NonNull Application application, @Nullable Forum forum) {
    super(application);

    this.forum = forum;

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

      if (this.forum == null) {
        final long forumId = forumDao.insert(forum);
        importConfig(forum.config, forumId);
      } else {
        forumDao.update(forum);
        if (!this.forum.config.equals(forum.config)) {
          importConfig(forum.config, forum.id);
        }
      }

      this.forum = forum;
    });
  }

  private void importConfig(String uri, long forumId) {
    final File file = getConfigFile(getApplication(), forumId);
    final File directory = Objects.requireNonNull(file.getParentFile());
    //noinspection ResultOfMethodCallIgnored
    directory.mkdirs();
    try (InputStream inputStream = openUri(Uri.parse(uri));
         OutputStream outputStream = new FileOutputStream(file)) {
      final byte[] buffer = new byte[4 * 1024];
      int read;
      while ((read = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, read);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   *
   */
  public void deleteForum() {
    AsyncTask.execute(() -> {
      final File file = getConfigFile(getApplication(), forum.id);
      if (!file.delete()) {
        throw new IllegalStateException("Could not delete file: " + file);
      }
      forumDao.delete(forum);
      forum = null;
    });
  }

  /**
   * @param configUri The uri from which to load the config file.
   * @param url       The url of the page to load.
   */
  public void loadPage(@NonNull Uri configUri, @NonNull String url) {
    AsyncTask.execute(() -> {
      try (InputStream inputStream = openUri(configUri)) {
        final Config config = ConfigLoader.INSTANCE.load(inputStream);
        final Page page = config.parse(Config.MAIN, url);
        livePage.postValue(page);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  /**
   * @return The page.
   */
  @NonNull
  public LiveData<Page> getPage() {
    return livePage;
  }

  /**
   * @return The forum.
   */
  @Nullable
  public Forum getForum() {
    return forum;
  }

  @NonNull
  private InputStream openUri(@NonNull Uri uri) throws FileNotFoundException {
    final ContentResolver contentResolver = getApplication().getContentResolver();
    return Objects.requireNonNull(contentResolver.openInputStream(uri));
  }
}