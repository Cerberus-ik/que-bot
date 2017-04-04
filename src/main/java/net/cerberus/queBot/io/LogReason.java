package net.cerberus.queBot.io;


public enum LogReason {

    CONFIG("Config"),
    AFK_CHECK("AFK-Check"),
    QUE_UPDATE("Que-Update"),
    QUE_JOIN("Que-Join"),
    QUE_JOINED("Que-Joined");

    private String s;

    LogReason(String s) {
        this.s = s;
    }

    public String getName() {
        return s;
    }
}
