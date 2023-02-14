package ru.regiuss.dxf.selection.helper.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.regiuss.dxf.selection.helper.reader.Reader;
import ru.regiuss.dxf.selection.helper.reader.ReaderFactory;
import ru.regiuss.dxf.selection.helper.reader.Row;
import ru.regiuss.dxf.selection.helper.util.Utils;

import java.io.File;
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
    private String[][] preview;
    private int[] indexes;


    public void read(int[] indexes) throws Exception {
        preview = new String[10][];
        int previewIndex = 0;
        try(Reader reader = ReaderFactory.create(source)) {
            op = new HashSet<>(reader.length());
            template = new HashSet<>(reader.length());
            size = new HashSet<>(reader.length());
            if(indexes == null && reader.hasNext()) {
                Row row = reader.next();
                preview[previewIndex++] = row.toArray();
                indexes = Utils.readIndexes(row);
            }
            this.indexes = indexes;
            if(indexes == null) return;
            while (reader.hasNext()) {
                Row row = reader.next();
                if(previewIndex < 10) preview[previewIndex++] = row.toArray();
                add(template, row.get(indexes[1]));
                add(size, row.get(indexes[2]));
                add(op, row.get(indexes[3]));
            }
            log.debug("SET TEMPLATE: {}", template);
            log.debug("SET SIZE: {}", size);
            log.debug("SET OP: {}", op);
        }
    }

    private void add(Set<String> set, String value) {
        if(value != null && !value.isEmpty()) set.add(value);
    }
}
