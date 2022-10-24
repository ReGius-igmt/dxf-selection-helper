package ru.regiuss.dxf.selection.helper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class SpecificationStorage {
    private final File source;
    private Set<String> op;
    private Set<String> template;
    private Set<String> size;
}
