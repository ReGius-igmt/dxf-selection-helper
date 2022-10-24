package ru.regiuss.dxf.selection.helper.reader;

public interface Reader extends AutoCloseable {
    boolean hasNext();
    Row next();
    int length();
}
