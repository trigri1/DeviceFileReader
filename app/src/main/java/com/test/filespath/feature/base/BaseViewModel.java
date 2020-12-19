package com.test.filespath.feature.base;

import androidx.lifecycle.ViewModel;

import com.test.filespath.rx.SchedulerProvider;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseViewModel extends ViewModel {

    protected final CompositeDisposable compositeDisposable;
    protected final SchedulerProvider schedulerProvider;

    public BaseViewModel(SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        this.schedulerProvider = schedulerProvider;
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }
}
