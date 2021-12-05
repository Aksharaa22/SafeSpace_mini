package com.example.safespace;

public class putPDF {
    public String url;
    public String name;
    public putPDF()
    {

    }
    public putPDF(String name, String url) {
        this.url = url;
        this.name = name;
    }
    public  String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public  String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}