package ru.regiuss.dxf.selection.helper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.regiuss.dxf.selection.helper.reader.Reader;
import ru.regiuss.dxf.selection.helper.reader.ReaderFactory;
import ru.regiuss.dxf.selection.helper.reader.Row;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Getter
@RequiredArgsConstructor
@Log4j2
public class SpecificationStorage {
    private final File source;
    private Set<String> op;
    private Set<String> template;
    private Set<String> size;

    public void read() {
        try(Reader reader = ReaderFactory.create(source)) {
            op = new HashSet<>(reader.length());
            template = new HashSet<>(reader.length());
            size = new HashSet<>(reader.length());
            Row row;
            while (reader.hasNext()) {
                row = reader.next();
                template.add(row.get(3));
                size.add(row.get(4));
                op.add(row.get(5));
            }
            log.info(op);
            log.info(template);
            log.info(size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
