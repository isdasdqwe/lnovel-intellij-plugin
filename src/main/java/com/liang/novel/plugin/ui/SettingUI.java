package com.liang.novel.plugin.ui;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.*;
import com.intellij.ui.table.JBTable;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.liang.novel.plugin.ui.model.BookTableModel;
import com.liang.novel.plugin.state.NovelState;
import com.liang.novel.plugin.pojo.Book;
import com.liang.novel.plugin.utils.NovelUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SettingUI {

    public static final String EMPTY_MESSAGE = "书架中还没有书，快去添加吧";

    private JPanel contentPanel;
    private JComboBox<String> fontTypeComboBox;
    private JComboBox<Integer> fontSizeComboBox;
    private JPanel bookPanel;
    private JPanel readPanel;
    private JCheckBox fontCheckBox;
    private JTextField preChapterText;
    private JTextField nextChapterText;


    private NovelState novelState;
    private Boolean useCustomFont;
    private String fontType;
    private Integer fontSize;
    private List<Book> bookList;
    private Integer currentReadBookIndex;

    private ReadUI readUI;
    private JBTable jbTable;

    private GridConstraints gridConstraints;


    public SettingUI() {
        this.novelState = NovelState.getInstance();
        this.useCustomFont = novelState.getUseCustomFont();
        this.fontType = novelState.getFontType();
        this.fontSize = novelState.getFontSize();
        this.bookList = novelState.getBookList();
        this.currentReadBookIndex = novelState.getCurrentReadBookIndex();

        gridConstraints = new GridConstraints();
        gridConstraints.setFill(GridConstraints.FILL_HORIZONTAL); //水平占满

        //初始化面板
        initContentPanel();
    }

    /**
     * 初始化面板（得先初始化readUI，别的面板要用）
     */
    private void initContentPanel() {
        //初始化阅读面板
        initReadPanel();
        //初始化书架面板
        initBookPanel();
        //初始化字体
        intFont();
    }

    @SuppressWarnings("unchecked")
    private void intFont() {
        if (useCustomFont) {
            fontCheckBox.setSelected(true);
            fontTypeComboBox.setEnabled(true);
            fontSizeComboBox.setEnabled(true);
        }

        //初始化字体类型
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        List<String> fontFamilyList = Arrays.stream(fonts).map(Font::getFamily).distinct().toList(); //去重
        DefaultComboBoxModel<String> fontTypeComboBoxModel = new DefaultComboBoxModel<>();
        for (String fontFamily : fontFamilyList) {
            fontTypeComboBoxModel.addElement(fontFamily);
        }
        fontTypeComboBox.setModel(fontTypeComboBoxModel);
        fontTypeComboBox.setSelectedItem(fontType);

        //初始化字体字号
        DefaultComboBoxModel<Integer> fontSizeComboBoxModel = new DefaultComboBoxModel<>();
        for (int i = 8; i <= 24; i++) {
            fontSizeComboBoxModel.addElement(i);
        }
        fontSizeComboBox.setModel(fontSizeComboBoxModel);
        fontSizeComboBox.setSelectedItem(fontSize);

        //监听是否启用自定义字体
        fontCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                fontTypeComboBox.setEnabled(true);
                fontSizeComboBox.setEnabled(true);
                //重设字体
                readUI.setFont(new Font(fontType, Font.PLAIN, fontSize));
            } else {
                fontTypeComboBox.setEnabled(false);
                fontSizeComboBox.setEnabled(false);
                //重设为平台字体
                readUI.setFont(null);
            }
        });
        //字体类型监听器
        fontTypeComboBox.addActionListener(e -> {
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
            //重设字体类型
            this.fontType = (String) cb.getSelectedItem();
            readUI.setFont(new Font(fontType, Font.PLAIN, getFontSize()));
        });
        //字体字号监听器
        fontSizeComboBox.addActionListener(e -> {
            JComboBox<Integer> cb = (JComboBox<Integer>) e.getSource();
            //重设字体字号
            this.fontSize = (Integer) cb.getSelectedItem();
            readUI.setFont(new Font(getFontType(), Font.PLAIN, fontSize));
        });
    }

    private void initBookPanel() {
        jbTable = new JBTable();
        //添加数据
        jbTable.setModel(new BookTableModel(bookList));
        //设置空数据时的提示语
        if (bookList.isEmpty()) {
            jbTable.getEmptyText().setText(EMPTY_MESSAGE);
        }
        //设置行高
        jbTable.setRowHeight(25);
        //设置行数
        jbTable.setVisibleRowCount(5);
        //设置选择模式为单选
        jbTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //设置默认选中的图书
        if (currentReadBookIndex != null) {
            jbTable.setRowSelectionInterval(currentReadBookIndex, currentReadBookIndex);
        }
        //监听表格的选择
        jbTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                //获取选中的行索引
                int selectedRow = jbTable.getSelectedRow();
                if (selectedRow != -1) {
                    //修改当前正在读的图书
                    currentReadBookIndex = selectedRow;
                    //持久化
                    novelState.setCurrentReadBookIndex(currentReadBookIndex);
                    //异步刷新章节列表
                    readUI.refreshChapterListAsync(bookList.get(selectedRow));
                } else {
                    //取消当前在读
                    novelState.setCurrentReadBookIndex(null);
                }
            }
        });

        //创建工具栏装饰器
        ToolbarDecorator toolbarDecorator = createToolbarDecorator();
        //添加到firstPanel内
        bookPanel.add(toolbarDecorator.createPanel(), gridConstraints);
    }

    private ToolbarDecorator createToolbarDecorator() {
        return ToolbarDecorator.createDecorator(jbTable)
                .setAddAction(anActionButton -> { //添加
                    //文件选择器
                    FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
                    descriptor.withFileFilter(file -> file.getName().toLowerCase().endsWith(".txt")); //只要txt
                    VirtualFile virtualFile = FileChooser.chooseFile(descriptor, null, null);
                    //检查用户是否选择了文件
                    if (virtualFile != null) {
                        //显示进度条
                        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
                            ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
                            progressIndicator.setIndeterminate(true); //设置进度条为不确定状态

                            //获取文件名称
                            String fileName = virtualFile.getNameWithoutExtension();
                            String path = virtualFile.getPath();

                            Book book = new Book();
                            book.setBookName(fileName);
                            book.setUrl(path);
                            book.setChapterList(NovelUtil.parseNovelChapters(path));
                            bookList.add(book);

                            doAfterAction(bookList.size() - 1, true);

                        }, "正在添加图书", true, null); // 设置进度条标题
                    }
                })
                .setRemoveAction(anActionButton -> { //移除
                    int selectedRow = jbTable.getSelectedRow();
                    bookList.remove(selectedRow);
                    if (bookList.isEmpty()) {
                        jbTable.getEmptyText().setText(EMPTY_MESSAGE);
                    }
                    currentReadBookIndex = null;
                    //清空当前章节内容
                    readUI.clearContentText();
                    //清空当前章节列表
                    readUI.clearChapterList();

                    doAfterAction(bookList.size() - 1, true);
                })
                .setEditAction(anActionButton -> { //编辑
                    int selectedRow = jbTable.getSelectedRow();
                    Book book = bookList.get(selectedRow);

                    EditDialogUI editDialogUI = new EditDialogUI();
                    editDialogUI.setBookName(book.getBookName());
                    editDialogUI.setUrl(book.getUrl());
                    boolean isOk = editDialogUI.showAndGet();
                    if (isOk) {
                        boolean updateBookName = !editDialogUI.getBookName().equals(book.getBookName());
                        boolean updateBookUrl = !editDialogUI.getUrl().equals(book.getUrl());
                        //如果修改了书名
                        if (updateBookName) {
                            book.setBookName(editDialogUI.getBookName());
                        }
                        //如果修改了路径
                        if (updateBookUrl) {
                            book.setUrl(editDialogUI.getUrl());
                            //开启进度条
                            ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
                                book.setChapterList(NovelUtil.parseNovelChapters(editDialogUI.getUrl()));
                            }, "正在解析图书", false, null);
                        }
                        if (updateBookName || updateBookUrl) {
                            //替换bookList中对应的book
                            bookList.set(selectedRow, book);
                            //如果updateBookUrl了，就刷新table
                            doAfterAction(selectedRow, updateBookUrl);
                        }
                    }
                })
                .setMoveUpAction(anActionButton -> { //上移
                    int curBookIndex = jbTable.getSelectedRow();
                    if (curBookIndex > 0) {
                        int lastBookIndex = curBookIndex - 1;
                        //交换索引顺序
                        Collections.swap(bookList, curBookIndex, lastBookIndex);

                        doAfterAction(lastBookIndex, false);
                    }
                })
                .setMoveDownAction(anActionButton -> { //下移
                    int curBookIndex = jbTable.getSelectedRow();
                    if (curBookIndex < bookList.size() - 1) {
                        int nextBookIndex = curBookIndex + 1;
                        //交换索引顺序
                        Collections.swap(bookList, curBookIndex, nextBookIndex);

                        doAfterAction(nextBookIndex, false);
                    }
                });
    }

    private void doAfterAction(Integer selectIndex, Boolean refreshTable) {
        //持久化
        novelState.setBookList(bookList);
        //更新表格数据
        if (refreshTable) {
            jbTable.setModel(new BookTableModel(bookList));
        }
        //选中变更后的自己
        if (selectIndex != -1) {
            jbTable.setRowSelectionInterval(selectIndex, selectIndex);
        }
    }

    private void initReadPanel() {
        Book book = currentReadBookIndex != null ? bookList.get(currentReadBookIndex) : null;
        Font font = null;
        if (useCustomFont) {
            font = new Font(fontType, Font.PLAIN, fontSize);
        }
        readUI = new ReadUI(book, font, true);
        readUI.getContentPanel().setPreferredSize(new Dimension(0, 250));
        readPanel.add(readUI.getContentPanel(), gridConstraints);
    }


    public JPanel getContentPanel() {
        return contentPanel;
    }

    public Boolean getUseCustomFont() {
        return fontCheckBox.isSelected();
    }

    public String getFontType() {
        return fontTypeComboBox.getSelectedItem().toString();
    }

    public void setFontType(String fontType) {
        fontTypeComboBox.setSelectedItem(fontType);
    }

    public Integer getFontSize() {
        return (Integer) fontSizeComboBox.getSelectedItem();
    }

    public void setFontSize(Integer fontSize) {
        fontSizeComboBox.setSelectedItem(fontSize);
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
        contentPanel.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        final Spacer spacer1 = new Spacer();
        contentPanel.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("字体:");
        panel2.add(label1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fontTypeComboBox = new JComboBox();
        fontTypeComboBox.setEnabled(false);
        panel2.add(fontTypeComboBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("字号:");
        panel2.add(label2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fontSizeComboBox = new JComboBox();
        fontSizeComboBox.setEnabled(false);
        panel2.add(fontSizeComboBox, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fontCheckBox = new JCheckBox();
        fontCheckBox.setText("");
        panel2.add(fontCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("上一章:");
        panel3.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        preChapterText = new JTextField();
        preChapterText.setEnabled(false);
        preChapterText.setText("Alt + ↑");
        panel3.add(preChapterText, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("下一章:");
        panel3.add(label4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nextChapterText = new JTextField();
        nextChapterText.setEnabled(false);
        nextChapterText.setText("Alt + ↓");
        panel3.add(nextChapterText, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        bookPanel = new JPanel();
        bookPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPanel.add(bookPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        readPanel = new JPanel();
        readPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPanel.add(readPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPanel;
    }

}
