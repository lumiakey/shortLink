package com.what2e.entity;

public class LinkInfo {

    private String longLink; //长链接
    private String custom;  //自定义字符串
    private int validity; //有效期
    private int length; //配置长度

    public String getLongLink() {
        return longLink;
    }

    public void setLongLink(String longLink) {
        this.longLink = longLink;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }


}
