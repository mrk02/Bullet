package com.mrk02.bullet.ui.forum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrk02.bullet.R;
import com.mrk02.bullet.service.model.Board;

import java.util.Collections;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ForumFragment extends Fragment {

  private static final String KEY_FORUM_ID = "ForumFragment_forum_id";
  private static final String KEY_TYPE = "ForumFragment_type";
  private static final String KEY_URL = "ForumFragment_url";

  private ForumViewModel vm;

  @NonNull
  public static ForumFragment newInstance(int forumId, @NonNull String type, @NonNull String url) {
    final ForumFragment fragment = new ForumFragment();
    final Bundle bundle = new Bundle();
    bundle.putInt(KEY_FORUM_ID, forumId);
    bundle.putString(KEY_TYPE, type);
    bundle.putString(KEY_URL, url);
    fragment.setArguments(bundle);
    return fragment;
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
      vm = new ViewModelProvider(this).get(ForumViewModel.class);
    }

    final Bundle args = requireArguments();
    final int forumId = args.getInt(KEY_FORUM_ID);
    final String type = Objects.requireNonNull(args.getString(KEY_TYPE));
    final String url = Objects.requireNonNull(requireArguments().getString(KEY_URL));

    vm.loadPage(forumId, type, url, false);

    final Toolbar toolbar = view.findViewById(R.id.forum_toolbar);
    new MenuInflater(getContext()).inflate(R.menu.forum, toolbar.getMenu());

    final SwipeRefreshLayout refresh = view.findViewById(R.id.forum_refresh);
    refresh.setOnRefreshListener(() -> vm.loadPage(forumId, type, url, true));

    final ForumAdapter listAdapter = new ForumAdapter()
        .factory(Board.class, R.layout.forum_item_board, HolderBoard::new);

    final RecyclerView list = view.findViewById(R.id.forum_list);
    list.setAdapter(listAdapter);

    vm.getPage().observe(getViewLifecycleOwner(), page -> {
      if (page == null) {
        toolbar.setTitle("");
        listAdapter.items(Collections.emptyList());
      } else {
        toolbar.setTitle(page.title());
        listAdapter.items(page.boards());
        refresh.setRefreshing(false);
      }
    });
  }

  /**
   *
   */
  private final class HolderBoard extends ForumAdapter.Holder<Board> {

    final TextView title;

    public HolderBoard(@NonNull View itemView) {
      super(itemView);
      title = (TextView) itemView;
    }

    @Override
    public void bind(Board item) {
      title.setText(item.title());
      title.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction()
          .setCustomAnimations(R.anim.fragment_open_enter, R.anim.fragment_open_exit, R.anim.fragment_close_enter, R.anim.fragment_close_exit)
          .replace(R.id.container, ForumFragment.newInstance(requireArguments().getInt(KEY_FORUM_ID), item.link().type(), item.link().url()))
          .addToBackStack(null)
          .commit());
    }
  }

}