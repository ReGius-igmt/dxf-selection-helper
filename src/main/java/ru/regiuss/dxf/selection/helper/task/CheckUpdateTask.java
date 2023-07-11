package ru.regiuss.dxf.selection.helper.task;

import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@RequiredArgsConstructor
public class CheckUpdateTask extends Task<String> {

    private final String currentVersionTag;

    @Override
    protected String call() throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL("https://api.github.com/repos/ReGius-igmt/dxf-selection-helper/releases/latest").openConnection();
        StringBuilder builder = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line);
        }
        String response = builder.toString();
        Matcher m = Pattern.compile("(?<=\"tag_name\":\")[^\"]+").matcher(response);
        if(!m.find()) throw new RuntimeException("tag name not match");
        String tag = m.group();
        log.info("found last version tag {}", tag);
        if(!currentVersionTag.equals(tag)) {
            m = Pattern.compile("(?<=\"html_url\":\")[^\"]+").matcher(response);
            if(!m.find()) throw new RuntimeException("version url not match");
            return m.group();
        }
        return null;
    }
}
