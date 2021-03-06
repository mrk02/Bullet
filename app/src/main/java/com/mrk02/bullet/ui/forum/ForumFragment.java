package com.mrk02.bullet.ui.forum;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.mrk02.bullet.R;
import com.mrk02.bullet.service.model.Board;
import com.mrk02.bullet.service.model.Breadcrumb;
import com.mrk02.bullet.service.model.Link;
import com.mrk02.bullet.service.model.Page;
import com.mrk02.bullet.service.model.Thread;
import com.mrk02.bullet.ui.forum.ForumViewModel.Section;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
        requireActivity().getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
      }
    });

    vm.getBookmark().observe(getViewLifecycleOwner(), bookmark -> {
      toolbar.getMenu().findItem(R.id.forum_menu_bookmark).setChecked(bookmark != null);
    });

    final SwipeRefreshLayout refresh = view.findViewById(R.id.forum_refresh);
    refresh.setOnRefreshListener(vm::loadPage);

    final ForumAdapter listAdapter = new ForumAdapter()
        .factory(Board.class, R.layout.forum_item_board, HolderBoard::new)
        .factory(com.mrk02.bullet.service.model.Thread.class, R.layout.forum_item_thread, HolderThread::new)
        .factory(Section.class, R.layout.forum_item_section, HolderSection::new);
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
      } else {
        toolbar.setTitle(page.title());
        breadcrumbAdapter.items(page.breadcrumbs());
        refresh.setRefreshing(false);
        appbar.setExpanded(false, false);
      }

      vm.getItems().observe(getViewLifecycleOwner(), listAdapter::submitList);
    });

    vm.getException().observe(getViewLifecycleOwner(), exception -> {
      if (exception != null) {
        refresh.setRefreshing(false);
        final String message = exception.getLocalizedMessage() != null ? exception.getLocalizedMessage() : exception.getClass().getName();
        final String stacktrace;

        try (StringWriter stringWriter = new StringWriter();
             PrintWriter printWriter = new PrintWriter(stringWriter)) {
          exception.printStackTrace(printWriter);
          stacktrace = stringWriter.toString();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction(R.string.forum_dialog_details, v -> new AlertDialog.Builder(requireContext())
                .setTitle(exception.getClass().getCanonicalName())
                .setMessage(stacktrace)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .create()
                .show())
            .show();
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
    final String type = link.type();
    final String url = link.url();
    if (type == null) {
      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    } else {
      requireActivity().getSupportFragmentManager().beginTransaction()
          .setCustomAnimations(R.anim.fragment_open_enter, R.anim.fragment_open_exit, R.anim.fragment_close_enter, R.anim.fragment_close_exit)
          .replace(R.id.container, ForumFragment.newInstance(vm.getForumId(), type, url))
          .addToBackStack(null)
          .commit();
    }
  }

  /**
   *
   */
  private final class HolderSection extends ForumAdapter.Holder<Section> {

    public HolderSection(@NonNull View itemView) {
      super(itemView);
    }

    @Override
    public void bind(Section item) {
      final CheckBox checkBox = (CheckBox) itemView;
      checkBox.setText(item.text);
      checkBox.setChecked(item.expanded);
      checkBox.setOnCheckedChangeListener((v, checked) -> {
        item.expanded = checked;
        vm.buildItems();
      });
    }

  }

  /**
   *
   */
  private final class HolderBoard extends ForumAdapter.Holder<Board> {

    public HolderBoard(@NonNull View itemView) {
      super(itemView);
    }

    @Override
    public void bind(Board item) {
      ((TextView) itemView).setText(item.name());
      itemView.setOnClickListener(v -> openLink(item.link()));
    }

  }

  /**
   *
   */
  private final class HolderThread extends ForumAdapter.Holder<com.mrk02.bullet.service.model.Thread> {

    private final TextView name;
    private final ImageView sticky;
    private final TextView user;
    private final TextView timestamp;
    private final ImageButton latest;

    public HolderThread(@NonNull View itemView) {
      super(itemView);
      name = itemView.findViewById(R.id.forum_item_thread_name);
      sticky = itemView.findViewById(R.id.forum_item_thread_info_sticky);
      user = itemView.findViewById(R.id.forum_item_thread_info_user);
      timestamp = itemView.findViewById(R.id.forum_item_thread_info_timestamp);
      latest = itemView.findViewById(R.id.forum_item_thread_latest);
    }

    @Override
    public void bind(Thread item) {
      name.setText(item.name());
      sticky.setVisibility(item.sticky() ? View.VISIBLE : View.GONE);
      user.setText(item.user());
      timestamp.setText(vm.getDateFormat().format(new Date(item.timestamp() * 1000)));
      itemView.setOnClickListener(v -> openLink(item.link()));
      latest.setOnClickListener(v -> openLink(item.latest()));
    }
  }

  /**
   *
   */
  private final class HolderBreadcrumb extends RecyclerView.ViewHolder {

    public HolderBreadcrumb(@NonNull View itemView) {
      super(itemView);
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
      ((TextView) holder.itemView).setText(breadcrumb.name());
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