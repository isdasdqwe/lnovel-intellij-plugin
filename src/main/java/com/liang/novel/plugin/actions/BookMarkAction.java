package com.liang.novel.plugin.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.components.JBList;
import com.liang.novel.plugin.icons.Icons;
import com.liang.novel.plugin.pojo.Book;
import com.liang.novel.plugin.pojo.Chapter;
import com.liang.novel.plugin.state.NovelState;
import com.liang.novel.plugin.ui.ReadUI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BookMarkAction extends DumbAwareAction {

    private ReadUI readUI;

    public BookMarkAction(ReadUI readUI) {
        this.readUI = readUI;

        getTemplatePresentation().setText("书签");
        getTemplatePresentation().setIcon(Icons.BOOK_MARK_ICON);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<Chapter> dataList = readUI.getDataList();
        JBList<Chapter> jbList = readUI.getJbList();
        Integer currentChapterIndex = dataList.get(jbList.getSelectedIndex()).getIndex();

        Book book = readUI.getBook();
        List<Chapter> chapterList = book.getChapterList();
        Chapter chapter = chapterList.get(currentChapterIndex);
        chapter.setBookmark(!chapter.getBookmark());
        //持久化
        NovelState.getInstance().setNewBook(book);
        //异步刷新章节列表
        readUI.refreshChapterListAsync(dataList);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Book currentReadBook = NovelState.getInstance().getCurrentReadBook();
        JBList<Chapter> jbList = readUI.getJbList();
        int selectedIndex = jbList.getSelectedIndex();

        Presentation presentation = e.getPresentation();
        presentation.setEnabled(currentReadBook != null && selectedIndex != -1);
    }
}
