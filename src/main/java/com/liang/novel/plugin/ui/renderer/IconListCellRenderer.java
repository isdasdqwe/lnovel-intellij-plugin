package com.liang.novel.plugin.ui.renderer;

import com.liang.novel.plugin.pojo.Chapter;

import javax.swing.*;
import java.awt.*;

/**
 * 重新绘制的时候会调用getListCellRendererComponent
 * 当列表获得焦点、失去焦点、鼠标移到列表上方等与外观有关的事件发生时，可能会引发重新绘制。
 */
public class IconListCellRenderer extends DefaultListCellRenderer {

    private Icon icon;

    public IconListCellRenderer(Icon icon) {
        this.icon = icon;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Chapter chapter) {
            if (chapter.getBookmark()) {
                //设置文本和图标
                label.setIcon(icon);
                //可选，将图标放在文本右边
                label.setHorizontalTextPosition(SwingConstants.RIGHT);
            }
        }
        return label;
    }
}
