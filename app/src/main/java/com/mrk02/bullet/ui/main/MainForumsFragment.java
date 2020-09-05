package com.mrk02.bullet.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mrk02.bullet.R;
import com.mrk02.bullet.ui.forum.ForumFragment;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

/**
 *
 */
public class MainForumsFragment extends Fragment {

  private static final String TAG = "MainForumsFragment";

  private static final int REQUEST_FILE = 1234;

  private MainViewModel vm;

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
    final FragmentActivity activity = Objects.requireNonNull(getActivity());
    if (vm == null) {
      vm = new ViewModelProvider(activity).get(MainViewModel.class);
    }

    final MainForumsAdapter adapter = new MainForumsAdapter(
        forum -> activity.getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.fragment_open_enter, R.anim.fragment_open_exit, R.anim.fragment_close_enter, R.anim.fragment_close_exit)
            .replace(R.id.container, ForumFragment.newInstance())
            .addToBackStack(null)
            .commit(),
        forum -> MainForumDialog.newInstance(forum).show(getChildFragmentManager(), "main-forum-dialog-" + forum.id));

    vm.findAllForums().observe(activity, adapter::submitList);

    final RecyclerView list = view.findViewById(R.id.main_forums_list);
    list.setAdapter(adapter);

    final FloatingActionButton add = view.findViewById(R.id.main_forums_add);
    add.setOnClickListener(v -> startActivityForResult(
        Intent.createChooser(
            new Intent()
                .setType("application/zip")
                .setAction(Intent.ACTION_GET_CONTENT),
            getString(R.string.main_forums_add_title)),
        REQUEST_FILE));
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == REQUEST_FILE) {
      if (resultCode == Activity.RESULT_OK) {
        final Context context = Objects.requireNonNull(getContext());
        try {
          final Uri uri = Objects.requireNonNull(Objects.requireNonNull(data).getData());
          vm.insertForum(uri);
        } catch (Exception e) {
          Log.e(TAG, "unable to load file", e);
          Toast.makeText(context, R.string.main_forums_add_error, Toast.LENGTH_SHORT).show();
        }
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

}