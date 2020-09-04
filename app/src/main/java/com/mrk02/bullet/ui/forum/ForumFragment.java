package com.mrk02.bullet.ui.forum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.mrk02.bullet.R;
import com.mrk02.bullet.ui.PagerAdapter;
import com.mrk02.bullet.ui.main.MainBookmarksFragment;
import com.mrk02.bullet.ui.main.MainForumsFragment;
import com.mrk02.bullet.ui.main.MainViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

public class ForumFragment extends Fragment {

  private ForumViewModel vm;

  public static ForumFragment newInstance() {
    return new ForumFragment();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.forum_fragment, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (vm == null) {
      vm = new ViewModelProvider(getActivity()).get(ForumViewModel.class);
    }
  }

}