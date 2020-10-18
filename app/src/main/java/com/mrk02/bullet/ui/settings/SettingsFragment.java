package com.mrk02.bullet.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrk02.bullet.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

public final class SettingsFragment extends Fragment {

  public static Fragment newInstance() {
    return new SettingsFragment();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.settings_fragment, container, false);
  }

  /**
   *
   */
  public static final class PreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
      setPreferencesFromResource(R.xml.settings, rootKey);
    }

  }

}
