package net.cerberus.queueBot.bot;


import net.cerberus.queueBot.Main;
import net.cerberus.queueBot.common.QueueStatus;
import net.cerberus.queueBot.io.LogLevel;
import net.cerberus.queueBot.io.LogReason;
import net.cerberus.queueBot.io.Logger;
import net.cerberus.queueBot.io.SoundManager;
import net.cerberus.queueBot.io.config.Config;
import net.cerberus.queueBot.listener.MessageListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.commons.lang3.StringUtils;

import javax.security.auth.login.LoginException;
import java.util.Random;

public class Bot {

    private Config config;
    private SoundManager soundManager;
    private BotStatus botStatus;
    private QueueStatus queueStatus;
    private JDA jda;

    Bot(Config config, SoundManager soundManager) {
        this.config = config;
        this.soundManager = soundManager;
        this.botStatus = BotStatus.NOT_INITIALIZED;
        this.queueStatus = QueueStatus.UNKNOWN;
    }

    /**
     * Initializes the bot, tries to log in with the given token.
     */
    public void initialize() {
        try {
            jda = new JDABuilder(AccountType.CLIENT).setToken(config.getToken()).addListener(new MessageListener()).buildBlocking();
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            e.printStackTrace();
            Logger.logMessage("Error while initializing the bot.", LogLevel.ERROR, LogReason.BOT);
            Main.shutdown();
        }

        while (!jda.getStatus().equals(JDA.Status.CONNECTED)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        botStatus = BotStatus.INITIALIZED;
    }

    /**
     * Checks if the queue has started and if the user has already joined it.
     * The bot waits if the other bot is offline.
     */
    private void requestUpdate() {
        /* Checks if the bot is online. */
        while (true) {
            if (!getBotOnlineStatus().equals(OnlineStatus.ONLINE)) {
                try {
                    Logger.logMessage("The bot is offline, waiting for it to get back online.", LogLevel.WARNING, LogReason.BOT);
                    Thread.sleep(1000 * 90);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Main.shutdown();
                }
            } else {
                break;
            }
        }
        /* Sends a message to the bot to check the current que status. */
        jda.getUserById(config.getBotId()).getPrivateChannel().sendMessage("!q").queue();
    }

    /**
     * The bot checks the current status of the que.
     */
    public void checkCurrentQueueStatus() {
        requestUpdate();

        for (int i = 0; i < 6; i++) {
            if (queueStatus.equals(QueueStatus.UNKNOWN)) {
                try {
                    Thread.sleep(100 + i * 500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
            if (i > 4) {
                Logger.logMessage("The bot is not responding.", LogLevel.WARNING, LogReason.QUEUE_UPDATE);
            }
        }

        if (queueStatus.equals(QueueStatus.NOT_JOINED_NOT_AVAILABLE)) {
            Logger.logMessage("No drops are currently running, waiting for one to start...", LogLevel.INFO, LogReason.QUEUE_UPDATE);
        } else if (queueStatus.equals(QueueStatus.JOINED)) {
            Logger.logMessage("You are already in the queue, waiting for the drop to start.", LogLevel.INFO, LogReason.QUEUE_UPDATE);
        } else if (queueStatus.equals(QueueStatus.NOT_JOINED_AVAILABLE)) {
            Logger.logMessage("Drop is running, joining que.", LogLevel.INFO, LogReason.QUEUE_UPDATE);
            joinRunningQueue();
        } else if (queueStatus.equals(QueueStatus.UNKNOWN)) {
            Logger.logMessage("Failed to retrieve que status.", LogLevel.WARNING, LogReason.QUEUE_UPDATE);
        }
    }

    /**
     * The bot joins tries to join a running que.
     * It will retrieve the latest 50 messages from the drop channel.
     * If the bot finds no "starting que" message the bot waits for the next drop.
     */
    private void joinRunningQueue() {
        MessageHistory messageHistory = jda.getTextChannelById(config.getChannelId()).getHistory();
        for (Message message : messageHistory.retrievePast(50).complete()) {
            if (message.getRawContent().contains("A drop is starting")) {
                String joinMessage = StringUtils.substringAfter(message.getRawContent(), "!join").replaceAll("`", "");
                waitBeforeAction();
                jda.getUserById(config.getBotId()).getPrivateChannel().sendMessage("!join" + joinMessage).queue();
                Logger.logMessage("Joined a running drop, expect longer queue times!", LogLevel.INFO, LogReason.QUEUE_JOIN);
                waitBeforeAction();
                jda.getUserById(config.getBotId()).getPrivateChannel().sendMessage("!q").queue();
                soundManager.alert();
                return;
            }
        }
        Logger.logMessage("The bot found no fitting drop start messages, waiting for the next drop...", LogLevel.WARNING, LogReason.QUEUE_UPDATE);
    }

    /**
     * Checks if the bot is currently online.
     *
     * @return the online status of the bot.
     */
    private OnlineStatus getBotOnlineStatus() {
        jda.getGuildById(config.getGuildId());
        jda.getTextChannelById(config.getChannelId());
        return jda.getGuildById(config.getGuildId()).getMember(jda.getUserById(config.getBotId())).getOnlineStatus();
    }

    /**
     * Sets the queue status.
     *
     * @param queueStatus current queue status.
     */
    public void setQueueStatus(QueueStatus queueStatus) {
        this.queueStatus = queueStatus;
    }

    /**
     * The bot will leave the queue if the bot shuts down.
     */
    private void leavingQueue() {
        if (queueStatus.equals(QueueStatus.JOINED)) {
            jda.getUserById(config.getBotId()).getPrivateChannel().sendMessage("!leave").complete();
            Logger.logMessage("Leaved the queue.", LogLevel.INFO, LogReason.QUEUE_UPDATE);
        }
    }

    /**
     * Will return the bot status.
     *
     * @return bot status.
     */
    public BotStatus getBotStatus() {
        return botStatus;
    }

    /**
     * Will block the thread before the next action can be executed.
     */
    public void waitBeforeAction() {
        try {
            long fuzzyTime = config.getWaitingFuzzyTime();
            long waitingTime = config.getWaitingTime();
            Random random = new Random();

            long sleep;
            if (fuzzyTime <= 0) {
                sleep = waitingTime * 1000;
            } else {
                sleep = waitingTime * 1000 + random.nextInt((int) (fuzzyTime * 1000));
            }

            if (sleep > 0) {
                Thread.sleep(sleep);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Will shutdown the bot.
     */
    public void shutdown() {
        leavingQueue();
        jda.shutdown();
    }

    /**
     * Will return the current buildId.
     *
     * @return buildId.
     */
    public String getBuildId() {
        return "020-a";
    }
}
