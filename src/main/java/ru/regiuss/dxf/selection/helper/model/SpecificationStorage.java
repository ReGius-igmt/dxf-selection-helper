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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
@Log4j2
public class SpecificationStorage {
    private final File source;
    private Set<String>[] values;
    private List<String[]> preview;
    private int[] indexes;


    public void read(int[] indexes) throws Exception {
        preview = new LinkedList<>();
        try(Reader reader = ReaderFactory.create(source)) {
            values = new HashSet[4];
            for (int i = 0; i < 4; i++) {
                values[i] = new HashSet<>(reader.length());
            }
            if(reader.hasNext()) {
                Row row = reader.next();
                preview.add(row.toArray());
                if(indexes == null) indexes = Utils.readIndexes(row);
            } else return;
            this.indexes = indexes;
            while (reader.hasNext()) {
                Row row = reader.next();
                if(preview.size() < 100) preview.add(row.toArray());
                for (int i = 0; i < 4; i++) {
                    add(values[i], row.get(indexes[2+i]));
                }
            }
        }
    }

    private void add(Set<String> set, String value) {
        if(value != null && !value.isEmpty()) set.add(value);
    }
}
