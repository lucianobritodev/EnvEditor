package br.com.lucianobrito.enveditor.utils;

public final class EnvUtils {
    private EnvUtils() {}

    public static final String USER_HOME = System.getProperty("user.home");
    public static final String SYSTEM_NAME = System.getProperty("os.name");
    public static final String SYSTEM_ARCH = System.getProperty("os.arch");

}
