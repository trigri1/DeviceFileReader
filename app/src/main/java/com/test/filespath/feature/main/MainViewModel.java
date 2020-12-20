package com.test.filespath.feature.main;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.test.filespath.feature.base.BaseViewModel;
import com.test.filespath.feature.main.reader.FileModel;
import com.test.filespath.feature.main.reader.FileReader;
import com.test.filespath.rx.SchedulerProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;


public class MainViewModel extends BaseViewModel {

    private final MutableLiveData<List<FileModel>> _allFiles = new MutableLiveData<List<FileModel>>();
    protected LiveData<List<FileModel>> allFiles = _allFiles;

    private final MutableLiveData<FileModel> _toDetail = new MutableLiveData<FileModel>();
    protected LiveData<FileModel> toDetail = _toDetail;

    private final MutableLiveData<Integer> _searchResult = new MutableLiveData<Integer>();
    protected LiveData<Integer> searchResult = _searchResult;

    private final MutableLiveData<Boolean> _searchOptionsVisibility = new MutableLiveData<Boolean>();
    protected LiveData<Boolean> searchOptionsVisibility = _searchOptionsVisibility;

    private final List<FileModel> filesList = new ArrayList<FileModel>();

    private final List<FileModel> originalList = new ArrayList<FileModel>();

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
                        () -> {
                            originalList.addAll(filesList);
                            _allFiles.postValue(filesList);
                            sortIntoExtensionGroups(filesList);
                        }
                ));
    }

    private void sortIntoExtensionGroups(List<FileModel> filesList) {
        Log.e("Extensions", "Extension size =" + FileReader.map.size());
//        compositeDisposable.add(Observable.fromIterable(filesList)
//                .subscribeOn(schedulerProvider.io())
//                .observeOn(schedulerProvider.ui())
//                .groupBy(FileModel::getExtension)
//                .flatMapSingle(Observable::toList)
//                .subscribe(groups -> {
//                            Log.e("groupBy", "" + groups);
//                        },
//                        throwable -> Log.e("listExternalStorage", "Error Reading"),
//                        () -> {
//
//                        }
//                ));
    }

    public void onSortAlphabetically() {
        clearList();
        compositeDisposable.add(Observable.fromIterable(originalList)
                .toSortedList(FileModel::compareTo)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                        _allFiles::postValue,
                        throwable -> Log.e("listExternalStorage", "Error Sorting Alphabetically")
                ));
    }

    public void onSortChronologically() {
        clearList();
        compositeDisposable.add(Observable.fromIterable(originalList)
                .toSortedList((f1, f2) -> f1.lastModified.compareTo(f2.lastModified))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                        _allFiles::postValue,
                        throwable -> Log.e("listExternalStorage", "Error Sorting Chronologically")
                ));
    }

    public void onSortExtension() {
        Log.e("listExternalStorage", "sortExtension");
    }

    public void onSearch(String query) {
        clearList();
        List<FileModel> listToSearch = new ArrayList<>(originalList);
        compositeDisposable.add(Observable.fromIterable(listToSearch)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .flatMap(Observable::just)
                .filter(file -> {
                    boolean matched = file.name.toLowerCase().contains(query);
                    if (matched) {
                        file.query = query;
                    }
                    return matched;
                })
                .toList()
                .subscribe(fileModels -> {
                            _searchResult.postValue(fileModels.size());
                            _allFiles.postValue(fileModels);
                            _searchOptionsVisibility.postValue(!query.isEmpty());
                        },
                        throwable -> Log.e("listExternalStorage", "Error onSearch")
                ));
    }

    public void onSaveSearchResults() {

    }

    public void onItemClick(FileModel fileModel) {
        _toDetail.postValue(fileModel);
    }

    private void clearList() {
        _allFiles.postValue(new ArrayList<FileModel>());
    }
}
