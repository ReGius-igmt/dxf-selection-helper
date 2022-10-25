package ru.regiuss.dxf.selection.helper.model;

import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;

@Data
public class Settings implements Serializable {

    private static final long serialVersionUID = 1L;

    private String result;
    private String source;
    private String specification;
    private HashSet<String> op;
    private HashSet<String> template;
    private HashSet<String> size;
    private boolean clearResultFolder;
}
