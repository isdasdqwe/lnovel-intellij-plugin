package com.liang.novel.plugin.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.liang.novel.plugin.icons.Icons;
import com.liang.novel.plugin.pojo.Book;
import com.liang.novel.plugin.state.NovelState;
import com.liang.novel.plugin.ui.ToolWindowUI;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class BookSelectAction extends DumbAwareAction {

    private ToolWindowUI toolWindowUI;

    public BookSelectAction(ToolWindowUI toolWindowUI) {
        this.toolWindowUI = toolWindowUI;

        getTemplatePresentation().setText("书架");
        getTemplatePresentation().setIcon(AllIcons.Actions.ListFiles);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        NovelState instance = NovelState.getInstance();
        List<Book> bookList = instance.getBookList();

        JBPopupFactory.getInstance()
                .createPopupChooserBuilder(bookList)
                .setTitle("书架")
                .setItemChosenCallback(book -> {
                    //设置当前在读
                    int selectedIndex = bookList.indexOf(book);
                    instance.setCurrentReadBookIndex(selectedIndex);
                    //修改toolwindow的标题
                    ToolWindow toolWindow = ToolWindowManager.getInstance(e.getProject()).getToolWindow("lnovel");
                    toolWindow.setTitle(book.getBookName());
                    //刷新面板
                    SwingUtilities.invokeLater(() -> toolWindowUI.updateReadUI());
                })
                .createPopup()
                //.showUnderneathOf(e.getInputEvent().getComponent()); //显示在Action下方显示
                .showInScreenCoordinates(e.getInputEvent().getComponent(), getPoint(e.getInputEvent().getComponent())); //想在右侧展示还得自己写坐标...
    }

    private Point getPoint(Component component) {
        Point locationOnScreen = component.getLocationOnScreen();
        int x = locationOnScreen.x + component.getWidth();
        int y = locationOnScreen.y;
        return new Point(x, y);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        List<Book> bookList = NovelState.getInstance().getBookList();

        Presentation presentation = e.getPresentation();
        presentation.setEnabled(CollectionUtils.isNotEmpty(bookList));
    }
}
