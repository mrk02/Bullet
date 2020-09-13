package com.mrk02.bullet.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mrk02.bullet.R;
import com.mrk02.bullet.model.Forum;
import com.mrk02.bullet.ui.Util;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class MainForumDialogFragment extends BottomSheetDialogFragment {

  private static final String TAG = "MainForumDialog";
  private static final String KEY_FORUM = "MainForumDialog_forum";
  private static final int REQUEST_FILE = 1;

  private MainForumDialogViewModel vm;

  private TextInputEditText url;
  private TextInputEditText config;
  private TextInputEditText name;
  private TextInputEditText icon;

  /**
   * @param forum The forum to show in this dialog.
   * @return A new instance of this fragment.
   */
  public static MainForumDialogFragment newInstance(Forum forum) {
    final MainForumDialogFragment fragment = new MainForumDialogFragment();
    final Bundle bundle = new Bundle();
    bundle.putParcelable(KEY_FORUM, forum);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (vm == null) {
      vm = new ViewModelProvider(this).get(MainForumDialogViewModel.class);
    }

    final View view = inflater.inflate(R.layout.main_forum_dialog, container, false);
    final TextView title = view.findViewById(R.id.main_forum_dialog_title);
    final Button delete = view.findViewById(R.id.main_forum_dialog_delete);
    final Button ok = view.findViewById(R.id.main_forum_dialog_ok);
    url = view.findViewById(R.id.main_forum_dialog_url);
    config = view.findViewById(R.id.main_forum_dialog_config);
    name = view.findViewById(R.id.main_forum_dialog_name);
    icon = view.findViewById(R.id.main_forum_dialog_icon);

    final Forum forum = requireArguments().getParcelable(KEY_FORUM);
    if (forum != null) {
      title.setText(R.string.main_forum_dialog_title_edit);
      delete.setOnClickListener(v -> {
        vm.deleteForum(forum);
        dismiss();
      });

      url.setText(forum.url);
      config.setText(forum.config);
      name.setText(forum.name);
      icon.setText(forum.icon);
    } else {
      title.setText(R.string.main_forum_dialog_title_add);
      delete.setVisibility(View.GONE);
    }

    final TextInputLayout configLayout = view.findViewById(R.id.main_forum_dialog_config_layout);
    configLayout.setEndIconOnClickListener(v -> startActivityForResult(
        Intent.createChooser(
            new Intent()
                .setType("application/zip")
                .setAction(Intent.ACTION_GET_CONTENT),
            getString(R.string.main_forums_add_title)),
        REQUEST_FILE));

    ok.setOnClickListener(v -> {
      final Forum.Builder builder = forum != null ? forum.toBuilder() : Forum.builder();
      vm.insertForum(builder
          .setUrl(Objects.requireNonNull(url.getText()).toString())
          .setConfig(Objects.requireNonNull(config.getText()).toString())
          .setName(Objects.requireNonNull(name.getText()).toString())
          .setIcon(Objects.requireNonNull(icon.getText()).toString())
          .build());
      dismiss();
    });

    return view;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == REQUEST_FILE) {
      if (resultCode == Activity.RESULT_OK) {
        try {
          final Uri configUri = Objects.requireNonNull(Objects.requireNonNull(data).getData());
          final String url = Objects.requireNonNull(this.url.getText()).toString();
          Util.observeOnce(vm.loadPage(configUri, url), page -> {
            config.setText(configUri.toString());
            name.setText(page.getTitle());
            icon.setText(page.getIcon());
          });
        } catch (Exception e) {
          Log.e(TAG, "unable to load file", e);
          Toast.makeText(getContext(), R.string.main_forums_add_error, Toast.LENGTH_SHORT).show();
        }
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

}