package com.mrk02.bullet.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.mrk02.bullet.R;
import com.mrk02.bullet.ui.PagerAdapter;
import com.mrk02.bullet.ui.settings.SettingsFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class MainFragment extends Fragment {

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
    toolbar.setOnMenuItemClickListener(item -> {
      if (item.getItemId() == R.id.main_menu_settings) {
        requireActivity().getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.fragment_open_enter, R.anim.fragment_open_exit, R.anim.fragment_close_enter, R.anim.fragment_close_exit)
            .replace(R.id.container, SettingsFragment.newInstance())
            .addToBackStack(null)
            .commit();
        return true;
      }
      return false;
    });

    final PagerAdapter adapter = new PagerAdapter(getContext(), getChildFragmentManager())
        .page(R.string.main_bookmarks_title, MainBookmarksFragment::newInstance)
        .page(R.string.main_forums_title, MainForumsFragment::newInstance);

    final ViewPager pager = view.findViewById(R.id.main_pager);
    pager.setAdapter(adapter);
    final TabLayout tabs = view.findViewById(R.id.main_tabs);
    tabs.setupWithViewPager(pager);
  }

}