package com.test.filespath.feature.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.test.filespath.feature.base.BaseViewModel;
import com.test.filespath.feature.main.reader.FileModel;
import com.test.filespath.rx.SchedulerProvider;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class DetailViewModel extends BaseViewModel {

    private final MutableLiveData<FileModel> _data = new MutableLiveData<FileModel>();
    protected LiveData<FileModel> data = _data;

    private FileModel fileModel;

    @Inject
    public DetailViewModel(SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(schedulerProvider, compositeDisposable);
    }

    public void onViewModelReady(FileModel fileModel) {
        this.fileModel = fileModel;
        _data.postValue(fileModel);
    }
}
