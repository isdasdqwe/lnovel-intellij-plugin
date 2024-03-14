package com.liang.novel.plugin.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.liang.novel.plugin.pojo.Book;
import com.liang.novel.plugin.state.NovelState;
import com.liang.novel.plugin.ui.ToolWindowUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class RefreshToolWindowUIAction extends DumbAwareAction {

    private ToolWindowUI toolWindowUI;

    public RefreshToolWindowUIAction(ToolWindowUI toolWindowUI) {
        this.toolWindowUI = toolWindowUI;

        getTemplatePresentation().setText("刷新");
        getTemplatePresentation().setIcon(AllIcons.Actions.Refresh);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Book currentReadBook = NovelState.getInstance().getCurrentReadBook();
        //修改toolwindow的标题
        ToolWindow toolWindow = ToolWindowManager.getInstance(e.getProject()).getToolWindow("lnovel");
        if (currentReadBook != null) {
            toolWindow.setTitle(currentReadBook.getBookName());
        } else {
            toolWindow.setTitle("");
        }
        //刷新面板
        SwingUtilities.invokeLater(() -> toolWindowUI.updateReadUI());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setEnabled(false);

        Book toolWindowUIBook = toolWindowUI.getCurrentReadBook();
        Book novelStatebook = NovelState.getInstance().getCurrentReadBook();

        if (toolWindowUIBook != null) {
            if (novelStatebook != null) {
                if (!toolWindowUIBook.getUrl().equals(novelStatebook.getUrl())) {
                    presentation.setEnabled(true);
                }
            } else {
                presentation.setEnabled(true);
            }
        } else {
            if (novelStatebook != null) {
                presentation.setEnabled(true);
            }
        }
    }

}
