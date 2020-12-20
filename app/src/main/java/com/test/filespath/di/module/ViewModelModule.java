package com.test.filespath.di.module;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.test.filespath.di.ViewModelKey;
import com.test.filespath.feature.base.ViewModelProviderFactory;
import com.test.filespath.feature.detail.DetailViewModel;
import com.test.filespath.feature.main.MainViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory viewModelProviderFactory);

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    public abstract ViewModel bindsMainViewModel(MainViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel.class)
    public abstract ViewModel bindsDetailViewModel(DetailViewModel viewModel);
}
