package com.test.filespath.feature.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.test.filespath.R;
import com.test.filespath.feature.base.BaseFragment;
import com.test.filespath.feature.main.reader.FileModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class MainFragment extends BaseFragment<MainViewModel> implements FilesAdapter.OnItemClickListener {

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    private final int PERMISSION_REQUEST_CODE = 1234;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @BindView(R.id.rv_file_names)
    RecyclerView rvFileNames;

    @BindView(R.id.toolbar_main)
    Toolbar toolbarMain;

    @BindView(R.id.tv_number_of_matches)
    TextView tvNumberOfMatches;

    @BindView(R.id.tv_save_result)
    TextView tvSaveResult;

    @BindView(R.id.g_search_options)
    Group gSearchOptions;

    private final FilesAdapter adapter = new FilesAdapter(this);

    private MainNavigation mainNavigation;

    public void setMainNavigation(MainNavigation mainNavigation) {
        this.mainNavigation = mainNavigation;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public MainViewModel getViewModel() {
        return new ViewModelProvider(this, viewModelFactory).get(MainViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        observeViewMode();
        setListeners();
        loadData();
    }

    private void loadData() {
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

    private void setListeners() {
        tvSaveResult.setOnClickListener(v -> {

        });
    }

    private void observeViewMode() {
        viewModel.allFiles.observe(getViewLifecycleOwner(), fileModels -> {
            if (fileModels != null)
                adapter.updateList(fileModels);
        });

        viewModel.toDetail.observe(getViewLifecycleOwner(), fileModel -> {
            if (fileModel != null) {
                mainNavigation.toDetailScreen(fileModel);
            }
        });

        viewModel.searchResult.observe(getViewLifecycleOwner(), result -> {
            tvNumberOfMatches.setText("Matches: " + result);
        });

        viewModel.searchOptionsVisibility.observe(getViewLifecycleOwner(), visible -> {
            if (visible) {
                gSearchOptions.setVisibility(View.VISIBLE);
            } else {
                gSearchOptions.setVisibility(View.GONE);
            }
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

            searchView.setOnCloseListener(() -> {
                viewModel.onSearch("");
                return false;
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

    @Override
    public void OnItemCLick(FileModel fileModel) {
        viewModel.onItemClick(fileModel);
    }
}
