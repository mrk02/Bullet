package com.mrk02.bullet.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.mrk02.bullet.R;
import com.mrk02.bullet.ui.PagerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

public class MainFragment extends Fragment {

  private MainViewModel vmMain;

  public static MainFragment newInstance() {
    return new MainFragment();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.main_fragment, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    final Toolbar toolbar = view.findViewById(R.id.main_toolbar);

    new MenuInflater(getContext()).inflate(R.menu.main, toolbar.getMenu());

    final PagerAdapter adapter = new PagerAdapter(getContext(), getChildFragmentManager())
        .page(R.string.main_forums_title, MainForumsFragment::newInstance)
        .page(R.string.main_bookmarks_title, MainBookmarksFragment::newInstance);

    final ViewPager pager = view.findViewById(R.id.main_pager);
    pager.setAdapter(adapter);
    final TabLayout tabs = view.findViewById(R.id.main_tabs);
    tabs.setupWithViewPager(pager);
  }

}