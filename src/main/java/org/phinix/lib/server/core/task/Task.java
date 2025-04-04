package org.phinix.lib.server.core.task;

import org.phinix.lib.server.core.Manageable;

public abstract class Task<M extends Manageable> {
    protected static final long PAUSE_DELAY_MILLIS = 100;

    protected Thread threadTask;
    protected boolean running;
    protected boolean paused;

    public Task() {
        this.running = false;
        this.paused = false;
    }

    public abstract void process(M manageable);
    protected abstract void executeAsync(M manageable);

    protected void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void pauseDelay() {
        delay(PAUSE_DELAY_MILLIS);
    }

    public void start(M serverContext) {
        if (!running) {
            threadTask = new Thread(() -> this.executeAsync(serverContext));
            threadTask.start();
        }
    }

    public void pause() {
        paused = true;
    }

    public void resumeTask() {
        paused = false;
    }

    public void stop() {
        running = false;
        if (threadTask != null) {
            threadTask.interrupt();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public String getName() {
        return threadTask.getName();
    }
}