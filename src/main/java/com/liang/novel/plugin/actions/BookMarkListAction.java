package com.liang.novel.plugin.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.DumbAware;
import com.liang.novel.plugin.icons.Icons;
import com.liang.novel.plugin.pojo.Book;
import com.liang.novel.plugin.state.NovelState;
import com.liang.novel.plugin.ui.ReadUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BookMarkListAction extends ToggleAction implements DumbAware {

    private ReadUI readUI;

    private Boolean onlyBookMarkChapter = false;

    public BookMarkListAction(ReadUI readUI) {
        this.readUI = readUI;

        getTemplatePresentation().setText("书签列表");
        getTemplatePresentation().setIcon(Icons.BOOK_MARK_LIST_ICON);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return onlyBookMarkChapter;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        Book currentReadBook = NovelState.getInstance().getCurrentReadBook();

        JComponent leftPanel = readUI.getLeftPanel();
        if (!leftPanel.isVisible()) {
            leftPanel.setVisible(true);
        }
        onlyBookMarkChapter = !onlyBookMarkChapter;
        readUI.refreshChapterListAsync(currentReadBook, onlyBookMarkChapter);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Book currentReadBook = NovelState.getInstance().getCurrentReadBook();

        Presentation presentation = e.getPresentation();
        presentation.setEnabled(currentReadBook != null);
    }
}
