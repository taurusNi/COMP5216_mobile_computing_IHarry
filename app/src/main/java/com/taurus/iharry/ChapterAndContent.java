package com.taurus.iharry;

/**
 * Created by taurus on 16/10/7.
 */
public class ChapterAndContent {
    private String chapter;
    private String content;

    public ChapterAndContent(String chapter, String content) {
        this.chapter = chapter;
        this.content = content;
    }

    public String getChapter() {
        return chapter;
    }

    public String getContent() {
        return content;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
