package com.liang.novel.plugin.pojo;

import java.io.Serializable;

public class Chapter implements Serializable {

    /**
     * 章节索引
     */
    private Integer index;

    /**
     * 章节标题
     */
    private String title;

    /**
     * 当前章节所在行数
     */
    private Integer row;

    /**
     * 本章字数
     */
    private Integer count;

    /**
     * 是否添加书签
     */
    private Boolean bookmark = false;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Boolean getBookmark() {
        return bookmark;
    }

    public void setBookmark(Boolean bookmark) {
        this.bookmark = bookmark;
    }

    @Override
    public String toString() {
        return title + "   " + row;
    }
}
