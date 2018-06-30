package com.arcsoft.sdk_demo;

import android.graphics.Rect;

public class ProcessOut {
    private int left;
    private int top;
    private Rect rect;
    private String desc;
    ProcessOut(int left,int top,Rect rect,String desc){
        this.left=left;
        this.top=top;
        this.rect=rect;
        this.desc=desc;
    }
    ProcessOut(){}

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public Rect getRect() {
        return rect;
    }

    public int getTop() {
        return top;
    }

    public String getDesc() {
        return desc;
    }
}
