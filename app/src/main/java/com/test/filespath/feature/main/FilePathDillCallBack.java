package com.test.filespath.feature.main;

import com.test.filespath.feature.base.BaseDiffCallBack;

import java.util.List;

public class FilePathDillCallBack extends BaseDiffCallBack<FileModel> {

    public FilePathDillCallBack(List<FileModel> oldList, List<FileModel> newList) {
        super(oldList, newList);
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        FileModel oldItem = oldList.get(oldItemPosition);
        FileModel newItem = newList.get(newItemPosition);
        return oldItem == newItem;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        FileModel oldItem = oldList.get(oldItemPosition);
        FileModel newItem = newList.get(newItemPosition);

        return oldItem.name.equalsIgnoreCase(newItem.name)
                && oldItem.path.equalsIgnoreCase(newItem.path)
                && oldItem.size == newItem.size;
    }
}
