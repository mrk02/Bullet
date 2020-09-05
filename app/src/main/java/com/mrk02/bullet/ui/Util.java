package com.mrk02.bullet.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public final class Util {

  private Util() {

  }

  /**
   * @param liveData The liveData to observe.
   * @param observer The observer with which to observe the liveData.
   * @param <T>      The type of the liveDatas value.
   */
  public static <T> void observeOnce(LiveData<T> liveData, Observer<T> observer) {
    liveData.observeForever(new Observer<T>() {
      @Override
      public void onChanged(T t) {
        liveData.removeObserver(this);
        observer.onChanged(t);
      }
    });
  }

}
