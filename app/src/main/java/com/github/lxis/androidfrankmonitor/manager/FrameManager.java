package com.frankui.frankmonitor.manager;

import android.support.annotation.NonNull;

import com.frankui.frankmonitor.CrossThreadQueue;
import com.frankui.frankmonitor.util.ListUtils;
import com.frankui.frankmonitor.util.LogUtil;
import com.frankui.frankmonitor.MonitorConfig;

import java.util.ArrayList;
import java.util.List;

public class FrameManager {

    // 不能取最后一个，因为Event等信息还没有返回。需要往前取几个。
    private final static int INDEX_OFFSET = 10;
    // 类似INDEX_OFFSET
    private final static int TIME_OFFSET = 20000000;

    @NonNull
    private final CrossThreadQueue mQueue;
    @NonNull
    private final List<Long> mFrames = new ArrayList<>();
    @NonNull
    private final MonitorConfig mConfig;

    public FrameManager(@NonNull CrossThreadQueue queue, @NonNull MonitorConfig config) {
        mQueue = queue;
        mConfig = config;
    }

    void takeToInternalQueue() {
        mFrames.add(mQueue.takeFrame());
    }

    public void clear() {
        if (mFrames.size() > 30) { // TODO：优化成一个更合理的数字
            ListUtils.remove(mFrames, 0);
        }
    }

    // 本次计算的Frame的开始
    long getPreFrame() {
        Long pre = ListUtils.getItem(mFrames, getPreFrameIndex());
        return pre == null ? 0 : pre;
    }

    // 本次计算的Frame的结束
    private long getCurrentFrame() {
        Long current = ListUtils.getItem(mFrames, getCurrentFrameIndex());
        return current == null ? 0 : current;
    }

    // 本次计算的Frame的event计算的结束
    long getFrameEventEnd() {
        return getCurrentFrame() + TIME_OFFSET; // 截止时间滞后20MS,因为Event返回比较慢
    }

    private int getPreFrameIndex() {
        return getCurrentFrameIndex() - 1;
    }

    private int getCurrentFrameIndex() {
        return mFrames.size() - 1 - INDEX_OFFSET;
    }

    public void logFrameToConsole(@NonNull List<String> logs) {
        long currentFrame = getCurrentFrame();
        long preFrame = getPreFrame();
        long diff = currentFrame - preFrame;
        logs.add("AB:Frame:diff:" + LogUtil.getOutputTime(diff) + ", pre:" + preFrame + ", current:" + currentFrame + ", end:" + getFrameEventEnd());
    }

    public long getFrameDiff() {
        // 取倒数第二个，event有时候返回的比较迟
        return getCurrentFrame() - getPreFrame();
    }
}
