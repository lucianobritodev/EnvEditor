package br.com.lucianobrito.enveditor.models.enuns;

import br.com.lucianobrito.enveditor.utils.EnvUtils;

public enum Env {

    GLOBAL("/etc/environment"),
    LOCAL(EnvUtils.USER_HOME + "/.environment");

    private String value;

    private Env(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
