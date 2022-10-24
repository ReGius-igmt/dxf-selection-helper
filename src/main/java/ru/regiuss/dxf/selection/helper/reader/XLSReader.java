package ru.regiuss.dxf.selection.helper.reader;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class XLSReader implements Reader {

    private final Workbook workbook;
    private final Iterator<org.apache.poi.ss.usermodel.Row> iterator;
    private final Sheet sheet;

    public XLSReader(File file) throws IOException {
        this.workbook = new HSSFWorkbook(new FileInputStream(file));
        this.sheet = workbook.getSheetAt(0);
        this.iterator = sheet.rowIterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Row next() {
        final org.apache.poi.ss.usermodel.Row row = iterator.next();
        return i -> row.getCell(i).getStringCellValue();
    }

    @Override
    public int length() {
        return sheet.getPhysicalNumberOfRows();
    }

    @Override
    public void close() throws Exception {
        workbook.close();
    }
}
