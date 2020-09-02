package pw.chew.multirc;

import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.feature.auth.SaslPlain;
import pw.chew.multirc.listeners.ChannelMessageHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Main {
    public static Client server1, server2;
    public static Map<String, String> mapping1, mapping2 = new HashMap<>();
    public static Properties server1config = new Properties();
    public static Properties server2config = new Properties();

    public static void main(String[] args) throws IOException {
        server1config.load(new FileInputStream("server1.properties"));
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
                .build();

        if(properties.containsKey("nickservname") && properties.containsKey("nickservpass"))
            client.getAuthManager().addProtocol(new SaslPlain(client, properties.getProperty("nickservname"), properties.getProperty("nickservpass")));
        client.connect();

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
