package com.mrk02.bullet.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mrk02.bullet.R;
import com.mrk02.bullet.repository.model.Bookmark;
import com.mrk02.bullet.ui.forum.ForumFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.ObjectsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainBookmarksFragment extends Fragment {

  private MainBookmarksViewModel vm;

  public static MainBookmarksFragment newInstance() {
    MainBookmarksFragment fragment = new MainBookmarksFragment();
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
    return inflater.inflate(R.layout.main_bookmarks_fragment, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (vm == null) {
      vm = new ViewModelProvider(requireActivity()).get(MainBookmarksViewModel.class);
    }

    final MainBookmarksAdapter adapter = new MainBookmarksAdapter();
    vm.getBookmarks().observe(requireActivity(), adapter::submitList);

    final RecyclerView list = view.findViewById(R.id.main_bookmarks_list);
    list.setAdapter(adapter);
  }

  /**
   *
   */
  public static final class ViewHolder extends RecyclerView.ViewHolder {

    private final ImageView icon;
    private final TextView title;
    private final TextView subtitle;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      icon = itemView.findViewById(R.id.main_item_icon);
      title = itemView.findViewById(R.id.main_item_title);
      subtitle = itemView.findViewById(R.id.main_item_subtitle);
    }
  }

  /**
   *
   */
  public final class MainBookmarksAdapter extends ListAdapter<Bookmark.WithForum, MainBookmarksFragment.ViewHolder> {

    private MainBookmarksAdapter() {
      super(new DiffUtil.ItemCallback<Bookmark.WithForum>() {
        @Override
        public boolean areItemsTheSame(@NonNull Bookmark.WithForum oldItem, @NonNull Bookmark.WithForum newItem) {
          return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Bookmark.WithForum oldItem, @NonNull Bookmark.WithForum newItem) {
          return ObjectsCompat.equals(oldItem.forum.icon, newItem.forum.icon)
              && ObjectsCompat.equals(oldItem.name, newItem.name)
              && ObjectsCompat.equals(oldItem.forum.name, newItem.forum.name);
        }
      });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      final Bookmark.WithForum bookmark = getItem(position);

      holder.title.setText(bookmark.name);
      holder.subtitle.setText(bookmark.forum.name);
      Glide.with(holder.itemView.getContext())
          .load(bookmark.forum.icon)
          .into(holder.icon);

      holder.itemView.setOnClickListener(v -> requireActivity()
          .getSupportFragmentManager()
          .beginTransaction()
          .setCustomAnimations(R.anim.fragment_open_enter, R.anim.fragment_open_exit, R.anim.fragment_close_enter, R.anim.fragment_close_exit)
          .replace(R.id.container, ForumFragment.newInstance(bookmark.forumId, bookmark.type, bookmark.url))
          .addToBackStack(null)
          .commit());
      holder.itemView.setOnLongClickListener(v -> {
        MainBookmarkDialogFragment.newInstance(bookmark).show(getChildFragmentManager(), null);
        return true;
      });
    }
  }

}