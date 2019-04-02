package com.example.dflet.scripttanklogindemo;

public class WriterSearchResult {
    public String title, writer;

    public WriterSearchResult(String title, String writer){
        this.title = title;
        this.writer = writer;
    }

    public WriterSearchResult() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

}
