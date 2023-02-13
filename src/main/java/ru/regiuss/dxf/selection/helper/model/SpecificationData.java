package ru.regiuss.dxf.selection.helper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class SpecificationData {
    private String path;
    private int[] indexes;
}
