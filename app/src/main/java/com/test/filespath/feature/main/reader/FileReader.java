package com.test.filespath.feature.main.reader;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileReader implements Publisher<FileModel> {

    public static Map<String, Object> map = new HashMap<String, Object>();

    private Subscriber<? super FileModel> subscriber;
    private final File file;

    public FileReader(File directory) {
        this.file = directory;
    }

    @Override
    public void subscribe(Subscriber<? super FileModel> s) {
        if (s == null) {
            return;
        }

        subscriber = s;
        listFiles(file);
        subscriber.onComplete();
    }

    private void listFiles(File directory) {
        File[] filesList = directory.listFiles();
        if (filesList != null) {
            for (File file : filesList) {
                if (file != null) {
                    if (file.isDirectory()) {
                        listFiles(file);
                    } else {
                        FileModel fileModel = new FileModel();
                        fileModel.name = file.getName();
                        fileModel.path = file.getAbsolutePath();
                        fileModel.size = file.getTotalSpace();
                        fileModel.lastModified = file.lastModified();

                        map.put(fileModel.getExtension(), "");

                        subscriber.onNext(fileModel);
                    }
                }
            }
        }
    }
}
