package br.com.lucianobrito.enveditor.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;

public final class EnvUtils {
    private EnvUtils() {
    }


    public static String USER_HOME;
    public static String APP_VERSION;
    public static final String SYSTEM_ARCH = System.getProperty("os.arch");
    public static final String ENV_EDITOR_HOME = USER_HOME + "/.cache/enveditor";

    private static final String LOG_FILE = ENV_EDITOR_HOME + "/enveditor.log";

    public static FileHandler getFileHander() {

        try {
            File theDir = new File(ENV_EDITOR_HOME);
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            return new FileHandler(LOG_FILE);
        } catch (IOException e) {
            return null;
        }
    }
}
