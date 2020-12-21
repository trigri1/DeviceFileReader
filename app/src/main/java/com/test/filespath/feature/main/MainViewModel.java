package com.test.filespath.feature.main;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
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

    private final MutableLiveData<SearchModel> _saveSearch = new MutableLiveData<SearchModel>();
    protected LiveData<SearchModel> saveSearch = _saveSearch;

    private final MutableLiveData<List<String>> _extensions = new MutableLiveData<List<String>>();
    protected LiveData<List<String>> extensions = _extensions;

    private final List<FileModel> filesList = new ArrayList<FileModel>();

    private final List<FileModel> originalList = new ArrayList<FileModel>();

    private final MutableLiveData<Boolean> _loading = new MutableLiveData<Boolean>();
    protected LiveData<Boolean> loading = _loading;

    private ArrayList<String> extensionsList = new ArrayList<String>();


    private final Gson gson;

    @Inject
    public MainViewModel(Gson gson, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(schedulerProvider, compositeDisposable);
        this.gson = gson;
    }

    public void readAllFiles(List<File> directoryList) {
        readDirectoryList(directoryList);
    }

    private void readDirectoryList(List<File> directoryList) {
        readFiles(directoryList);
    }

    private void readFiles(List<File> files) {
        compositeDisposable.add(Observable.fromIterable(files)
                .subscribeOn(schedulerProvider.newThread())
                .observeOn(schedulerProvider.ui())
                .doOnSubscribe(disposable -> _loading.postValue(true))
                .doOnComplete(() -> _loading.postValue(false))
                .concatMap(file -> Observable.fromPublisher(new FileReader(file)))
                .subscribe(filesList::add,
                        throwable -> Log.e("listExternalStorage", "Error Reading"),
                        () -> {
                            originalList.addAll(filesList);
                            _allFiles.postValue(filesList);
                            sortIntoExtensionGroups();
                        }
                ));
    }

    private void sortIntoExtensionGroups() {
        Log.e("Extensions", "Extension size =" + FileReader.map.size());
        extensionsList.addAll(FileReader.map.keySet());
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
        _extensions.postValue(extensionsList);
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

    public void onExtensionSelected(String query) {
        clearList();
        List<FileModel> listToSearch = new ArrayList<>(originalList);
        compositeDisposable.add(Observable.fromIterable(listToSearch)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .flatMap(Observable::just)
                .filter(file -> {
                    boolean matched = file.name.toLowerCase().endsWith(query);
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
        List<FileModel> list = _allFiles.getValue();
        if (list != null && !list.isEmpty()) {

            String result = gson.toJson(list);
            SearchModel searchModel = new SearchModel();
            searchModel.payload = result;
            searchModel.name = list.get(0).query + System.currentTimeMillis() + ".txt";
            _saveSearch.postValue(searchModel);
            Log.e("listExternalStorage", result);
        }
    }

    public void onItemClick(FileModel fileModel) {
        _toDetail.postValue(fileModel);
    }

    private void clearList() {
        _allFiles.postValue(new ArrayList<FileModel>());
    }
}
