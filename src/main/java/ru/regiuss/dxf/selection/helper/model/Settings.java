package ru.regiuss.dxf.selection.helper.model;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;

@Data
@Log4j2
public class Settings implements Serializable {

    private static final long serialVersionUID = 2L;

    private String result;
    private String source;
    private String specification;
    private int[] indexes;
    private HashSet<String> op;
    private HashSet<String> template;
    private HashSet<String> size;
    private boolean clearResultFolder;
    private boolean checkCount;

    public void save(String path) {
        try(ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path))) {
            os.writeObject(this);
        } catch (Exception e) {
            log.error("save settings error", e);
        }
    }
}
