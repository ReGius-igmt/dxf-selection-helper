package ru.regiuss.dxf.selection.helper.reader;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class ReaderFactory {
    public static Reader create(File file) throws IOException {
        int dotIndex = file.getName().lastIndexOf('.');
        if(dotIndex < 0) throw new IllegalArgumentException(file.getName() + " unknown extension");
        String extension = file.getName().substring(dotIndex +1).toLowerCase(Locale.ROOT);
        switch (extension) {
            case "xls": return new XLSReader(file);
        }
        throw new IllegalArgumentException("extension [" + extension + "] not have reader");
    }
}
