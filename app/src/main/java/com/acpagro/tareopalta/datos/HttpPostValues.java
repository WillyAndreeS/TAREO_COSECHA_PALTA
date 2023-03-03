package com.acpagro.tareopalta.datos;

public class HttpPostValues {
    private String parametro;
    private String valor;

    public HttpPostValues(String parametro, String valor){
        this.parametro = parametro;
        this.valor = valor;
    }

    public String getParametro() {
        return parametro;
    }

    public String getValor() {
        return valor;
    }

}
