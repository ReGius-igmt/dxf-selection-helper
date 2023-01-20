package ru.regiuss.dxf.selection.helper.util;

import ru.regiuss.dxf.selection.helper.exception.ColumnIndexException;
import ru.regiuss.dxf.selection.helper.reader.Row;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Utils {
    public static int[] readIndexes(Row row) {
        int[] indexes = new int[] {-1, -1, -1, -1, -1};
        List<String> values = Arrays.asList("обозначение", "заготовка", "типоразмер", "оп1", "к-во");
        for (int i = 0; i < row.size(); i++) {
            String v = row.get(i).toLowerCase(Locale.ROOT);
            int index = values.indexOf(v);
            if(index != -1) indexes[index] = i;
        }
        for (int i = 0; i < indexes.length; i++) {
            if(indexes[i] < 0) throw new ColumnIndexException(values.get(i));
        }
        return indexes;
    }
}
