package com.liang.novel.plugin.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import com.liang.novel.plugin.state.NovelState;
import com.liang.novel.plugin.ui.SettingUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class NovelSetting implements Configurable {

    private SettingUI settingUI;

    private NovelState novelState;

    public NovelSetting() {
        this.settingUI = new SettingUI();
        this.novelState = NovelState.getInstance();
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return null;
    }

    @Override
    public @Nullable JComponent createComponent() {
        return settingUI.getContentPanel();
    }

    @Override
    public boolean isModified() {
        return !settingUI.getUseCustomFont().equals(novelState.getUseCustomFont()) ||
                !settingUI.getFontType().equals(novelState.getFontType()) ||
                !settingUI.getFontSize().equals(novelState.getFontSize());
    }

    @Override
    public void apply() throws ConfigurationException {
        novelState.setUseCustomFont(settingUI.getUseCustomFont());
        novelState.setFontType(settingUI.getFontType());
        novelState.setFontSize(settingUI.getFontSize());

        //书籍相关直接保存，不用点apply
        //novelState.setBookList(settingUI.getBookList());
        //novelState.setCurrentReadBook(settingUI.getCurrentReadBook());
    }

}
