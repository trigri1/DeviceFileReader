package com.test.filespath.di.module;


import com.test.filespath.feature.main.MainFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract MainFragment contributeActivityInjector();
}
