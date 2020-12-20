package com.test.filespath.feature.main;

import java.util.Objects;

public class FileModel implements Comparable<FileModel> {
    public String path;
    public String name;
    public long size;
    public Long lastModified;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileModel fileModel = (FileModel) o;
        return size == fileModel.size &&
                lastModified.equals(fileModel.lastModified) &&
                path.equals(fileModel.path) &&
                name.equals(fileModel.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, name, size, lastModified);
    }
}
