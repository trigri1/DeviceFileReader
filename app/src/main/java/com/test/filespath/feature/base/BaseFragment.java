package com.test.filespath.feature.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;

import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;

public abstract class BaseFragment<V> extends DaggerFragment {

    protected V viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        viewModel = getViewModel();
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        return view;
    }

    public abstract @LayoutRes
    int getLayoutId();

    public abstract V getViewModel();
}
