package com.test.filespath.feature;

import android.os.Bundle;

import com.test.filespath.R;
import com.test.filespath.feature.detail.DetailFragment;
import com.test.filespath.feature.main.MainFragment;
import com.test.filespath.feature.main.MainNavigation;
import com.test.filespath.feature.main.reader.FileModel;

import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends DaggerAppCompatActivity implements MainNavigation {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        addMainFragment();
    }

    private void addMainFragment() {
        MainFragment fragment = MainFragment.newInstance();
        fragment.setMainNavigation(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_main_container, fragment)
                .commit();
    }

    @Override
    public void toDetailScreen(FileModel fileModel) {
        DetailFragment fragment = DetailFragment.newInstance(fileModel);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_main_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}