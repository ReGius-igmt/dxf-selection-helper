package ru.regiuss.dxf.selection.helper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TaskResult {
    private int found;
    private int copied;
    private List<String> files;
}
