package com.mrk02.bullet.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.mrk02.bullet.R;
import com.mrk02.bullet.model.Forum;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class MainForumDialog extends BottomSheetDialogFragment {

  private static final String KEY_FORUM = "MainForumDialog_forum";

  private Forum forum;
  private MainViewModel vm;

  /**
   * @param forum The forum to show in this dialog.
   * @return A new instance of this fragment.
   */
  public static MainForumDialog newInstance(Forum forum) {
    final MainForumDialog fragment = new MainForumDialog();
    final Bundle bundle = new Bundle();
    bundle.putParcelable(KEY_FORUM, forum);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (vm == null) {
      vm = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    }
    if (forum == null) {
      forum = getArguments().getParcelable(KEY_FORUM);
    }

    final View view = inflater.inflate(R.layout.main_forum_dialog, container, false);

    final TextInputEditText name = view.findViewById(R.id.main_forum_dialog_name);
    name.setText(forum.name);
    final TextInputEditText icon = view.findViewById(R.id.main_forum_dialog_icon);
    icon.setText(forum.icon);

    final Button delete = view.findViewById(R.id.main_forum_dialog_delete);
    delete.setOnClickListener(v -> {
      vm.deleteForum(forum);
      dismiss();
    });

    final Button ok = view.findViewById(R.id.main_forum_dialog_ok);
    ok.setOnClickListener(v -> {
      final Forum newForum = new Forum();
      newForum.id = forum.id;
      newForum.url = forum.url;
      newForum.config = forum.config;
      newForum.name = Objects.requireNonNull(name.getText()).toString();
      newForum.icon = Objects.requireNonNull(icon.getText()).toString();
      vm.updateForum(newForum);
      dismiss();
    });

    return view;
  }

}