package com.test.filespath.feature.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.test.filespath.R;
import com.test.filespath.feature.base.BaseFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class MainFragment extends BaseFragment<MainViewModel> {

    private final int PERMISSION_REQUEST_CODE = 1234;

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

    @BindView(R.id.toolbar_main)
    Toolbar toolbarMain;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        observeViewMode();
        if (isPermissionNeeded()) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            listExternalStorage();
        }
    }

    private void initView() {
        rvFileNames.setAdapter(adapter);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbarMain);
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
        if (requestCode == this.PERMISSION_REQUEST_CODE) {
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

            List<File> directoryList = new ArrayList<File>();
            directoryList.add(Environment.getExternalStorageDirectory());
            directoryList.add(Environment.getRootDirectory());
            directoryList.add(Environment.getDataDirectory());
            directoryList.add(Environment.getDownloadCacheDirectory());
            viewModel.readAllFiles(directoryList);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.item_search).getActionView();

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    viewModel.onSearch(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_search:
                viewModel.onSortExtension();
                break;
            case R.id.item_alphabetically:
                viewModel.onSortAlphabetically();
                break;
            case R.id.item_chronologically:
                viewModel.onSortChronologically();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
