package com.mrk02.bullet.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mrk02.bullet.R;
import com.mrk02.bullet.repository.model.Forum;
import com.mrk02.bullet.service.Config;
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

    final MainForumsAdapter adapter = new MainForumsAdapter();
    vm.getForums().observe(requireActivity(), adapter::submitList);

    final RecyclerView list = view.findViewById(R.id.main_forums_list);
    list.setAdapter(adapter);

    final FloatingActionButton add = view.findViewById(R.id.main_forums_add);
    add.setOnClickListener(v -> MainForumDialogFragment.newInstance(null).show(getChildFragmentManager(), null));
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
  public final class MainForumsAdapter extends ListAdapter<Forum, MainForumsFragment.ViewHolder> {

    protected MainForumsAdapter() {
      super(new DiffUtil.ItemCallback<Forum>() {
        @Override
        public boolean areItemsTheSame(@NonNull Forum oldItem, @NonNull Forum newItem) {
          return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Forum oldItem, @NonNull Forum newItem) {
          return ObjectsCompat.equals(oldItem.icon, newItem.icon)
              && ObjectsCompat.equals(oldItem.name, newItem.name)
              && ObjectsCompat.equals(oldItem.url, newItem.url);
        }
      });
    }

    @NonNull
    @Override
    public MainForumsFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new MainForumsFragment.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainForumsFragment.ViewHolder holder, int position) {
      final Forum forum = getItem(position);

      holder.title.setText(forum.name);
      holder.subtitle.setText(forum.url);
      Glide.with(holder.itemView.getContext())
          .load(forum.icon)
          .into(holder.icon);

      holder.itemView.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction()
          .setCustomAnimations(R.anim.fragment_open_enter, R.anim.fragment_open_exit, R.anim.fragment_close_enter, R.anim.fragment_close_exit)
          .replace(R.id.container, ForumFragment.newInstance(forum.id, Config.MAIN, forum.url))
          .addToBackStack(null)
          .commit());
      holder.itemView.setOnLongClickListener(v -> {
        MainForumDialogFragment.newInstance(forum).show(getChildFragmentManager(), null);
        return true;
      });
    }
  }

}