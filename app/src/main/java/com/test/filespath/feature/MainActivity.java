package com.test.filespath.feature;

import android.os.Bundle;

import com.test.filespath.R;
import com.test.filespath.feature.main.MainFragment;

import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends DaggerAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getSupportFragmentManager().beginTransaction().
                replace(R.id.fl_main_container, new MainFragment())
                .commit();
    }
}