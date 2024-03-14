package com.liang.novel.plugin.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.util.NlsActions;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.AnActionButton;
import com.liang.novel.plugin.icons.Icons;
import com.liang.novel.plugin.ui.ReadUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ToggleChapterAction extends DumbAwareAction {

    private ReadUI readUI;

    public ToggleChapterAction(ReadUI readUI) {
        this.readUI = readUI;

        getTemplatePresentation().setText("隐藏章节列表");
        getTemplatePresentation().setIcon(AllIcons.Actions.ArrowCollapse);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        JComponent leftPanel = readUI.getLeftPanel();
        //隐藏或展示
        leftPanel.setVisible(!leftPanel.isVisible());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        JComponent leftPanel = readUI.getLeftPanel();
        //更新图标
        Presentation presentation = e.getPresentation();
        if (leftPanel.isVisible()) {
            presentation.setText("隐藏章节列表");
            presentation.setIcon(AllIcons.Actions.ArrowCollapse);
        }else{
            presentation.setText("打开章节列表");
            presentation.setIcon(AllIcons.Actions.ArrowExpand);
        }
    }
}
