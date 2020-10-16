package com.frankui.frankmonitor.monitor;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Printer;

import com.frankui.frankmonitor.CrossThreadQueue;
import com.frankui.frankmonitor.data.EventInfo;

public class EventMonitor {


    public static final String START_PREFIX = ">>>>> Dispatching";
    public static final String FINISH_PREFIX = "<<<<< Finished to";

    @NonNull
    private final CrossThreadQueue mQueue;

    public EventMonitor(@NonNull CrossThreadQueue queue) {
        mQueue = queue;
    }

    private EventInfo mCurrentEvent;

    @NonNull
    private final Printer mPrinter = new Printer() {
        @Override
        public void println(String x) {
            long time = System.nanoTime();
            boolean isStart = x.contains(START_PREFIX);
            if (isStart) {
                mQueue.mainThreadFinishTime = 0;
                mCurrentEvent = mQueue.getEventFromPool();
                mCurrentEvent.startEvent = x;
                mCurrentEvent.startTime = time;
            } else {
                mQueue.mainThreadFinishTime = time;
                mCurrentEvent.endEvent = x;
                mCurrentEvent.endTime = time;
                mQueue.addEvent(mCurrentEvent);
            }

        }
    };

    public void start() {
        Looper.getMainLooper().setMessageLogging(mPrinter);
    }
}
