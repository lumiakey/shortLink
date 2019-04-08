package com.what2e.entity;


public class LinkMapping {
    private Integer id; //主键id
    private String shortLink; //短链接的随机串
    private String longLink;   //长链接
    private String createTime;   //创建日期
    private String failureTime;  //失效日期
    private int validity;      //有效性  0失效 1有效
    private int visits;        //访问次数


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(String failureTime) {
        this.failureTime = failureTime;
    }

    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public String getShortLink() {
        return shortLink;
    }

    public void setShortLink(String shortLink) {
        this.shortLink = shortLink;
    }

    public String getLongLink() {
        return longLink;
    }

    public void setLongLink(String longLink) {
        this.longLink = longLink;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }
}
