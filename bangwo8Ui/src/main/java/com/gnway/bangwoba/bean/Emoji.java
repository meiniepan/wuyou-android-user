package com.gnway.bangwoba.bean;

import java.io.Serializable;


public class Emoji implements Serializable {
    int imageUri;
    String content;

    public int getImageUri() {
        return imageUri;
    }

    public void setImageUri(int imageUri) {
        this.imageUri = imageUri;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
