package ru.regiuss.dxf.selection.helper.reader;

public interface Row {
    String get(int i);
    String[] toArray();
    int size();
}
