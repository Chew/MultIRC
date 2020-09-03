package pw.chew.multirc.listeners;

import net.engio.mbassy.listener.Handler;
import org.kitteh.irc.client.library.command.UserModeCommand;
import org.kitteh.irc.client.library.element.mode.ModeStatus;
import org.kitteh.irc.client.library.event.channel.ChannelCtcpEvent;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.client.ClientNegotiationCompleteEvent;
import org.kitteh.irc.client.library.util.Format;
import pw.chew.multirc.Main;

public class ChannelMessageHandler {
    private static final Format[] COLORS = {
            Format.BLUE,
            Format.BROWN,
            Format.CYAN,
            Format.DARK_GREEN,
            Format.GREEN,
            Format.MAGENTA,
            Format.OLIVE,
            Format.PURPLE,
            Format.RED,
            Format.TEAL,
            Format.YELLOW,
    };

    @Handler
    public void onChannelMessage(ChannelMessageEvent event) {
        if(event.getActor().getNick().equals(event.getActor().getClient().getNick()))
            return;

        String message = getDisplayName(event.getActor().getNick()) + " " + event.getMessage();
        if(event.getClient() == Main.server1) {
            Main.server2.sendMessage(Main.mapping1.get(event.getChannel().getName()), message);
        } else {
            Main.server1.sendMessage(Main.mapping2.get(event.getChannel().getName()), message);
        }
    }

    @Handler
    public void onChannelCTCP(ChannelCtcpEvent event) {
        if(event.getCommand().equalsIgnoreCase("action")) {
            String message = Format.ITALIC + event.getActor().getNick() + " " + event.getMessage().substring(7);

            if(event.getClient() == Main.server1) {
                Main.server2.sendMessage(Main.mapping1.get(event.getChannel().getName()), message);
            } else {
                Main.server1.sendMessage(Main.mapping2.get(event.getChannel().getName()), message);
            }
        }
    }
    
    @Handler
    public void onConnect(ClientNegotiationCompleteEvent event) {
        if(event.getClient() == Main.server1 && Boolean.parseBoolean(Main.server1config.getProperty("botmode"))
                || (event.getClient() == Main.server2 && Boolean.parseBoolean(Main.server2config.getProperty("botmode")))) {
            event.getClient().getServerInfo().getUserModes().stream().filter((mode) ->
                    mode.getChar() == 'B')
                    .findFirst()
                    .ifPresent((bruh) ->
                            new UserModeCommand(event.getClient())
                                    .add(ModeStatus.Action.ADD, bruh)
                                    .execute());
        }
    }

    private String getDisplayName(String nick) {
        var index = 0;
        for(char it : nick.toCharArray()) {
            index += it;
        }
        var color = COLORS[Math.abs(index) % COLORS.length];

        return "<" + Format.COLOR_CHAR + color + nick.charAt(0) + "\u200D️" + nick.substring(1) + Format.RESET +">";
    }
}
