package com.frankui.frankmonitor.manager;

import android.support.annotation.NonNull;

import com.frankui.frankmonitor.CrossThreadQueue;
import com.frankui.frankmonitor.data.EventInfo;
import com.frankui.frankmonitor.util.ListUtils;
import com.frankui.frankmonitor.MonitorConfig;

import java.util.ArrayList;
import java.util.List;

import static com.frankui.frankmonitor.util.LogUtil.getOutputTime;

public class EventManager {

    @NonNull
    private final List<EventInfo> mEvents = new ArrayList<>();
    @NonNull
    private final MonitorConfig mConfig;
    @NonNull
    private final CrossThreadQueue mQueue;
    @NonNull
    private final StackManager mStackManager;

    public EventManager(@NonNull CrossThreadQueue queue, @NonNull MonitorConfig config, @NonNull StackManager stackManager) {
        mQueue = queue;
        mConfig = config;
        mStackManager = stackManager;
    }


    void clearEventInternalList(long frameEventStart) {
        for (int i = mEvents.size() - 1; i >= 0; i--) {
            EventInfo event = ListUtils.getItem(mEvents, i);
            if (event != null && event.endTime < frameEventStart) {
                ListUtils.remove(mEvents, i);
                break;
            }
        }
    }

    void takeToInternalList() {
        EventInfo event;
        do {
            event = mQueue.pollFirstEvent();
            if (event != null) {
                mEvents.add(event);
            }
        } while (event != null);
    }


    /**
     * log主方法，TODO：优化一下逻辑
     *
     * @param frameEventStart
     * @param frameEventEnd
     */
    void logToConsole(long frameEventStart, long frameEventEnd, @NonNull List<String> logs) {
        long timeSum = 0;
        for (int i = 0; i < mEvents.size(); i++) {
            EventInfo event = ListUtils.getItem(mEvents, i);
            if (event != null && event.startTime > frameEventStart && event.endTime < frameEventEnd) {
                timeSum += addEventLog(event, logs);
            }
        }
        logs.add("AB:TimeSum:" + getOutputTime(timeSum) + ",Start:" + frameEventStart + ",End:" + frameEventEnd);
    }

    private long addEventLog(EventInfo event, @NonNull List<String> logs) {
        if (event == null) {
            return 0;
        }
        long eventDuration = event.endTime - event.startTime;
        logs.add("AB:Event:" + getOutputTime(eventDuration) + "," + event.startEvent + ", Start:" + event.startTime + ", End:" + event.endTime);
        mStackManager.addStackLog(event.startTime, event.endTime, logs);
        return eventDuration;
    }

}
