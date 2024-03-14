package com.liang.novel.plugin.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Book implements Serializable {

    /**
     * 书名
     */
    private String bookName;

    /**
     * 本地地址或链接
     */
    private String url;

    /**
     * 总字数
     */
    private Integer count;

    /**
     * 上次读到哪章
     */
    private Integer chapterIndex;

    /**
     * 章节列表
     */
    private List<Chapter> chapterList;

    public Book() {
        this.bookName = "";
        this.url = "";
        this.count = 0;
        this.chapterIndex = 0;
        this.chapterList = new ArrayList<>();
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(Integer chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public List<Chapter> getChapterList() {
        return chapterList;
    }

    public void setChapterList(List<Chapter> chapterList) {
        this.chapterList = chapterList;
    }

    @Override
    public String toString() {
        return bookName;
    }
}
