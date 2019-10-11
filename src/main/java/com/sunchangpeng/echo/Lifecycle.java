package com.sunchangpeng.echo;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Lifecycle {
    private AtomicBoolean start = new AtomicBoolean(false);

    public void start() {
        if (start.compareAndSet(false, true)) {
            doStart();
        }
    }

    protected abstract void doStart();

    public void shutDown() {
        if (start.compareAndSet(true, false)) {
            doShutDown();
        }
    }

    protected abstract void doShutDown();
}
