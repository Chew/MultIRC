package pw.chew.multirc;

import org.kitteh.irc.client.library.Client;
import pw.chew.multirc.listeners.ChannelMessageHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Main {
    public static Client server1, server2;

    public static void main(String[] args) throws IOException {
        Properties server1config = new Properties();
        server1config.load(new FileInputStream("server1.properties"));
        Properties server2config = new Properties();
        server2config.load(new FileInputStream("server2.properties"));

        server1 = create(server1config);
        server2 = create(server2config);
    }

    public static Client create(Properties properties) {
        Client client = Client.builder()
                .nick(properties.getProperty("nickname"))
                .realName(properties.getProperty("realname"))
                .user(properties.getProperty("username"))
                .server()
                .host(properties.getProperty("host"))
                .port(Integer.parseInt(properties.getProperty("port")), Client.Builder.Server.SecurityType.SECURE)
                .then()
                .buildAndConnect();

        String[] mappings = properties.getProperty("mapping").split(",");
        List<String> channels = new ArrayList<>();
        for(String mapping : mappings)
            channels.add(mapping.split(":")[0]);
        client.addChannel(channels.toArray(new String[0]));
        client.getEventManager().registerEventListener(new ChannelMessageHandler());

        return client;
    }
}
