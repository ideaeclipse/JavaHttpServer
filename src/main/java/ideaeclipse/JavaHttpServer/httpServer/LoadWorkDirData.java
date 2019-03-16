package ideaeclipse.JavaHttpServer.httpServer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class LoadWorkDirData {
    private final String replace;
    private final Map<String, File> data;

    LoadWorkDirData(final File workDir) {
        replace = workDir.getAbsolutePath() + "/";
        this.data = getData(workDir);
    }

    private Map<String, File> getData(final File workDir) {
        Map<String, File> data = new HashMap<>();
        for (File file : Objects.requireNonNull(workDir.listFiles())) {
            if (file.isDirectory())
                for (Map.Entry<String, File> entry : getData(file).entrySet())
                    data.put(entry.getKey(), entry.getValue());
            else
                data.put(file.getAbsolutePath().replace(this.replace, ""), file);
        }
        return data;
    }

    Map<String, File> getData() {
        return data;
    }
}
