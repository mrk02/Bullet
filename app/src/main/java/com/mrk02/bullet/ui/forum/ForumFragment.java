package com.mrk02.bullet.ui.forum;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.mrk02.bullet.R;
import com.mrk02.bullet.service.model.Board;
import com.mrk02.bullet.service.model.Breadcrumb;
import com.mrk02.bullet.service.model.Link;
import com.mrk02.bullet.service.model.Page;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ForumFragment extends Fragment implements ViewModelProvider.Factory {

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
      vm = new ViewModelProvider(this, this).get(ForumViewModel.class);
    }

    final Toolbar toolbar = view.findViewById(R.id.forum_toolbar);
    final Menu menu = toolbar.getMenu();
    new MenuInflater(getContext()).inflate(R.menu.forum, menu);
    toolbar.setOnMenuItemClickListener(item -> {
      switch (item.getItemId()) {
        case R.id.forum_menu_bookmark:
          if (item.isChecked()) {
            vm.deleteBookmark();
          } else {
            vm.insertBookmark();
          }
          return true;
        case R.id.forum_menu_browser:
          startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(vm.getUrl())));
          return true;
        case R.id.forum_menu_share:
          final Intent intent = new Intent();
          intent.setAction(Intent.ACTION_SEND);
          intent.putExtra(Intent.EXTRA_TEXT, vm.getUrl());
          intent.setType("text/plain");
          startActivity(Intent.createChooser(intent, null));
          return true;
      }
      return false;
    });
    toolbar.setNavigationOnClickListener(v -> {
      final Page page = vm.getPage().getValue();
      if (page != null && !page.breadcrumbs().isEmpty()) {
        openLink(page.breadcrumbs().get(page.breadcrumbs().size() - 1).link());
      } else {
        requireActivity().getSupportFragmentManager().popBackStackImmediate();
      }
    });

    vm.getBookmark().observe(getViewLifecycleOwner(), bookmark -> {
      menu.findItem(R.id.forum_menu_bookmark).setChecked(bookmark != null);
    });

    final SwipeRefreshLayout refresh = view.findViewById(R.id.forum_refresh);
    refresh.setOnRefreshListener(vm::loadPage);

    final ForumAdapter listAdapter = new ForumAdapter()
        .factory(Board.class, R.layout.forum_item_board, HolderBoard::new);
    final RecyclerView list = view.findViewById(R.id.forum_list);
    list.setAdapter(listAdapter);
    list.setNestedScrollingEnabled(false);

    final BreadcrumbAdapter breadcrumbAdapter = new BreadcrumbAdapter();
    final RecyclerView breadcrumbs = view.findViewById(R.id.forum_breadcrumbs);
    breadcrumbs.setAdapter(breadcrumbAdapter);

    final AppBarLayout appbar = view.findViewById(R.id.forum_appbar);
    appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
      if (list.isNestedScrollingEnabled()) {
        if (verticalOffset == -appBarLayout.getTotalScrollRange()) {
          list.setNestedScrollingEnabled(false);
        }
      } else {
        if (verticalOffset == 0) {
          list.setNestedScrollingEnabled(true);
        }
      }
    });

    vm.getPage().observe(getViewLifecycleOwner(), page -> {
      if (page == null) {
        toolbar.setTitle("");
        breadcrumbAdapter.items(Collections.emptyList());
        listAdapter.items(Collections.emptyList());
      } else {
        toolbar.setTitle(page.title());
        breadcrumbAdapter.items(page.breadcrumbs());
        listAdapter.items(page.boards());
        refresh.setRefreshing(false);
        appbar.setExpanded(false, false);
      }
    });
  }

  @NonNull
  @Override
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    final Bundle args = requireArguments();
    final int forumId = args.getInt(KEY_FORUM_ID);
    final String type = Objects.requireNonNull(args.getString(KEY_TYPE));
    final String url = Objects.requireNonNull(args.getString(KEY_URL));
    //noinspection unchecked
    return (T) new ForumViewModel(requireActivity().getApplication(), forumId, type, url);
  }

  /**
   * @param link The link to open.
   */
  private void openLink(Link link) {
    requireActivity().getSupportFragmentManager().beginTransaction()
        .setCustomAnimations(R.anim.fragment_open_enter, R.anim.fragment_open_exit, R.anim.fragment_close_enter, R.anim.fragment_close_exit)
        .replace(R.id.container, ForumFragment.newInstance(vm.getForumId(), link.type(), link.url()))
        .addToBackStack(null)
        .commit();
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
      title.setText(item.name());
      itemView.setOnClickListener(v -> openLink(item.link()));
    }
  }

  /**
   *
   */
  private final class HolderBreadcrumb extends RecyclerView.ViewHolder {

    private final TextView text;

    public HolderBreadcrumb(@NonNull View itemView) {
      super(itemView);
      text = (TextView) itemView;
    }
  }

  /**
   *
   */
  private final class BreadcrumbAdapter extends RecyclerView.Adapter<HolderBreadcrumb> {

    private List<Breadcrumb> items = Collections.emptyList();

    @NonNull
    @Override
    public HolderBreadcrumb onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new HolderBreadcrumb(LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_item_breadcrumb, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HolderBreadcrumb holder, int position) {
      final Breadcrumb breadcrumb = items.get(position);
      holder.text.setText(breadcrumb.name());
      holder.itemView.setOnClickListener(v -> openLink(breadcrumb.link()));
    }

    @Override
    public int getItemCount() {
      return items.size();
    }

    public void items(List<Breadcrumb> items) {
      this.items = items;
      notifyDataSetChanged();
    }
  }

}