package ru.regiuss.dxf.selection.helper.util;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import ru.regiuss.dxf.selection.helper.exception.ColumnIndexException;
import ru.regiuss.dxf.selection.helper.reader.Row;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Utils {
    public static int[] readIndexes(Row row) {
        int[] indexes = new int[] {-1, -1, -1, -1, -1};
        List<String> values = Arrays.asList("обозначение", "заготовка", "типоразмер", "оп1", "к-во");
        for (int i = 0; i < row.size(); i++) {
            String v = row.get(i).toLowerCase(Locale.ROOT);
            int index = values.indexOf(v);
            if(index != -1) indexes[index] = i;
        }
        for (int i = 0; i < indexes.length; i++) {
            if(indexes[i] < 0) throw new ColumnIndexException(values.get(i));
        }
        return indexes;
    }

    public static void browseFile(TextField field, Window window) {
        FileChooser chooser = new FileChooser();
        if(field.getText() != null && !field.getText().isEmpty()) {
            File initialDirectory = new File(field.getText());
            if(initialDirectory.exists())
                chooser.setInitialDirectory(initialDirectory.isFile() ? initialDirectory.getParentFile() : initialDirectory);
        }
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("EXCEL", "*.xls"));
        File specificationFile = chooser.showOpenDialog(window);
        if(specificationFile != null) field.setText(specificationFile.getAbsolutePath());
    }

    public static void browseFolder(TextField field, Window window) {
        DirectoryChooser chooser = new DirectoryChooser();
        if(!field.getText().isEmpty()) {
            File initialDirectory = new File(field.getText());
            if(initialDirectory.exists() && initialDirectory.isDirectory())
                chooser.setInitialDirectory(initialDirectory);
        }
        File resultFolder = chooser.showDialog(window);
        if(resultFolder != null) field.setText(resultFolder.getAbsolutePath());
    }
}
