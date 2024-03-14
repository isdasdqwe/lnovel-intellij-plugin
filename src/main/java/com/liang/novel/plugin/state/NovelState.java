package com.liang.novel.plugin.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.liang.novel.plugin.pojo.Book;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(name = "novelState", storages = @Storage("NovelState.xml"))
public class NovelState implements PersistentStateComponent<NovelState> {

    /**
     * 是否使用自定义字体
     */
    private Boolean useCustomFont;

    /**
     * 字体
     */
    private String fontType;

    /**
     * 字号
     */
    private Integer fontSize;

    /**
     * 当前正在读（书架中的第几本，用Integer而不是Book，因为会导致多存一个Book的内容）
     */
    private Integer currentReadBookIndex;

    /**
     * 书架
     */
    private List<Book> bookList;

    public NovelState() {
        this.useCustomFont = false;
        this.fontType = "Monospaced";
        this.fontSize = 12;
        this.currentReadBookIndex = null;
        this.bookList = new ArrayList<>();
    }

    public static NovelState getInstance() {
        return ApplicationManager.getApplication().getService(NovelState.class);
    }

    @Override
    public @Nullable NovelState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull NovelState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public Boolean getUseCustomFont() {
        return useCustomFont;
    }

    public void setUseCustomFont(Boolean useCustomFont) {
        this.useCustomFont = useCustomFont;
    }

    public String getFontType() {
        return fontType;
    }

    public void setFontType(String fontType) {
        this.fontType = fontType;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public Integer getCurrentReadBookIndex() {
        return currentReadBookIndex;
    }

    public void setCurrentReadBookIndex(Integer currentReadBookIndex) {
        this.currentReadBookIndex = currentReadBookIndex;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }

    public void setNewBook(Book newBook){
        for (int i = 0; i < bookList.size(); i++) {
            Book book = bookList.get(i);
            if (book.getUrl().equals(newBook.getUrl())) {
                bookList.set(i, book);
            }
        }
        setBookList(bookList);
    }

    public Book getCurrentReadBook() {
        return currentReadBookIndex != null ? bookList.get(currentReadBookIndex) : null;
    }
}
