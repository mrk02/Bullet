package com.mrk02.bullet.ui.forum;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.recyclerview.widget.RecyclerView;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.Holder<?>> {

  private final Map<Class<?>, Integer> viewTypes = new IdentityHashMap<>();
  private final SparseArray<Function<View, Holder<?>>> factories = new SparseArray<>();
  private List<?> items = Collections.emptyList();

  @NonNull
  @Override
  public Holder<?> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    final View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
    return factories.get(viewType).apply(itemView);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onBindViewHolder(@NonNull Holder holder, int position) {
    holder.bind(items.get(position));
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public int getItemViewType(int position) {
    final Class<?> clazz = items.get(position).getClass();
    return viewTypes.get(clazz);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public <V> ForumAdapter factory(Class<V> clazz, int viewType, Function<View, Holder<V>> factory) {
    viewTypes.put(clazz, viewType);
    factories.put(viewType, (Function) factory);
    return this;
  }

  public void items(List<?> items) {
    this.items = items;
    super.notifyDataSetChanged();
  }

  public static abstract class Holder<T> extends RecyclerView.ViewHolder {

    public Holder(@NonNull View itemView) {
      super(itemView);
    }

    public abstract void bind(T item);

  }

}
