package com.test.filespath.feature.main.reader;

import android.content.Context;
import android.net.Uri;
import android.text.format.Formatter;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class FileModel implements Comparable<FileModel>, Serializable {

    private static final String NO_EXTENSION = "NoExtension";
    public String path;
    public String name;
    public long size;
    public Long lastModified;
    public String query;

    public String getExtension() {
        if (path.lastIndexOf(".") != -1) {
            String ext = path.substring(path.lastIndexOf("."));

            if (ext.contains("/")) {
                return NO_EXTENSION;
            } else {
                return path.substring(path.lastIndexOf("."));
            }

        } else {
            return NO_EXTENSION;
        }
    }

    public Uri getFileUri() {
        return Uri.fromFile(new File(path));
    }

    public String getSizeInMb(Context context) {
        return Formatter.formatFileSize(context, new File(path).length());
    }

    public String getLastModifiedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(new Date(lastModified));
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
