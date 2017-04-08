package net.cerberus.queueBot;


import net.cerberus.queueBot.bot.Bot;
import net.cerberus.queueBot.bot.BotBuilder;
import net.cerberus.queueBot.io.LogLevel;
import net.cerberus.queueBot.io.LogReason;
import net.cerberus.queueBot.io.Logger;
import net.cerberus.queueBot.io.SoundManager;
import net.cerberus.queueBot.io.config.Config;
import net.cerberus.queueBot.io.config.ConfigUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

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
        checkForUpdate();
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

    /**
     * Will return the latest version.
     *
     * @return latest version.
     */
    public static void checkForUpdate() {
        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpGet httpGet = new HttpGet("https://raw.githubusercontent.com/Cerberus-ik/queue-bot/master/version.txt");
        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpGet);
            httpGet.releaseConnection();
            String result = IOUtils.toString(httpResponse.getEntity().getContent(), "UTF-8");
            String lines[] = result.split("\\r?\\n");
            int version = Integer.parseInt(lines[1].split("-")[0]);
            int botVersion = Integer.parseInt(bot.getBuildId().split("-")[0]);

            if (version > botVersion) {
                Logger.logMessage("A new Version is available: " + lines[0], LogLevel.WARNING, LogReason.BOT);
                Logger.logMessage("Updating is highly recommended!", LogLevel.WARNING, LogReason.BOT);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logMessage("An error occurred while checking for updates.", LogLevel.ERROR, LogReason.BOT);
        }
    }

    public static void shutdown(){
        Logger.logMessage("Shutting down...", LogLevel.INFO, LogReason.BOT);
        soundManager.shutdown();
        bot.shutdown();
    }
}
