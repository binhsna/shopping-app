package vn.binhnc.banhanga.model;

import java.io.Serializable;

public class HinhAnh implements Serializable {

    private String url;

    public HinhAnh() {
    }

    public HinhAnh(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
