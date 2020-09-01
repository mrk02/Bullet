package com.mrk02.bullet.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.mrk02.bullet.R;
import com.mrk02.bullet.model.Forum;

import androidx.annotation.Nullable;

public class MainForumDialog extends BottomSheetDialogFragment {

  private final Forum forum;

  /**
   * @param forum
   */
  public MainForumDialog(Forum forum) {
    this.forum = forum;
  }

  /**
   * @param forum
   * @return
   */
  public static MainForumDialog newInstance(Forum forum) {
    return new MainForumDialog(forum);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.main_forum_dialog, container, false);

    final TextInputLayout url = view.findViewById(R.id.main_forum_dialog_url);

    return view;
  }

}