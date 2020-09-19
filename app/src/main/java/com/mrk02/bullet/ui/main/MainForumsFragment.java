package com.mrk02.bullet.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mrk02.bullet.R;
import com.mrk02.bullet.service.Config;
import com.mrk02.bullet.ui.forum.ForumFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

/**
 *
 */
public class MainForumsFragment extends Fragment {

  private MainForumsViewModel vm;

  public static MainForumsFragment newInstance() {
    MainForumsFragment fragment = new MainForumsFragment();
    Bundle bundle = new Bundle();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.main_forums_fragment, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (vm == null) {
      vm = new ViewModelProvider(requireActivity()).get(MainForumsViewModel.class);
    }

    final MainForumsAdapter adapter = new MainForumsAdapter(
        forum -> requireActivity().getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.fragment_open_enter, R.anim.fragment_open_exit, R.anim.fragment_close_enter, R.anim.fragment_close_exit)
            .replace(R.id.container, ForumFragment.newInstance(forum.id, Config.MAIN, forum.url))
            .addToBackStack(null)
            .commit(),
        forum -> MainForumDialogFragment.newInstance(forum).show(getChildFragmentManager(), null));

    vm.findAllForums().observe(requireActivity(), adapter::submitList);

    final RecyclerView list = view.findViewById(R.id.main_forums_list);
    list.setAdapter(adapter);

    final FloatingActionButton add = view.findViewById(R.id.main_forums_add);
    add.setOnClickListener(v -> MainForumDialogFragment.newInstance(null).show(getChildFragmentManager(), null));
  }

}