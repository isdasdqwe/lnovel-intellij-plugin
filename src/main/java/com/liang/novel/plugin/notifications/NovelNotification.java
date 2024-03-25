package com.liang.novel.plugin.notifications;

import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.liang.novel.plugin.icons.Icons;
import org.intellij.lang.annotations.Language;

public class NovelNotification {

    @Language("HTML")
    private final static String WELCOME_MESSAGE = "<p>欢迎使用 <span>lnovel 插件</span></p>";

    public static final String GROUP_ID = "MyNotificationGroup";

    private static final String GIT_LINK = "http://git.zhiweidata.top/liangyuhang/lnovel-intellij-plugin";

    public static void notifyWelcome(Project project) {
        Notification notification = new Notification(GROUP_ID, WELCOME_MESSAGE, NotificationType.INFORMATION);
        addNotificationActions(notification, project);
        notification.setIcon(Icons.BOOK_ICON);
        notification.notify(project);
    }

    private static void addNotificationActions(Notification notification, Project project) {
        notification.addAction(NotificationAction.createSimple("Setting", () -> ShowSettingsUtil.getInstance().showSettingsDialog(project, "lnovel")));
        notification.addAction(NotificationAction.createSimple("Git", () -> BrowserUtil.browse(GIT_LINK)));
    }


}
