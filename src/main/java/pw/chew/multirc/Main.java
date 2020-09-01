package pw.chew.multirc;

import org.kitteh.irc.client.library.Client;
import pw.chew.multirc.listeners.ChannelMessageHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Main {
    public static Client server1, server2;
    public static Map<String, String> mapping1, mapping2 = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Properties server1config = new Properties();
        server1config.load(new FileInputStream("server1.properties"));
        Properties server2config = new Properties();
        server2config.load(new FileInputStream("server2.properties"));

        server1 = create(server1config);
        server2 = create(server2config);

        mapping1 = generateMapping(server1config);
        mapping2 = generateMapping(server2config);
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

    public static Map<String, String> generateMapping(Properties properties) {
        String[] mappings = properties.getProperty("mapping").split(",");
        Map<String, String> response = new HashMap<>();
        for(String mapping : mappings) {
            String[] mappin = mapping.split(":");
            response.put(mappin[0], mappin[1]);
        }
        return response;
    }
}
