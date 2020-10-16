package com.frankui.frankmonitor.monitor;

import android.support.annotation.NonNull;

import com.frankui.frankmonitor.CrossThreadQueue;
import com.frankui.frankmonitor.MonitorConfig;
import com.frankui.frankmonitor.manager.FrankManager;

public class FrankMonitor {


    public static FrankMonitor getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        static FrankMonitor instance = new FrankMonitor();
    }

    @NonNull
    private final CrossThreadQueue mQueue = new CrossThreadQueue();
    @NonNull
    private MonitorConfig mConfig = new MonitorConfig();
    @NonNull
    private final FrankManager mFrankManager = new FrankManager(mQueue, mConfig);
    @NonNull
    private final StackMonitor mStackMonitor = new StackMonitor(mQueue);
    @NonNull
    private final FrameMonitor mFrameMonitor = new FrameMonitor(mQueue);
    @NonNull
    private final EventMonitor mEventMonitor = new EventMonitor(mQueue);

    private volatile boolean mHasInit = false;


    public static void install() {
        getInstance().installInternal();
    }


    private void installInternal() {
        if (mHasInit) {
            return;
        }
        synchronized (FrankMonitor.this) {
            if (mHasInit) {
                return;
            }
            mHasInit = true;
            mStackMonitor.start();
            mFrameMonitor.start();
            mEventMonitor.start();
            mFrankManager.start();
        }
    }

    @NonNull
    public static MonitorConfig getConfig() {
        return getInstance().getConfigInternal();
    }

    private MonitorConfig getConfigInternal() {
        return mConfig;
    }
}
