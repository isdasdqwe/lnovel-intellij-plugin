package com.liang.novel.plugin;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;

public class NovelPluginManager {

    public static final String PLUGIN_ID = "com.liang.novel.lnovel-intellij-plugin";

    public static String currentVersion() {
        IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID));
        return plugin.getVersion();
    }
}
