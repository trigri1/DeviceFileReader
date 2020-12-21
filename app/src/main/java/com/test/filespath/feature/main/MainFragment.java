package com.test.filespath.feature.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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
import com.test.filespath.feature.main.extension.ExtensionsDialog;
import com.test.filespath.feature.main.reader.FileModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class MainFragment extends BaseFragment<MainViewModel> implements FilesAdapter.OnItemClickListener, OnExtensionSelectedListener {

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

    @BindView(R.id.pb_circular)
    ProgressBar pbCircular;

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
        tvSaveResult.setOnClickListener(v -> viewModel.onSaveSearchResults());
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

        viewModel.saveSearch.observe(getViewLifecycleOwner(), this::onSaveSearch);

        viewModel.loading.observe(getViewLifecycleOwner(), loading -> {
            if (loading) {
                pbCircular.setVisibility(View.VISIBLE);
            } else {
                pbCircular.setVisibility(View.GONE);
            }
        });

        viewModel.extensions.observe(getViewLifecycleOwner(), extensions -> {
            ExtensionsDialog dialog = new ExtensionsDialog(requireActivity());
            dialog.setData(extensions);
            dialog.setOnExtensionSelectedListener(this);
            dialog.show();
        });
    }

    //https://blog.cindypotvin.com/saving-data-to-a-file-in-your-android-application/
    public void onSaveSearch(SearchModel searchModel) {
        try {
            File testFile = new File(requireContext().getFilesDir(), searchModel.name);
            if (!testFile.exists())
                testFile.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, true));
            writer.write(searchModel.payload);
            writer.close();
            String msg = getString(R.string.search_saved_message, searchModel.name);
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("ReadWriteFile", "Unable to write to the file.", e);
        }
    }

    private boolean isPermissionNeeded() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == this.PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listExternalStorage();
            } else {
                Toast.makeText(requireContext(), R.string.permission_message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //    https://gist.github.com/lopspower/76421751b21594c69eb2
    private void listExternalStorage() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            File parentFile = requireContext().getFilesDir().getParentFile();
            List<File> directoryList = new ArrayList<File>();

            if (parentFile != null) {
                directoryList.add(parentFile);
            }
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
            case R.id.item_alphabetically:
                viewModel.onSortAlphabetically();
                break;
            case R.id.item_chronologically:
                viewModel.onSortChronologically();
                break;
            case R.id.item_extension:
                viewModel.onSortExtension();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnItemCLick(FileModel fileModel) {
        viewModel.onItemClick(fileModel);
    }

    @Override
    public void onExtensionSelected(String extension) {
        viewModel.onExtensionSelected(extension);
    }
}
