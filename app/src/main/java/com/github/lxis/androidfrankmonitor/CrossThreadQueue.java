package com.frankui.frankmonitor;

import android.support.annotation.NonNull;

import com.frankui.frankmonitor.data.EventInfo;
import com.frankui.frankmonitor.data.StackInfo;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class CrossThreadQueue {

    @NonNull
    private final BlockingDeque<EventInfo> eventInfoPool = new LinkedBlockingDeque<>();
    @NonNull
    private final BlockingDeque<EventInfo> eventQueue = new LinkedBlockingDeque<>();
    @NonNull
    private final BlockingDeque<StackInfo> stackQueue = new LinkedBlockingDeque<>();
    @NonNull
    private final BlockingDeque<Long> frameQueue = new LinkedBlockingDeque<>();
    public volatile long mainThreadFinishTime;

    @NonNull
    public BlockingDeque<StackInfo> getStackQueue() {
        return stackQueue;
    }

    @NonNull
    public EventInfo getEventFromPool() {
        EventInfo event = eventInfoPool.pollFirst();
        if (event == null) {
            event = new EventInfo();
        }
        return event;
    }

    public void addEvent(EventInfo event) {
        eventQueue.add(event);
    }

    public EventInfo takeEvent() {
        EventInfo event = takeInternal();
        if (event == null) {
            return null;
        }
        EventInfo cloneEvent = event.cloneEvent();
        returnEvent(event);
        return cloneEvent;
    }

    private void returnEvent(EventInfo event) {
        if (event == null) {
            return;
        }
        event.clear();
        eventInfoPool.add(event);
    }

    private EventInfo takeInternal() {
        try {
            return eventQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public EventInfo pollFirstEvent() {
        return eventQueue.pollFirst();
    }

    public Long takeFrame() {
        return takeFrameInternal();
    }

    private Long takeFrameInternal() {
        try {
            return frameQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addFrame(long frame) {
        frameQueue.add(frame);
    }

    public boolean isEventQueueEmpty() {
        return frameQueue.isEmpty();
    }
}
