package com.test.filespath.di.module;

import android.content.Context;

import com.google.gson.Gson;
import com.test.filespath.FilesApplication;
import com.test.filespath.rx.AppSchedulerProvider;
import com.test.filespath.rx.SchedulerProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class AppModule {

    @Singleton
    @Provides
    Context provideContext(FilesApplication context) {
        return context;
    }

    @Singleton
    @Provides
    SchedulerProvider provideSchedulers() {
        return new AppSchedulerProvider();
    }

    @Singleton
    @Provides
    Gson provideGson() {
        return new Gson();
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }
}
