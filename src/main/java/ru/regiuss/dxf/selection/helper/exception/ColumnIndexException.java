package ru.regiuss.dxf.selection.helper.exception;

import lombok.Getter;

@Getter
public class ColumnIndexException extends RuntimeException {
    private final String column;

    public ColumnIndexException(String column) {
        super(String.format("Столбец [%s] не найден", column));
        this.column = column;
    }
}
