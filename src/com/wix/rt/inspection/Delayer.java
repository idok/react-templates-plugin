package com.wix.rt.inspection;

import java.util.Date;

public class Delayer {
    private Date lastNotification;
    private final long timeOut;

    public Delayer(long timeOut) {
        this.timeOut = timeOut;
    }

    public void done() {
        lastNotification = new Date();
    }

    public boolean should() {
        Date now = new Date();
        long delta = now.getTime() - lastNotification.getTime();
        return delta > timeOut;
    }
}