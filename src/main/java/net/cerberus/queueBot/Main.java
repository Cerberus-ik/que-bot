package net.cerberus.queueBot;


import net.cerberus.queueBot.bot.Bot;
import net.cerberus.queueBot.bot.BotBuilder;
import net.cerberus.queueBot.io.LogLevel;
import net.cerberus.queueBot.io.LogReason;
import net.cerberus.queueBot.io.Logger;
import net.cerberus.queueBot.io.SoundManager;
import net.cerberus.queueBot.io.config.Config;
import net.cerberus.queueBot.io.config.ConfigUtil;

public class Main {

    static private SoundManager soundManager;
    static private Config config;
    static private Bot bot;

    public static void main(String[] args){
        config = ConfigUtil.loadConfig();
        if(config == null){
            Logger.logMessage("Invalid config.json, delete the file to reset the config.", LogLevel.WARNING, LogReason.CONFIG);
            shutdown();
            return;
        }

        /* Initializing the sound system. */
        soundManager = new SoundManager();
        soundManager.initialize();

        /* Creates one instance of the bot. */
        BotBuilder botBuilder = new BotBuilder();
        botBuilder.setConfig(config);
        botBuilder.setSoundManager(soundManager);
        bot = botBuilder.build();
        bot.initialize();
        bot.checkCurrentQueueStatus();
    }

    public static SoundManager getSoundManager() {
        return soundManager;
    }

    public static Config getConfig() {
        return config;
    }

    public static Bot getBot() {
        return bot;
    }

    public static void shutdown(){
        Logger.logMessage("Shutting down...", LogLevel.INFO, LogReason.BOT);
        soundManager.shutdown();
        bot.shutdown();
    }
}
