package org.phinix.lib.server.core.task;

public abstract class Task {

    protected static final long PAUSE_DELAY_MILLIS = 100;

    protected Thread threadTask;
    protected boolean running;
    protected boolean paused;

    public Task() {
        this.running = false;
        this.paused = false;
    }

    protected abstract void task();
    protected abstract void execute();

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

    public void start() {
        if (!running) {
            threadTask = new Thread(this::execute);
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
}