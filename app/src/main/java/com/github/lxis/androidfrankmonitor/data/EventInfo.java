package com.frankui.frankmonitor.data;

import android.support.annotation.NonNull;

public class EventInfo {
    public String startEvent;
    public long startTime;
    public String endEvent;
    public long endTime;

    public EventInfo cloneEvent() {
        EventInfo cloneItem = new EventInfo();
        cloneItem.startTime = this.startTime;
        cloneItem.endTime = this.endTime;
        cloneItem.startEvent = this.startEvent;
        cloneItem.endEvent = this.endEvent;
        return cloneItem;
    }

    public void clear() {
        this.startEvent = "clear";
        this.startTime = 0;
        this.endEvent = "clear";
        this.endTime = 0;
    }

    @NonNull
    @Override
    public String toString() {
        return  "startTime:" + startTime + ", startEvent:" + startEvent + ", endTime:" + endTime + ", endEvent:" + endEvent;
    }
}
