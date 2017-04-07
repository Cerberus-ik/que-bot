package net.cerberus.queueBot.io.config;

public class Config {

    private String token;
    private long waitingTime;
    private long waitingFuzzyTime;
    private String guildId;
    private String channelId;
    private String botId;

    public Config(String token, long waitingTime, long waitingFuzzyTime, String guildId, String channelId, String botId) {
        this.token = token;
        this.waitingTime = waitingTime;
        this.waitingFuzzyTime = waitingFuzzyTime;
        this.guildId = guildId;
        this.channelId = channelId;
        this.botId = botId;
    }

    public String getToken() {
        return token;
    }

    public long getWaitingTime() {
        return waitingTime;
    }

    public long getWaitingFuzzyTime() {
        return waitingFuzzyTime;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getBotId() {
        return botId;
    }
}
