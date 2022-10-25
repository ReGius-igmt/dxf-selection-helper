package ru.regiuss.dxf.selection.helper.task;

import javafx.concurrent.Task;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.regiuss.dxf.selection.helper.reader.Reader;
import ru.regiuss.dxf.selection.helper.reader.ReaderFactory;
import ru.regiuss.dxf.selection.helper.reader.Row;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;

@AllArgsConstructor
@Log4j2
public class StartTask extends Task<Void> {

    private Set<String> op;
    private Set<String> template;
    private Set<String> size;
    private File specification;
    private Path source;
    private Path result;
    private boolean clearResultFolder;

    @Override
    protected Void call() throws Exception {
        updateMessage("Запуск...");
        updateProgress(0, 1);

        if(clearResultFolder) clearFolder(result);

        if(!specification.exists()) throw new FileNotFoundException("файл " + specification + " не существует");
        if(!specification.isFile()) throw new RuntimeException(specification + " не является файлом");
        try(Reader reader = ReaderFactory.create(specification)) {
            Row row;
            int c = 0;
            while (reader.hasNext() && !Thread.currentThread().isInterrupted()) {
                updateMessage(String.format("Прогресс (%s/%s)", ++c, reader.length()));
                updateProgress(c, reader.length());
                row = reader.next();
                if(check(template, row.get(3)) || check(size, row.get(4)) || check(op, row.get(5))) continue;
                Path filePath = source.resolve(row.get(1) + ".dxf");
                log.info("copy file {}", filePath);
                if(filePath.toFile().exists())
                    Files.copy(filePath, result.resolve(row.get(1) + ".dxf"), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        return null;
    }

    private boolean check(Set<String> set, String value) {
        log.debug(value);
        return value == null || value.isEmpty() || !set.contains(value);
    }

    private void clearFolder(Path path) {
        File resultFile = path.toFile();
        if(resultFile.exists() && resultFile.isDirectory()) {
            File[] files = resultFile.listFiles();
            if(files == null) return;
            int c = 0;
            for(File f : files) {
                if(Thread.currentThread().isInterrupted()) break;
                updateMessage(String.format("Удаление (%s/%s)", ++c, files.length));
                updateProgress(++c, files.length);
                log.debug("delete file {}", f);
                f.delete();
            }
        } else resultFile.mkdirs();
    }
}
