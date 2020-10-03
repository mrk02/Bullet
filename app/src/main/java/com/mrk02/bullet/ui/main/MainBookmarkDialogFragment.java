package com.mrk02.bullet.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.mrk02.bullet.R;
import com.mrk02.bullet.repository.model.Bookmark;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainBookmarkDialogFragment extends BottomSheetDialogFragment implements ViewModelProvider.Factory {

  private static final String TAG = "MainBookmarkDialog";
  private static final String KEY_BOOKMARK = "MainBookmarkDialog_bookmark";

  private MainBookmarkDialogViewModel vm;

  /**
   * @param bookmark The bookmark to show in this dialog.
   * @return A new instance of this fragment.
   */
  public static MainBookmarkDialogFragment newInstance(Bookmark bookmark) {
    final MainBookmarkDialogFragment fragment = new MainBookmarkDialogFragment();
    final Bundle bundle = new Bundle();
    bundle.putParcelable(KEY_BOOKMARK, bookmark);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (vm == null) {
      vm = new ViewModelProvider(this, this).get(MainBookmarkDialogViewModel.class);
    }

    final View view = inflater.inflate(R.layout.main_bookmark_dialog, container, false);
    final TextInputEditText name = view.findViewById(R.id.main_bookmark_dialog_name);
    final Button delete = view.findViewById(R.id.main_bookmark_dialog_delete);
    final Button ok = view.findViewById(R.id.main_bookmark_dialog_ok);

    name.setText(vm.getBookmark().name);

    delete.setOnClickListener(v -> {
      vm.deleteBookmark();
      dismiss();
    });

    ok.setOnClickListener(v -> {
      vm.insertBookmark(vm.getBookmark().toBuilder()
          .setName(Objects.requireNonNull(name.getText()).toString())
          .build());
      dismiss();
    });

    return view;
  }

  @NonNull
  @Override
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    final Bookmark bookmark = Objects.requireNonNull(requireArguments().getParcelable(KEY_BOOKMARK));
    //noinspection unchecked
    return (T) new MainBookmarkDialogViewModel(requireActivity().getApplication(), bookmark);
  }
}