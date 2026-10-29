package turnboard;

import java.util.function.Consumer;

import turnboard.Protocol.Event;

public interface GameClient {

    void connect(
        String host,
        int port,
        String playerName,
        String serviceName,
        Consumer<Event> eventListener
    ) throws Exception;

    void send(String command) throws Exception;

    void close();
}