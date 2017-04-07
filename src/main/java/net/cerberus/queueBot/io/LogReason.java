package net.cerberus.queueBot.io;


public enum LogReason {

    BOT("Bot"),
    CONFIG("Config"),
    AFK_CHECK("AFK-Check"),
    QUEUE_UPDATE("Queue-Update"),
    QUEUE_JOIN("Queue-Join");

    private String s;

    LogReason(String s) {
        this.s = s;
    }

    public String getName() {
        return s;
    }
}
