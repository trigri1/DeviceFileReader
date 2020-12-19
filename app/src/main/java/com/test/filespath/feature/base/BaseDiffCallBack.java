package com.test.filespath.feature.base;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

abstract public class BaseDiffCallBack<T> extends DiffUtil.Callback {

    public final List<T> oldList;
    public final List<T> newList;

    public BaseDiffCallBack(List<T> oldList, List<T> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }
}
