package net.cerberus.queBot.events;

import net.cerberus.queBot.Main;
import net.cerberus.queBot.common.QueStatus;
import net.cerberus.queBot.io.LogLevel;
import net.cerberus.queBot.io.LogReason;
import net.cerberus.queBot.io.Logger;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;


public class MessageListener extends ListenerAdapter{



    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event){

        if(event.getAuthor().getId().equals(Main.getBotId())){
            if(event.getMessage().getRawContent().contains("AFK check")){
                Logger.logMessage("Afk check for que.", LogLevel.INFO, LogReason.AFK_CHECK);
                event.getChannel().sendMessage("!here").queue();
                Main.setQueStatus(QueStatus.JOINED);
            }else if(event.getMessage().getRawContent().contains("You have been added")){
                Logger.logMessage("Drop lobby que joined!", LogLevel.INFO, LogReason.QUE_JOINED);
                Main.setQueStatus(QueStatus.JOINED);
                Main.getSoundManager().alert();
            }else if(event.getMessage().getRawContent().contains("You are position")){
                Logger.logMessage("Que status:" + StringUtils.substringBetween(event.getMessage().getRawContent(), "position", "in"), LogLevel.INFO, LogReason.QUE_UPDATE);
                Main.setQueStatus(QueStatus.JOINED);
            }else if(event.getMessage().getRawContent().contains("to join the current queue")){
                Main.setQueStatus(QueStatus.NOT_JOINED);
            }else if(event.getMessage().getRawContent().contains("The queue hasn't started.")){
                Main.setQueStatus(QueStatus.NOT_JOINED);
            }
        }
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){

        if(event.getChannel().getId().equals(Main.getChannelId())){
            if(event.getMessage().getRawContent().contains("A drop is starting")){
                String joinMessage = StringUtils.substringAfter(event.getMessage().getRawContent(), "!join").replaceAll("`", "");
                event.getJDA().getUserById(Main.getBotId()).getPrivateChannel().sendMessage("!join" + joinMessage).queue();
                Logger.logMessage("New drop is starting!", LogLevel.INFO, LogReason.QUE_JOIN);
                Main.getSoundManager().alert();
            }
        }

        /* Testing channel. */
        if(event.getChannel().getId().equals("199899997825662977")){
            if(event.getMessage().getRawContent().contains("A drop is starting")){
                String joinMessage = StringUtils.substringAfter(event.getMessage().getRawContent(), "!join");
                event.getAuthor().getPrivateChannel().sendMessage("!join" + joinMessage).queue();
                Logger.logMessage("New drop is starting!", LogLevel.INFO, LogReason.QUE_JOIN);
                Main.getSoundManager().alert();
            }
        }
    }
}
