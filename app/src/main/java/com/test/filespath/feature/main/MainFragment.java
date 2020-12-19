package com.test.filespath.feature.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.test.filespath.R;
import com.test.filespath.feature.base.BaseFragment;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;

public class MainFragment extends BaseFragment<MainViewModel> {

    private final int requestCode = 1234;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private final FilesAdapter adapter = new FilesAdapter();


    @Override
    public int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public MainViewModel getViewModel() {
        return new ViewModelProvider(this, viewModelFactory).get(MainViewModel.class);
    }

    @BindView(R.id.rv_file_names)
    RecyclerView rvFileNames;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        observeViewMode();
        if (isPermissionNeeded()) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
        } else {
            listExternalStorage();
        }
    }

    private void initView() {
        rvFileNames.setAdapter(adapter);
    }

    private void observeViewMode() {
        viewModel.allFiles.observe(getViewLifecycleOwner(), fileModels -> {
            if (fileModels != null)
                adapter.updateList(fileModels);
        });
    }

    private boolean isPermissionNeeded() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == this.requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listExternalStorage();
            } else {
                Toast.makeText(requireContext(), R.string.permission_message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void listExternalStorage() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            viewModel.readAllFiles(externalStorageDirectory);
        }
    }
}
