package ru.regiuss.dxf.selection.helper.task;

import javafx.concurrent.Task;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.regiuss.dxf.selection.helper.model.Settings;
import ru.regiuss.dxf.selection.helper.model.TaskResult;
import ru.regiuss.dxf.selection.helper.reader.Reader;
import ru.regiuss.dxf.selection.helper.reader.ReaderFactory;
import ru.regiuss.dxf.selection.helper.reader.Row;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@Log4j2
public class StartTask extends Task<TaskResult> {

    private final Settings settings;

    @Override
    protected TaskResult call() throws Exception {
        updateMessage("Запуск...");
        updateProgress(0, 1);

        Path result = Paths.get(settings.getResult());
        if(settings.isClearResultFolder()) clearFolder(result);

        File specification = new File(settings.getSpecification());
        if(!specification.exists()) throw new FileNotFoundException("файл " + specification + " не существует");
        if(!specification.isFile()) throw new RuntimeException(specification + " не является файлом");

        int found = 0;
        int copied = 0;
        List<String> notFoundFiles = new LinkedList<>();

        try(Reader reader = ReaderFactory.create(specification)) {
            Path source = Paths.get(settings.getSource());
            Row row;
            int c = 0;

            int[] indexes = settings.getIndexes();

            while (reader.hasNext() && !isCancelled()) {
                updateMessage(String.format("Прогресс (%s/%s)", ++c, reader.length()));
                updateProgress(c, reader.length());
                row = reader.next();
                if(check(row, indexes)) continue;
                found++;
                Path filePath = source.resolve(row.get(indexes[0]) + ".dxf");

                if(filePath.toFile().exists()) {
                    log.debug("copy file {}", filePath);
                    if(settings.isCheckCount()) {
                        int count = 1;
                        try {
                            count = Integer.parseInt(row.get(indexes[1]));
                        } catch (Exception e) {
                            log.error("parse count error value:{}", row.get(indexes[1]), e);
                        }
                        for (int i = 0; i < count; i++) {
                            Files.copy(filePath, result.resolve(row.get(indexes[0]) + String.format(" (%03d.%03d)", count, i+1) + ".dxf"), StandardCopyOption.REPLACE_EXISTING);
                        }
                    } else
                        Files.copy(filePath, result.resolve(row.get(indexes[0]) + ".dxf"), StandardCopyOption.REPLACE_EXISTING);
                    copied++;
                } else {
                    notFoundFiles.add(filePath.getFileName().toString());
                    log.debug("file not exists {}", filePath);
                }
            }
        }
        return new TaskResult(found, copied, notFoundFiles);
    }

    private boolean check(Row row, int[] indexes) {
        for (int i = 0; i < settings.getValues().length; i++) {
            String value = row.get(indexes[2+i]);
            if(value == null || value.isEmpty() || !settings.getValues()[i].contains(value))
                return true;
        }
        return false;
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
                f.delete();
            }
        } else resultFile.mkdirs();
    }
}
