package com.test.filespath.feature.main;

public class FileModel {
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
}
