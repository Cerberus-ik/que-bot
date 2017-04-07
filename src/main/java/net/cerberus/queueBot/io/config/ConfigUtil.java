package net.cerberus.queueBot.io.config;


import net.cerberus.queueBot.Main;
import net.cerberus.queueBot.io.*;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class ConfigUtil {


    public static Config loadConfig(){
        File configFile = new File("config.json");
        String token;
        long waitingTime;
        long waitingFuzzyTime;
        String guildId;
        String channelId;
        String botId;

        if(!configFile.exists()){
            Logger.logMessage("Creating config file.", LogLevel.INFO, LogReason.CONFIG);
            setupNewConfig();
        }
        try {
            JSONObject jsonObject = new JSONObject(FileUtils.readFileToString(configFile, "UTF-8"));

            token = jsonObject.getString("token").replaceAll("\"", "");
            waitingTime = jsonObject.getLong("waitingTime");
            waitingFuzzyTime = jsonObject.getLong("waitingFuzzyTime");
            guildId = jsonObject.getJSONObject("guildSettings").getString("guildId");
            channelId = jsonObject.getJSONObject("guildSettings").getString("channelId");
            botId = jsonObject.getJSONObject("guildSettings").getString("botId");

            return new Config(token, waitingTime, waitingFuzzyTime, guildId, channelId, botId);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logMessage("An error occurred while reading the config file.", LogLevel.ERROR, LogReason.CONFIG);
            Main.shutdown();
        }
        return null;
    }

    private static void setupNewConfig(){
        ResourceLoader resourceLoader = new ResourceLoader();
        String defaultConfigContent = resourceLoader.getResourceFileContent("config/config.json");
        File buildOrderFile = new File("config.json");

        try {
            String beatified = JsonBeautifier.beatifyJsonObject(new JSONObject(defaultConfigContent));
            FileUtils.writeStringToFile(buildOrderFile, beatified, Charset.defaultCharset());
            Logger.logMessage("Created the config file.", LogLevel.INFO, LogReason.CONFIG);
        } catch (IOException e) {
            Logger.logMessage("An error occurred while creating the config.json.", LogLevel.ERROR, LogReason.CONFIG);
            Main.shutdown();
        }
    }
}
