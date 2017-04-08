package net.cerberus.queueBot.listener;

import net.cerberus.queueBot.Main;
import net.cerberus.queueBot.common.QueueStatus;
import net.cerberus.queueBot.io.LogLevel;
import net.cerberus.queueBot.io.LogReason;
import net.cerberus.queueBot.io.Logger;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;


public class MessageListener extends ListenerAdapter{

    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event){
        if(event.getAuthor().getId().equals(Main.getConfig().getBotId())){
            if(event.getMessage().getRawContent().contains("AFK check")){
                Logger.logMessage("Afk check for queue.", LogLevel.INFO, LogReason.AFK_CHECK);
                Main.getBot().waitBeforeAction();
                event.getChannel().sendMessage("!here").queue();
                Main.getBot().setQueueStatus(QueueStatus.JOINED);
            }else if(event.getMessage().getRawContent().contains("You have been added")){
                Logger.logMessage("Drop lobby queue joined successful.", LogLevel.INFO, LogReason.QUEUE_JOIN);
                Main.getBot().setQueueStatus(QueueStatus.JOINED);
            }else if(event.getMessage().getRawContent().contains("You are position")){
                Logger.logMessage("Queue status:" + StringUtils.substringBetween(event.getMessage().getRawContent(), "position", "in"), LogLevel.INFO, LogReason.QUEUE_UPDATE);
                Main.getBot().setQueueStatus(QueueStatus.JOINED);
            }else if(event.getMessage().getRawContent().contains("to join the current queue")){
                Main.getBot().setQueueStatus(QueueStatus.NOT_JOINED_AVAILABLE);
            }else if(event.getMessage().getRawContent().contains("The queue hasn't started.")){
                Main.getBot().setQueueStatus(QueueStatus.NOT_JOINED_NOT_AVAILABLE);
            }else if(event.getMessage().getRawContent().contains("You are not currently queued up.")){
                Main.getBot().setQueueStatus(QueueStatus.NOT_JOINED_AVAILABLE);
            }else if(event.getMessage().getRawContent().contains("You have been removed from the queue.")){
                Logger.logMessage("You got manually removed from the queue, shutting down.", LogLevel.INFO, LogReason.BOT);
                Main.getBot().setQueueStatus(QueueStatus.NOT_JOINED_AVAILABLE);
                Main.getBot().waitBeforeAction();
                Main.shutdown();
            }else{
                Logger.logMessage("Unknown bot message.", LogLevel.WARNING, LogReason.BOT);
            }
        }
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){

        if(event.getChannel().getId().equals(Main.getConfig().getChannelId())){
            if(event.getMessage().getRawContent().contains("A drop is starting")){
                String joinMessage = StringUtils.substringAfter(event.getMessage().getRawContent(), "!join").replaceAll("`", "");
                Main.getBot().waitBeforeAction();
                event.getJDA().getUserById(Main.getConfig().getBotId()).getPrivateChannel().sendMessage("!join" + joinMessage).queue();
                Logger.logMessage("New drop is starting!", LogLevel.INFO, LogReason.QUEUE_JOIN);
                Main.getSoundManager().alert();
            } else if (event.getMessage().getRawContent().contains("have been accepted to a drop")) {
                event.getJDA().getUserById(Main.getConfig().getBotId()).getPrivateChannel().sendMessage("!q").queue();
            }
        }

        /* Testing channel. */
        if (event.getChannel().getId().equals("199899997825662977") && event.getAuthor().getId().equals("174963408196599808")) {
            if(event.getMessage().getRawContent().contains("A drop is starting")){
                String joinMessage = StringUtils.substringAfter(event.getMessage().getRawContent(), "!join").replaceAll("`", "");
                event.getAuthor().getPrivateChannel().sendMessage("!join" + joinMessage).queue();
                Logger.logMessage("New drop is starting!", LogLevel.INFO, LogReason.QUEUE_JOIN);
                Main.getSoundManager().alert();
            }
        }
    }
}
