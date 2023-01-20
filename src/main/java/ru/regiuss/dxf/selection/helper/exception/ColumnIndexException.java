package ru.regiuss.dxf.selection.helper.exception;

public class ColumnIndexException extends RuntimeException {
    private final String column;

    public ColumnIndexException(String column) {
        super(String.format("Column [%s] not found", column));
        this.column = column;
    }

    public String getColumn() {
        return column;
    }
}
