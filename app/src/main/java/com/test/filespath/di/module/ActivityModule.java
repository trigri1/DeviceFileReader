package com.test.filespath.di.module;

import com.test.filespath.feature.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract MainActivity contributeActivityInjector();
}
