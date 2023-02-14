package ru.regiuss.dxf.selection.helper.reader;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class XLSReader implements Reader {

    private final Workbook workbook;
    private final Sheet sheet;
    private final int length;
    private int current;

    public XLSReader(File file) throws IOException, BiffException {
        this.workbook = Workbook.getWorkbook(file);
        this.sheet = workbook.getSheet(0);
        this.current = 0;
        length = sheet.getRows();
    }

    @Override
    public boolean hasNext() {
        return current < length;
    }

    @Override
    public Row next() {
        final Cell[] row = sheet.getRow(current++);
        return new Row() {
            @Override
            public String get(int i) {
                return row[i].getContents();
            }

            @Override
            public String[] toArray() {
                return Arrays.stream(row).map(Cell::getContents).toArray(String[]::new);
            }

            @Override
            public int size() {
                return row.length;
            }
        };
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public void close() throws Exception {
        workbook.close();
    }
}
