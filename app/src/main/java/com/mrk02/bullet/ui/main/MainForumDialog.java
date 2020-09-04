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

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class MainForumDialog extends BottomSheetDialogFragment {

  private final Forum forum;

  private MainViewModel vm;

  /**
   * @param forum
   */
  public MainForumDialog(Forum forum) {
    this.forum = forum;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (vm == null) {
      vm = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    }

    final View view = inflater.inflate(R.layout.main_forum_dialog, container, false);

    final TextInputEditText name = view.findViewById(R.id.main_forum_dialog_name);
    name.setText(forum.name);
    final TextInputEditText url = view.findViewById(R.id.main_forum_dialog_url);
    url.setText(forum.url);

    final Button delete = view.findViewById(R.id.main_forum_dialog_delete);
    delete.setOnClickListener(v -> {
      vm.deleteForum(forum);
      dismiss();
    });

    final Button ok = view.findViewById(R.id.main_forum_dialog_ok);
    ok.setOnClickListener(v -> {
      final Forum newForum = new Forum();
      newForum.id = forum.id;
      newForum.name = Objects.requireNonNull(name.getText()).toString();
      newForum.url = Objects.requireNonNull(url.getText()).toString();
      vm.updateForum(newForum);
      dismiss();
    });

    return view;
  }

}