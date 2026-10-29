package turnboard;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

import turnboard.Protocol.Event;
import turnboard.Protocol.Request;

public class SocketGameClient implements GameClient {

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private Consumer<Event> listener;

    private volatile boolean connected;

    @Override
    public void connect(
        String host,
        int port,
        String playerName,
        String serviceName,
        Consumer<Event> eventListener
    ) throws Exception {
        listener = eventListener;

        socket = new Socket(
            host,
            port
        );

        output = new ObjectOutputStream(
            socket.getOutputStream()
        );

        output.flush();

        input = new ObjectInputStream(
            socket.getInputStream()
        );

        connected = true;

        write(
            new Request(
                "JOIN",
                playerName
            )
        );

        Thread reader = new Thread(
            this::receiveEvents
        );

        reader.setName("socket-client-reader");
        reader.setDaemon(true);
        reader.start();
    }

    private void receiveEvents() {
        try {
            while (connected) {
                Object object = input.readObject();

                if (object instanceof Event) {
                    listener.accept(
                        (Event) object
                    );
                }
            }

        } catch (EOFException exception) {
            if (connected) {
                notifyDisconnected(
                    "O servidor encerrou a conexao."
                );
            }

        } catch (Exception exception) {
            if (connected) {
                notifyDisconnected(
                    "Conexao interrompida: "
                    + exception.getMessage()
                );
            }

        } finally {
            close();
        }
    }

    private void notifyDisconnected(String message) {
        if (listener != null) {
            listener.accept(
                new Event(
                    "ERROR",
                    message,
                    null
                )
            );
        }
    }

    private synchronized void write(
        Request request
    ) throws Exception {
        if (!connected || output == null) {
            throw new IllegalStateException(
                "O cliente nao esta conectado."
            );
        }

        output.writeObject(request);
        output.flush();
        output.reset();
    }

    @Override
    public void send(String command) throws Exception {
        write(
            new Request(
                "COMMAND",
                command
            )
        );
    }

    @Override
    public synchronized void close() {
        if (!connected) {
            return;
        }

        try {
            write(
                new Request(
                    "LEAVE",
                    ""
                )
            );
        } catch (Exception exception) {
            System.err.println(
                "Nao foi possivel enviar desconexao."
            );
        }

        connected = false;

        try {
            socket.close();
        } catch (Exception exception) {
            System.err.println(
                "Falha ao fechar a conexao."
            );
        }
    }
}