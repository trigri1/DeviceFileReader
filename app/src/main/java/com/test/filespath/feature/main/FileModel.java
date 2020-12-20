package com.test.filespath.feature.main;

public class FileModel implements Comparable<FileModel> {
    public String path;
    public String name;
    public long size;

    public String getExtension() {
        if (path.lastIndexOf(".") != -1) {
            return path.substring(path.lastIndexOf("."));
        } else {
            return "No Extension";
        }
    }

    @Override
    public int compareTo(FileModel fileModel) {
        return name.compareTo(fileModel.name);
    }
}
