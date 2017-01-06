package com.sega.vimarket.model;

/**a
 * Created by Sega on 04/01/2017.
 */

public class FileModel {

    private String type;
    private String url_file;
    public FileModel(){

    }

    public FileModel(String url_file) {
        this.type = "img";
        this.url_file = url_file;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl_file() {
        return url_file;
    }


}
