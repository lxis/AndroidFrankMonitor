package com.frankui.frankmonitor.data;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    @NonNull
    private final List<TreeNode> mChildren = new ArrayList<>();

    private String text;
    private long time;
    private long endTime;
    private int depth;

    private TreeNode parent;

    @NonNull
    public List<TreeNode> getChildren() {
        return mChildren;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public long getDuration() {
        return endTime - time;
    }
}
