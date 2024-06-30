package br.com.lucianobrito.enveditor.models;

public class EnvModel {

    private String chave;
    private String valor;
    private String chaveAnterior;

    public EnvModel(String chave, String valor, String chaveAnterior) {
        this.chave = chave;
        this.valor = valor;
        this.chaveAnterior = chaveAnterior;
    }

    public String getChave() {
        return chave;
    }

    public String getValor() {
        return valor;
    }

    public String getChaveAnterior() {
        return chaveAnterior;
    }

    @Override
    public String toString() {
        return "EnvModel{" +
                "chave='" + chave + '\'' +
                ", valor='" + valor + '\'' +
                '}';
    }
}
