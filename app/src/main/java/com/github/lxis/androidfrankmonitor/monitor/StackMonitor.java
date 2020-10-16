package com.frankui.frankmonitor.monitor;

import android.os.Looper;
import android.support.annotation.NonNull;

import com.frankui.frankmonitor.CrossThreadQueue;
import com.frankui.frankmonitor.data.StackInfo;

public class StackMonitor {

    @NonNull
    private final CrossThreadQueue mQueue;

    public StackMonitor(@NonNull CrossThreadQueue queue) {
        mQueue = queue;
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                stack();
            }
        }).start();
    }

    // TODO: 优化逻辑
    private void stack() {
        while (true) {
            if (mQueue.mainThreadFinishTime == 0 || System.nanoTime() - mQueue.mainThreadFinishTime < 300000) {
                StackTraceElement[] stack = Looper.getMainLooper().getThread().getStackTrace();
                StackInfo stackInfo = new StackInfo();
                stackInfo.stack = stack;
                stackInfo.time = System.nanoTime();
                mQueue.getStackQueue().add(stackInfo);
                if (mQueue.getStackQueue().size() > 10000) {
                    mQueue.getStackQueue().pollFirst();
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
