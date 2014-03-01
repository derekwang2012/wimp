package com.video.wimp.util;


public class Youtube {

    private int id;
    private String yid;
    private String title;
    private String createDate;

    // Empty constructor
    public Youtube(){
    }

    // constructor
    public Youtube(int id, String yid, String title, String createDate){
        this.id = id;
        this.yid = yid;
        this.title = title;
        this.createDate = createDate;
    }

    // constructor
    public Youtube(String yid, String title, String createDate){
        this.yid = yid;
        this.title = title;
        this.createDate = createDate;
    }

    public String getYid() {
        return yid;
    }

    public void setYid(String yid) {
        this.yid = yid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
