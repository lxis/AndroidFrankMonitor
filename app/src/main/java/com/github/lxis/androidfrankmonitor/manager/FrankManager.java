package com.frankui.frankmonitor.manager;

import android.support.annotation.NonNull;

import com.frankui.frankmonitor.CrossThreadQueue;
import com.frankui.frankmonitor.util.LogUtil;
import com.frankui.frankmonitor.MonitorConfig;

import java.util.ArrayList;
import java.util.List;

public class FrankManager {

    @NonNull
    private final MonitorConfig mConfig;
    @NonNull
    private final CrossThreadQueue mQueue;
    @NonNull
    private final StackManager mStackManager;
    @NonNull
    private final FrameManager mFrameManager;
    @NonNull
    private final EventManager mEventManager;

    public FrankManager(@NonNull CrossThreadQueue queue, @NonNull MonitorConfig config) {
        mQueue = queue;
        mConfig = config;
        mStackManager = new StackManager(mQueue, mConfig);
        mFrameManager = new FrameManager(mQueue, mConfig);
        mEventManager = new EventManager(mQueue, mConfig, mStackManager);
    }

    public void start() {
        Thread logThread = new Thread(new Runnable() {
            @Override
            public void run() {
                copy();
            }
        });
        logThread.start();
    }

    private void copy() {
        while (true) {
            List<String> logs = new ArrayList<>();
            mFrameManager.takeToInternalQueue();
            long diff = mFrameManager.getFrameDiff();
            if (diff != 0 && diff > mConfig.getJankThresholdNano()) {
                long frameEventStart = mFrameManager.getPreFrame();
                long frameEventEnd = mFrameManager.getFrameEventEnd();
                mEventManager.takeToInternalList();
                mStackManager.takeToInternalList();
                mFrameManager.logFrameToConsole(logs);
                mEventManager.logToConsole(frameEventStart, frameEventEnd, logs);
                mEventManager.clearEventInternalList(frameEventStart);
                mStackManager.clearStackInternalList(frameEventStart);
                mFrameManager.clear();
                logs.add("AB:=============================================");
                LogUtil.log(logs);
            }
        }
    }
}
