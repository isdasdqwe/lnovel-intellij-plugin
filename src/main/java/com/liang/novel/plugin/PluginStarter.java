package com.liang.novel.plugin;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.liang.novel.plugin.notifications.NovelNotification;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class PluginStarter implements StartupActivity, DumbAware {

    @Override
    public void runActivity(@NotNull Project project) {
        String version = PropertiesComponent.getInstance().getValue(NovelPluginManager.PLUGIN_ID);
        //空的就是第一次
        if (StringUtils.isEmpty(version)) {
            PropertiesComponent.getInstance().setValue(NovelPluginManager.PLUGIN_ID, NovelPluginManager.currentVersion());
            NovelNotification.notifyWelcome(project);
        }
    }

}
