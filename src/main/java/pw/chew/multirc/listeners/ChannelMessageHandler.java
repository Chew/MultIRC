package pw.chew.multirc.listeners;

import net.engio.mbassy.listener.Handler;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
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
        String message = getDisplayName(event.getActor().getNick()) + " " + event.getMessage();
        if(event.getClient() == Main.server1) {
            Main.server2.sendMessage(Main.mapping1.get(event.getChannel().getName()), message);
        } else {
            Main.server1.sendMessage(Main.mapping2.get(event.getChannel().getName()), message);
        }
    }

    private String getDisplayName(String nick) {
        var index = 0;
        for(char it : nick.toCharArray()) {
            index += it;
        }
        var color = COLORS[Math.abs(index) % COLORS.length];

        return "<" + Format.COLOR_CHAR + color + nick + Format.RESET +">";
    }
}