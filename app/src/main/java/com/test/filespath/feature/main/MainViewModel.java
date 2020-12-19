package com.test.filespath.feature.main;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.test.filespath.feature.base.BaseViewModel;
import com.test.filespath.rx.SchedulerProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class MainViewModel extends BaseViewModel {

    private MutableLiveData<List<FileModel>> _allFiles = new MutableLiveData<List<FileModel>>();
    protected LiveData<List<FileModel>> allFiles = _allFiles;

    private final List<FileModel> filesList = new ArrayList<FileModel>();


    @Inject
    public MainViewModel(SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(schedulerProvider, compositeDisposable);
    }

    public void readAllFiles(File externalStorageDirectory) {
        readFiles(externalStorageDirectory);
    }

    private void readFiles(File directory) {
        compositeDisposable.add(Observable.fromPublisher(new FileReader(directory))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(filesList::add,
                        throwable -> Log.e("listExternalStorage", "<=======Completed=====>"),
                        () -> _allFiles.postValue(filesList)
                ));
    }
}