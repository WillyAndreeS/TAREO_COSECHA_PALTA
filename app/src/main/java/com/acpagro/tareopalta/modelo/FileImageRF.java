package com.acpagro.tareopalta.modelo;

import java.io.File;

public class FileImageRF {
    private File file;
    private String name;

    public FileImageRF(File file, String name) {
        this.file = file;
        this.name = name;
    }

    public FileImageRF() {
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
