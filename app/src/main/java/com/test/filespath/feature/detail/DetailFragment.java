package com.test.filespath.feature.detail;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.test.filespath.R;
import com.test.filespath.feature.base.BaseFragment;
import com.test.filespath.feature.main.reader.FileModel;

import javax.inject.Inject;

import butterknife.BindView;

public class DetailFragment extends BaseFragment<DetailViewModel> {

    private static final String PARAM_FILE_MODEL = "param_file_model";

    public static DetailFragment newInstance(FileModel fileModel) {
        DetailFragment fragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM_FILE_MODEL, fileModel);
        fragment.setArguments(bundle);

        return fragment;
    }

    private FileModel fileModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @BindView(R.id.img_file)
    ImageView imgFile;

    @BindView(R.id.tv_file_size)
    TextView tvFileSize;

    @BindView(R.id.tv_file_name)
    TextView tvFileName;

    @BindView(R.id.tv_last_modified_date)
    TextView tvLastModifiedDate;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_detail;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fileModel = (FileModel) getArguments().getSerializable(PARAM_FILE_MODEL);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViewModel();
    }

    private void setupViewModel() {
        viewModel.onViewModelReady(fileModel);
        viewModel.data.observe(getViewLifecycleOwner(), model -> {
            try {
                tvFileName.setText(model.path);
                tvFileSize.setText("Size: " + model.getSizeInMb(requireContext()));
                tvLastModifiedDate.setText("Last Modified: " + model.getLastModifiedDate());
                imgFile.setImageURI(model.getFileUri());
            } catch (Exception e) {
                Log.e("Exception", "setImageURI", e);
            }
        });
    }

    @Override
    public DetailViewModel getViewModel() {
        return new ViewModelProvider(this, viewModelFactory).get(DetailViewModel.class);
    }
}
