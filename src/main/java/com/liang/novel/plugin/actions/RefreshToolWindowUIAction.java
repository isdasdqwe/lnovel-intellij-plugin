package com.liang.novel.plugin.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.liang.novel.plugin.constants.FontConstants;
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
        return ActionUpdateThread.EDT;
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
        presentation.setEnabled(isRefresh());
    }

    private Boolean isRefresh() {
        Book toolWindowUIBook = toolWindowUI.getCurrentReadBook();
        NovelState instance = NovelState.getInstance();
        Book novelStatebook = instance.getCurrentReadBook();

        //编辑书籍url时
        if (toolWindowUIBook != null && novelStatebook != null && !toolWindowUIBook.getUrl().equals(novelStatebook.getUrl())) {
            return true;
        }
        //删除书籍时
        if (toolWindowUIBook != null && novelStatebook == null) {
            return true;
        }
        //添加书籍时
        if (toolWindowUIBook == null && novelStatebook != null) {
            return true;
        }
        //字体变动时
        if (instance.getUseCustomFont()) {
            return !toolWindowUI.getFontType().equals(instance.getFontType()) || !toolWindowUI.getFontSize().equals(instance.getFontSize());
        } else {
            return !toolWindowUI.getFontType().equals(FontConstants.DEFAULT_FONT_TYPE) || !toolWindowUI.getFontSize().equals(FontConstants.DEFAULT_FONT_SIZE);
        }
    }


}
