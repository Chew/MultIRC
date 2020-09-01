package pw.chew.multirc;

import org.kitteh.irc.client.library.Client;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {
        Properties server1config = new Properties();
        server1config.load(new FileInputStream("server1.properties"));
        Properties server2config = new Properties();
        server2config.load(new FileInputStream("server2.properties"));

        Client server1 = create(server1config);
        Client server2 = create(server2config);
    }

    public static Client create(Properties properties) {
        return Client.builder()
                .nick(properties.getProperty("nickname"))
                .realName(properties.getProperty("realname"))
                .user(properties.getProperty("username"))
                .server()
                .host(properties.getProperty("host"))
                .port(Integer.parseInt(properties.getProperty("port")), Client.Builder.Server.SecurityType.SECURE)
                .then()
                .buildAndConnect();
    }
}
