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
import io.reactivex.disposables.CompositeDisposable;


public class MainViewModel extends BaseViewModel {

    private MutableLiveData<List<FileModel>> _allFiles = new MutableLiveData<List<FileModel>>();
    protected LiveData<List<FileModel>> allFiles = _allFiles;

    private final List<FileModel> filesList = new ArrayList<FileModel>();


    @Inject
    public MainViewModel(SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(schedulerProvider, compositeDisposable);
    }

    public void readAllFiles(List<File> directoryList) {
        readDirectoryList(directoryList);
    }

    private void readDirectoryList(List<File> directoryList) {
        readFiles(directoryList);
    }

    private void readFiles(List<File> files) {
        compositeDisposable.add(Observable.fromIterable(files)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .concatMap(file -> Observable.fromPublisher(new FileReader(file))
                ).subscribe(filesList::add,
                        throwable -> Log.e("listExternalStorage", "Error Reading"),
                        () -> _allFiles.postValue(filesList))
        );
    }

    public void onSortAlphabetically() {
        if (_allFiles.getValue() != null) {
            clearList();
            compositeDisposable.add(Observable.fromIterable(_allFiles.getValue())
                    .toSortedList(FileModel::compareTo)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(
                            list -> _allFiles.postValue(list),
                            throwable -> Log.e("listExternalStorage", "Error Sorting Alphabetically")
                    ));

        }
    }

    public void onSortChronologically() {
        if (_allFiles.getValue() != null) {
            clearList();
            compositeDisposable.add(Observable.fromIterable(_allFiles.getValue())
                    .toSortedList((f1, f2) -> f1.lastModified.compareTo(f2.lastModified))
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(
                            list -> _allFiles.postValue(list),
                            throwable -> Log.e("listExternalStorage", "Error Sorting Alphabetically")
                    ));

        }
    }

    public void onSortExtension() {
        Log.e("listExternalStorage", "sortExtension");
    }

    public void onSearch(String query) {
        Log.e("listExternalStorage", "Query = " + query);
        clearList();
        compositeDisposable.add(Observable.fromIterable(_allFiles.getValue())
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .flatMap(Observable::just)
                .filter(file -> file.name.contains(query))
                .toList()
                .subscribe(
                        list -> _allFiles.postValue(list),
                        throwable -> Log.e("listExternalStorage", "Error Sorting Alphabetically")
                ));
    }

    private void clearList() {
        _allFiles.postValue(new ArrayList<FileModel>());
    }
}
