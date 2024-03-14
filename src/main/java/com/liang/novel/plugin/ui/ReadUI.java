package com.liang.novel.plugin.ui;

import com.intellij.ui.ListSpeedSearch;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.liang.novel.plugin.icons.Icons;
import com.liang.novel.plugin.pojo.Book;
import com.liang.novel.plugin.pojo.Chapter;
import com.liang.novel.plugin.ui.renderer.IconListCellRenderer;
import com.liang.novel.plugin.state.NovelState;
import com.liang.novel.plugin.utils.NovelUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReadUI {
    private JPanel contentPanel;

    private JComponent leftPanel;

    private JComponent rightPanel;

    private Book book;
    private Font font;
    private List<Chapter> dataList;

    private JTextArea contentTextArea;
    private DefaultListModel<Chapter> listModel;
    private JBList<Chapter> jbList;

    public ReadUI(Book book, Font font, Boolean isFullBoth) {
        this.book = book;
        this.font = font;
        this.dataList = book != null ? book.getChapterList() : Collections.emptyList();

        contentTextArea = new JTextArea();
        //分离器，左边章节列表，右边内容
        OnePixelSplitter splitter = new OnePixelSplitter(0.2f);
        leftPanel = initLeftPanel();
        splitter.setFirstComponent(leftPanel);
        rightPanel = initRightPanel();
        splitter.setSecondComponent(rightPanel);

        GridConstraints gridConstraints = new GridConstraints();
        if (isFullBoth) {
            gridConstraints.setFill(GridConstraints.FILL_BOTH);
        } else {
            gridConstraints.setFill(GridConstraints.FILL_HORIZONTAL);
        }
        contentPanel.add(splitter, gridConstraints);

        //快捷键监听，需要将焦点在内容面板
        contentTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isAltDown()) {
                    if (e.getKeyCode() == KeyEvent.VK_UP) {
                        up();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        down();
                    }
                }
            }
        });
    }

    private JComponent initLeftPanel() {
        listModel = new DefaultListModel<>();
        for (Chapter chapter : dataList) {
            listModel.addElement(chapter);
        }
        jbList = new JBList<>(listModel);

        //添加监听器，监听左边章节列表的选择事件
        jbList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { //确保事件只触发一次
                int selectedIndex = jbList.getSelectedIndex();
                if (selectedIndex != -1) {
                    //确保选中在当前页可见，滚动页面到选中那一条
                    jbList.ensureIndexIsVisible(selectedIndex);

                    //将章节内容放入右边内容面板
                    Chapter chapter = dataList.get(selectedIndex);
                    contentTextArea.setText(getChapterContent(chapter.getRow()));
                    //设置字体字号
                    if (font != null) {
                        contentTextArea.setFont(font);
                    }
                    //设置光标位置
                    contentTextArea.setCaretPosition(0);
                    //持久化（记录当前章节）
                    book.setChapterIndex(selectedIndex);
                    NovelState.getInstance().setNewBook(book);
                }
            }
        });

        //触发快速查找
        new ListSpeedSearch<>(jbList);
        //设置单元格的固定高度
        jbList.setFixedCellHeight(20);
        //设置可见行数
        jbList.setVisibleRowCount(10);
        //设置书签图标
        jbList.setCellRenderer(new IconListCellRenderer(Icons.BOOK_MARK_ICON));
        //默认选中
        if (book != null) {
            jbList.setSelectedIndex(book.getChapterIndex());
        }

        //创建工具栏装饰器
        ToolbarDecorator toolbarDecorator = createToolbarDecorator(jbList);
        return toolbarDecorator.createPanel();
    }

    private String getChapterContent(Integer chapterRow) {
        if (book != null) {
            return NovelUtil.getChapterContent(book.getUrl(), chapterRow);
        }
        return "";
    }

    private ToolbarDecorator createToolbarDecorator(JComponent jComponent) {
        return ToolbarDecorator.createDecorator(jComponent)
                .setRemoveAction(null)  //移除删除操作按钮
                .setMoveUpAction(anActionButton -> {
                    up();
                })
                .setMoveDownAction(anActionButton -> {
                    down();
                });
    }

    private JComponent initRightPanel() {
        contentTextArea.setLineWrap(true);
        return new JBScrollPane(contentTextArea);
    }

    private void up() {
        int selectedIndex = jbList.getSelectedIndex();
        if (selectedIndex > 0) {
            jbList.setSelectedIndex(selectedIndex - 1);
        }
    }

    private void down() {
        int selectedIndex = jbList.getSelectedIndex();
        if (selectedIndex < dataList.size() - 1) {
            jbList.setSelectedIndex(selectedIndex + 1);
        }
    }

    public void refreshChapterListAsync(Book book) {
        refreshChapterListAsync(book, false);
    }

    public void refreshChapterListAsync(Book book, Boolean onlyBookMarkChapter) {
        if (book != null) {
            this.book = book;
            if (onlyBookMarkChapter) {
                this.dataList = book.getChapterList().stream().filter(Chapter::getBookmark).collect(Collectors.toList());
            } else {
                this.dataList = book.getChapterList();
            }
            SwingUtilities.invokeLater(() -> doRefreshChapterList(onlyBookMarkChapter));
        }
    }

    public void refreshChapterListAsync(List<Chapter> chapterList) {
        refreshChapterListAsync(chapterList, false);
    }

    public void refreshChapterListAsync(List<Chapter> chapterList, Boolean onlyBookMarkChapter) {
        if (chapterList != null) {
            if (onlyBookMarkChapter) {
                this.dataList = chapterList.stream().filter(Chapter::getBookmark).collect(Collectors.toList());
            } else {
                this.dataList = chapterList;
            }
        }
        SwingUtilities.invokeLater(() -> doRefreshChapterList(onlyBookMarkChapter));
    }

    private void doRefreshChapterList(Boolean onlyBookMarkChapter) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                //更新数据模型
                listModel.clear();
                for (Chapter chapter : dataList) {
                    listModel.addElement(chapter);
                }
                return null;
            }

            @Override
            protected void done() {
                //在后台线程完成后，触发列表视图的重绘
                jbList.repaint();
                if (onlyBookMarkChapter) {
                    contentTextArea.setText("");
                } else {
                    jbList.setSelectedIndex(book.getChapterIndex());
                }
            }
        };
        worker.execute();
    }

    public void setFont(Font font) {
        if (font != null) {
            this.font = font;
        } else {
            this.font = new Font("Monospaced", Font.PLAIN, 12); //重设为平台字体
        }
        if (StringUtils.isNotEmpty(contentTextArea.getText())) {
            contentTextArea.setFont(this.font);
        }
    }

    public void clearChapterList() {
        listModel.clear();
    }

    public void clearContentText() {
        contentTextArea.setText("");
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public JComponent getLeftPanel() {
        return leftPanel;
    }

    public JBList<Chapter> getJbList() {
        return jbList;
    }

    public Book getBook() {
        return book;
    }

    public List<Chapter> getDataList() {
        return dataList;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPanel;
    }
}
