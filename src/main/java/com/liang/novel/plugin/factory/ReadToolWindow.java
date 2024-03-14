package com.liang.novel.plugin.factory;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.ContentUI;
import com.liang.novel.plugin.actions.*;
import com.liang.novel.plugin.pojo.Book;
import com.liang.novel.plugin.state.NovelState;
import com.liang.novel.plugin.ui.ToolWindowUI;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ReadToolWindow implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ToolWindowUI toolWindowUI = new ToolWindowUI(project);
        Book currentReadBook = toolWindowUI.getCurrentReadBook();

        Content content = ContentFactory.getInstance().createContent(toolWindowUI.getContentPanel(), currentReadBook != null ? currentReadBook.getBookName() : "", false);
        toolWindow.getContentManager().addContent(content);
    }

}
