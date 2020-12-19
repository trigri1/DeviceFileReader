package com.test.filespath.di;

import com.test.filespath.FilesApplication;
import com.test.filespath.di.module.ActivityModule;
import com.test.filespath.di.module.AppModule;
import com.test.filespath.di.module.FragmentModule;
import com.test.filespath.di.module.ViewModelModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        FragmentModule.class,
        ActivityModule.class,
        ViewModelModule.class}
)
public interface AppComponent extends AndroidInjector<FilesApplication> {

    @Override
    void inject(FilesApplication instance);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(FilesApplication application);

        AppComponent build();
    }
}
