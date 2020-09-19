package com.mrk02.bullet.ui.forum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrk02.bullet.R;
import com.mrk02.bullet.repository.model.Forum;
import com.mrk02.bullet.service.model.Board;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

public class ForumFragment extends Fragment {

  private static final String KEY_FORUM = "ForumFragment_forum";
  private static final String KEY_URL = "ForumFragment_url";

  private ForumViewModel vm;

  @NonNull
  public static ForumFragment newInstance(@NonNull Forum forum, @NonNull String url) {
    final ForumFragment fragment = new ForumFragment();
    final Bundle bundle = new Bundle();
    bundle.putParcelable(KEY_FORUM, forum);
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

    final Forum forum = Objects.requireNonNull(requireArguments().getParcelable(KEY_FORUM));
    final String url = Objects.requireNonNull(requireArguments().getString(KEY_URL));

    vm.loadPage(forum.id, url);

    final Toolbar toolbar = view.findViewById(R.id.forum_toolbar);
    new MenuInflater(getContext()).inflate(R.menu.forum, toolbar.getMenu());

    final ForumAdapter listAdapter = new ForumAdapter()
        .factory(Board.class, R.layout.forum_item_board, HolderBoard::new);

    final RecyclerView list = view.findViewById(R.id.forum_list);
    list.setAdapter(listAdapter);

    vm.getPage().observe(getViewLifecycleOwner(), page -> {
      toolbar.setTitle(page.title());
      listAdapter.items(page.boards());
    });
  }

  /**
   *
   */
  private static final class HolderBoard extends ForumAdapter.Holder<Board> {

    final TextView title;

    public HolderBoard(@NonNull View itemView) {
      super(itemView);
      title = (TextView) itemView;
    }

    @Override
    public void bind(Board item) {
      title.setText(item.title());
    }
  }

}