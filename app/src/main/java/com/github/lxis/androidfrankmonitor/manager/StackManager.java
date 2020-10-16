package com.frankui.frankmonitor.manager;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.frankui.frankmonitor.CrossThreadQueue;
import com.frankui.frankmonitor.util.ListUtils;
import com.frankui.frankmonitor.MonitorConfig;
import com.frankui.frankmonitor.data.StackInfo;
import com.frankui.frankmonitor.data.TreeNode;

import java.util.ArrayList;
import java.util.List;

import static com.frankui.frankmonitor.util.LogUtil.getOutputTime;

// TODO:整体优化逻辑
public class StackManager {

    @NonNull
    private final CrossThreadQueue mQueue;
    @NonNull
    private final MonitorConfig mConfig;
    @NonNull
    private final List<StackInfo> mStacks = new ArrayList<>();

    public StackManager(@NonNull CrossThreadQueue queue, @NonNull MonitorConfig config) {
        mQueue = queue;
        mConfig = config;
    }

    void takeToInternalList() {
        StackInfo stack;
        do {
            stack = mQueue.getStackQueue().pollFirst();
            if (stack != null) {
                mStacks.add(stack);
            }
        } while (stack != null);
    }

    void addStackLog(long startTime, long endTime, @NonNull List<String> logs) {
        List<StackInfo> list = new ArrayList<>();
        for (int j = 0; j < mStacks.size(); j++) {
            StackInfo stack = mStacks.get(j);
            if (stack != null && stack.time < endTime && stack.time > startTime) {
                list.add(stack);
            }
        }
        TreeNode stackTree = generateStackTree(list);
        printTree(stackTree, logs);
    }

    private void printTree(TreeNode stackTree, List<String> logs) {
        long duration = stackTree.getEndTime() - stackTree.getTime();
        if (stackTree.getDuration() == 0) {
            return;
        }
        String logLevelSignal = duration > mConfig.getTreeFoldPrintThresholdNano() ? "A:" : "  ";
        StringBuilder treeLevelSignal = new StringBuilder();
        for (int i = 0; i < stackTree.getDepth(); i++) {
            treeLevelSignal.append("-");
        }
        boolean isPassThroughNode = stackTree.getParent() != null &&
                stackTree.getParent().getDuration() == stackTree.getDuration() &&
                stackTree.getChildren().size() == 1 &&
                stackTree.getChildren().get(0).getDuration() == stackTree.getDuration();
        String text = logLevelSignal + "|-" + treeLevelSignal + "Method:Time:" + getOutputTime(duration) + ", Stack:" + stackTree.getText();
        if (!isPassThroughNode && stackTree.getDuration() > mConfig.getTreePrintThresholdNano()) {
            logs.add(text);
        }
        for (int i = 0; i < stackTree.getChildren().size(); i++) {
            TreeNode child = stackTree.getChildren().get(i);
            printTree(child, logs);
        }
    }

    private TreeNode generateStackTree(@NonNull List<StackInfo> list) {
        TreeNode tree = new TreeNode();
        tree.setDepth(0);
        for (int i = 0; i < list.size(); i++) {
            StackInfo stackInfo = list.get(i);
            TreeNode parent = null;
            for (int j = 0; j < tree.getChildren().size(); j++) {
                TreeNode child = tree.getChildren().get(j);
                parent = findParent(child, stackInfo, 0);
                if (parent != null) {
                    break;
                }
            }
            if (parent == null) {
                parent = tree;
            }
            addStackLogToTree(parent, stackInfo);
        }
        return tree;
    }

    private void addStackLogToTree(@NonNull TreeNode parent, @NonNull StackInfo stackInfo) {
        if (parent.getDepth() == stackInfo.stack.length) {
            parent.setEndTime(stackInfo.time);
        }
        for (int i = stackInfo.stack.length - 1 - parent.getDepth(); i >= 0; i--) {
            TreeNode node = new TreeNode();
            node.setDepth(parent.getDepth() + 1);
            node.setText(stackInfo.stack[i].toString());
            node.setTime(stackInfo.time);
            node.setEndTime(stackInfo.time);
            parent.getChildren().add(node);
            node.setParent(parent);
            parent = node;
        }
        while (parent.getParent() != null) {
            parent.getParent().setEndTime(Math.max(parent.getEndTime(), parent.getParent().getEndTime()));
            if (parent.getParent().getTime() == 0) {
                parent.getParent().setTime(parent.getTime());
            }
            parent = parent.getParent();
        }
    }

    private TreeNode findParent(@NonNull TreeNode tree, @NonNull StackInfo treeStackInfo, int depth) {
        String currentTreeText = tree.getText();
        StackTraceElement currentItemStack = ListUtils.getItem(treeStackInfo.stack,
                treeStackInfo.stack.length - 1 - depth);
        if (currentTreeText == null || currentItemStack == null) {
            return null;
        }
        String currentItemText = currentItemStack.toString();
        boolean isCurrentMatch = TextUtils.equals(currentItemText, currentTreeText);
        if (!isCurrentMatch) {
            return null;
        }
        for (int i = 0; i < tree.getChildren().size(); i++) {
            TreeNode child = tree.getChildren().get(i);
            TreeNode parentFinded = findParent(child, treeStackInfo, depth + 1);
            if (parentFinded != null) {
                return parentFinded;
            }
        }
        return tree;
    }


    void clearStackInternalList(long frameEventStart) {
        for (int i = mStacks.size() - 1; i >= 0; i--) {
            StackInfo stack = mStacks.get(i);
            if (stack.time < frameEventStart) {
                mStacks.remove(i);
            }
        }
    }

}
