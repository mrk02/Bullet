package com.mrk02.bullet.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mrk02.bullet.R;
import com.mrk02.bullet.model.Forum;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.core.util.ObjectsCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

/**
 *
 */
public class MainForumsAdapter extends ListAdapter<Forum, MainForumsAdapter.ViewHolder> {

  private final Consumer<Forum> onClick;
  private final Consumer<Forum> onLongClick;

  protected MainForumsAdapter(Consumer<Forum> onClick, Consumer<Forum> onLongClick) {
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

    this.onClick = onClick;
    this.onLongClick = onLongClick;
  }

  @NonNull
  @Override
  public MainForumsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_forums_item, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull MainForumsAdapter.ViewHolder holder, int position) {
    final Forum forum = getItem(position);

    holder.title.setText(forum.name);
    holder.subtitle.setText(forum.url);
    Glide.with(holder.itemView.getContext())
        .load(forum.icon)
        .into(holder.icon);

    holder.itemView.setOnClickListener(v -> onClick.accept(forum));
    holder.itemView.setOnLongClickListener(v -> {
      onLongClick.accept(forum);
      return true;
    });
  }

  /**
   *
   */
  public static final class ViewHolder extends RecyclerView.ViewHolder {

    private final ImageView icon;
    private final TextView title;
    private final TextView subtitle;

    /**
     * @param itemView The view to hold.
     */
    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      icon = itemView.findViewById(R.id.main_forums_item_icon);
      title = itemView.findViewById(R.id.main_forums_item_title);
      subtitle = itemView.findViewById(R.id.main_forums_item_subtitle);
    }

  }


}
