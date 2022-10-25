package ru.regiuss.dxf.selection.helper.reader;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.Locale;

@Log4j2
public class ReaderFactory {
    public static Reader create(File file) throws Exception {
        log.debug("create reader by {}", file);
        int dotIndex = file.getName().lastIndexOf('.');
        if(dotIndex < 0) throw new IllegalArgumentException(file.getName() + " unknown extension");
        String extension = file.getName().substring(dotIndex +1).toLowerCase(Locale.ROOT);
        log.debug("extension {}", extension);
        switch (extension) {
            case "xls": return new XLSReader(file);
        }
        throw new IllegalArgumentException("extension [" + extension + "] not have reader");
    }
}
