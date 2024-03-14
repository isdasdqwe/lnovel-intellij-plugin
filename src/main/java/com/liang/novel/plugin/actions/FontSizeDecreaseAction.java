package com.liang.novel.plugin.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.util.NlsActions;
import com.liang.novel.plugin.icons.Icons;
import com.liang.novel.plugin.state.NovelState;
import com.liang.novel.plugin.ui.ReadUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class FontSizeDecreaseAction extends DumbAwareAction {

    private ReadUI readUI;

    public FontSizeDecreaseAction(ReadUI readUI) {
        this.readUI = readUI;

        getTemplatePresentation().setText("字号减小");
        getTemplatePresentation().setIcon(Icons.A_MINUS_ICON);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        NovelState instance = NovelState.getInstance();
        Integer fontSize = instance.getFontSize();
        if (fontSize > 12) {
            fontSize = fontSize - 1;
            instance.setFontSize(fontSize);
            readUI.setFont(new Font(instance.getFontType(), Font.PLAIN, fontSize));
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();

        NovelState instance = NovelState.getInstance();
        Boolean useCustomFont = instance.getUseCustomFont();
        if (useCustomFont) {
            Integer fontSize = instance.getFontSize();
            if (fontSize > 12) {
                presentation.setEnabled(true);
                presentation.setText("字号减少");
            }else{
                presentation.setEnabled(false);
                presentation.setText("已是最小字号");
            }
        }else{
            presentation.setEnabled(false);
            presentation.setText("请在setting中开启字体");
        }
    }

}
