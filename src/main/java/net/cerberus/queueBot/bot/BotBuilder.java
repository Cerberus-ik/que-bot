package net.cerberus.queueBot.bot;


import net.cerberus.queueBot.io.SoundManager;
import net.cerberus.queueBot.io.config.Config;

public class BotBuilder {

    private Config config;
    private SoundManager soundManager;

    public BotBuilder(){}

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setSoundManager(SoundManager soundManager) {
        this.soundManager = soundManager;
    }

    public Bot build(){
        return new Bot(config, soundManager);
    }
}
