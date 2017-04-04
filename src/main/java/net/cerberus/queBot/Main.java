package net.cerberus.queBot;


import net.cerberus.queBot.common.QueStatus;
import net.cerberus.queBot.events.MessageListener;
import net.cerberus.queBot.io.LogLevel;
import net.cerberus.queBot.io.LogReason;
import net.cerberus.queBot.io.Logger;
import net.cerberus.queBot.io.SoundManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.commons.lang3.StringUtils;

import javax.security.auth.login.LoginException;
import java.util.Random;

public class Main {

    static private JDA jda;
    static private String botId = "258012836599562240";
    static private String channelId = "255421006984904705";
    static private String guildId = "225097021671866368";
    static private SoundManager soundManager;
    static private QueStatus queStatus;

    public static void main(String[] args){
        try {
            queStatus = QueStatus.NOT_JOINED;
            soundManager = new SoundManager();
            soundManager.initialize();
            jda = new JDABuilder(AccountType.CLIENT).setToken("").buildBlocking();
            jda.addEventListener(new MessageListener());
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            e.printStackTrace();
        }

        /* Checks if the que has started and if the user has already joined it. */
        jda.getUserById(botId).getPrivateChannel().sendMessage("!q").complete();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(getQueStatus().equals(QueStatus.NOT_JOINED)) {
            if (checkForCurrentDrops()) {
                Logger.logMessage("Joining que.", LogLevel.INFO, LogReason.QUE_JOIN);

                MessageHistory messageHistory = jda.getTextChannelById(channelId).getHistory();
                for (Message message : messageHistory.retrievePast(50).complete()) {
                    if (message.getRawContent().contains("A drop is starting")) {
                        String joinMessage = StringUtils.substringAfter(message.getRawContent(), "!join").replaceAll("`", "");
                        jda.getUserById(Main.getBotId()).getPrivateChannel().sendMessage("!join" + joinMessage).queue();
                        Logger.logMessage("New drop is starting!", LogLevel.INFO, LogReason.QUE_JOIN);
                        break;
                    }
                }
            } else {
                Logger.logMessage("No drops are currently running!", LogLevel.INFO, LogReason.QUE_UPDATE);
            }
        }
        new Thread(() -> {
            try {
                Thread.sleep(1000 * 180);

                while (true) {
                    if(queStatus.equals(QueStatus.JOINED)){
                        queCheck();
                    }
                    Thread.sleep(1000 * 600 + new Random().nextInt(5000));
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }).start();
    }

    public static QueStatus getQueStatus() {
        return queStatus;
    }

    public static void setQueStatus(QueStatus queStatus) {
        Main.queStatus = queStatus;
    }

    private static boolean checkForCurrentDrops(){
        MessageHistory messageHistory = jda.getTextChannelById(channelId).getHistory();
        for (Message message : messageHistory.retrievePast(50).complete()) {
            if(message.getRawContent().contains("The drop queue has closed! ")){
                return false;
            }
            if(message.getRawContent().contains("A drop is starting")){
                return true;
            }
        }
        return false;
    }

    private static void queCheck(){
        User botUser = jda.getUserById(botId);
        if(jda.getGuildById(guildId).getMember(botUser).getOnlineStatus().equals(OnlineStatus.ONLINE)) {
            botUser.getPrivateChannel().sendMessage("!q").queue();
        }else{
            Logger.logMessage("DropBot is offline!", LogLevel.WARNING, LogReason.QUE_UPDATE);
        }
    }

    public static String getBotId() {
        return botId;
    }

    public static String getChannelId() {
        return channelId;
    }

    public static SoundManager getSoundManager() {
        return soundManager;
    }
}
