package com.example.android.camera2basic;

import android.graphics.Rect;

public class Segments {

    public static final int SCORE_FAILED = 0;
    public static final int SCORE_SUCCEED = 100;
    public static final String SOURCE = "device";


    private int top;
    private int left;
    private int bottom;
    private int right;
    private String name;
    private String value;
    private String source;

    private int score;


    public Segments(Rect rect) {
        top = rect.top;
        left = rect.left;
        bottom = rect.bottom;
        right = rect.right;
        source = Segments.SOURCE;
    }


    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
