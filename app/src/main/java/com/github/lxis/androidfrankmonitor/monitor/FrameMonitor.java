package com.frankui.frankmonitor.monitor;

import android.support.annotation.NonNull;
import android.view.Choreographer;

import com.frankui.frankmonitor.CrossThreadQueue;

public class FrameMonitor {
    @NonNull
    private final CrossThreadQueue mQueue;

    public FrameMonitor(@NonNull CrossThreadQueue queue) {
        mQueue = queue;
    }

    public void start() {
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                mQueue.addFrame(frameTimeNanos);
                Choreographer.getInstance().postFrameCallback(this);
            }
        });
    }
}
