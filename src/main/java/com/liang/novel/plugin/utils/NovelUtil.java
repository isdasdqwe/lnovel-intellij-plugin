package com.liang.novel.plugin.utils;

import com.intellij.openapi.ui.Messages;
import com.liang.novel.plugin.pojo.Chapter;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NovelUtil {

    private static final String CHAPTER_TITLE_REG = "^(正文)?第[零〇一二三四五六七八九十百千万a-zA-Z0-9]{1,7}[章节卷集部篇回]\\s*(.+?)(\\r\\n|\\r|\\n|$)";
    private static final Pattern CHAPTER_TITLE_PATTERN = Pattern.compile(CHAPTER_TITLE_REG);

    public static List<Chapter> parseNovelChapters(String url) {
        if (StringUtils.isEmpty(url)) {
            return Collections.emptyList();
        }

        List<Chapter> chapterList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(url), StandardCharsets.UTF_8))) {
            String line;
            int index = 0;
            int currentLineNumber = 1;
            while ((line = reader.readLine()) != null) {
                Chapter chapter = parseNovelChapter(line, index, currentLineNumber);
                if (chapter != null) {
                    chapterList.add(chapter);
                    index++;
                }
                currentLineNumber++;
            }
        } catch (IOException e) {
            Messages.showErrorDialog("该路径下文件不存在：" + url, "出错了");
        }
        return chapterList;
    }

    private static Chapter parseNovelChapter(String line, int index, int currentLineNumber) {
        Matcher matcher = CHAPTER_TITLE_PATTERN.matcher(line);
        if (matcher.find()) {
            String title = matcher.group();

            Chapter chapter = new Chapter();
            chapter.setIndex(index);
            chapter.setTitle(title.trim());
            chapter.setRow(currentLineNumber);
            return chapter;
        }
        return null;
    }

    public static String getChapterContent(String url, Integer chapterRow) {
        if (url.isEmpty()) {
            return "";
        }
        if (chapterRow == null) {
            chapterRow = 1;
        }
        StringBuilder contentBuilder = new StringBuilder();
        String indent = "    "; //四个空格作为缩进
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(url), StandardCharsets.UTF_8));) {
            String line;
            int currentLineNumber = 1;
            while ((line = reader.readLine()) != null) {
                if (currentLineNumber > chapterRow) {
                    Matcher matcher = CHAPTER_TITLE_PATTERN.matcher(line);
                    if (matcher.find()) {
                        return contentBuilder.toString();
                    }
                    if (StringUtils.isNotBlank(line)) {
                        line = indent + line.trim();
                        contentBuilder.append(line).append("\n");
                    }
                }
                currentLineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}
