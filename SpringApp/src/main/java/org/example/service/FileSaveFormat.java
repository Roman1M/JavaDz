package org.example.service;

public enum FileSaveFormat {
    JPG("jpg"),
    PNG("png"),
    GIF("gif"), webp("webp");

    private final String extension;

    FileSaveFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
