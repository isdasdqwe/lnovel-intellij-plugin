package com.liang.novel.plugin.ui.model;

import com.liang.novel.plugin.pojo.Book;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * 表格模型类
 */
public class BookTableModel extends AbstractTableModel {

    private List<Book> dataList;

    public BookTableModel(List<Book> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getRowCount() {
        return dataList.size();
    }

    @Override
    public int getColumnCount() {
        return 2; // 两列：书名和路径
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book data = dataList.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> data.getBookName();
            case 1 -> data.getUrl();
            default -> null;
        };
    }

    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case 0 -> "书名";
            case 1 -> "路径";
            default -> null;
        };
    }

}
