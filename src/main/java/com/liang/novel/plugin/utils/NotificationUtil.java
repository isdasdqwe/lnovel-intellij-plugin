package com.liang.novel.plugin.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;

public class NotificationUtil {

    public static void notify(String groupId, String content, Project project) {
        notify(groupId, null, content, project);
    }

    public static void notify(String groupId, String title, String content, Project project) {
        notify(groupId, title, content, NotificationType.INFORMATION, project);
    }

    public static void notify(String groupId, String title, String content, NotificationType notificationType, Project project) {
        Notification notification = null;
        if (StringUtils.isNotEmpty(title)) {
            notification = new Notification(groupId, title, content, notificationType);
        } else {
            notification = new Notification(groupId, content, notificationType);
        }
        notification.notify(project);
    }


}
