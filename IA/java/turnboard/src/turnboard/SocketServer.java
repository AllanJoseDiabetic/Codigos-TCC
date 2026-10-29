package turnboard;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import turnboard.Protocol.Event;
import turnboard.Protocol.Request;

public class SocketServer {

    private final Game game;
    private final int port;

    public SocketServer(Game game, int port) {
        this.game = game;
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocket server = new ServerSocket(port);

        Thread acceptThread = new Thread(() -> {
            while (!server.isClosed()) {
                try {
                    Socket socket = server.accept();

                    Thread clientThread = new Thread(
                        () -> handleClient(socket)
                    );

                    clientThread.setDaemon(true);
                    clientThread.start();

                } catch (IOException exception) {
                    if (!server.isClosed()) {
                        System.err.println(
                            "Falha ao aceitar conexao TCP."
                        );
                    }
                }
            }
        });

        acceptThread.setName("socket-acceptor");
        acceptThread.start();
    }

    private void handleClient(Socket socket) {
        String session = null;

        try (
            Socket connection = socket;
            ObjectOutputStream output =
                new ObjectOutputStream(
                    connection.getOutputStream()
                )
        ) {
            output.flush();

            try (
                ObjectInputStream input =
                    new ObjectInputStream(
                        connection.getInputStream()
                    )
            ) {
                Object firstObject = input.readObject();

                if (!(firstObject instanceof Request)) {
                    write(
                        output,
                        new Event(
                            "ERROR",
                            "Mensagem inicial invalida.",
                            null
                        )
                    );
                    return;
                }

                Request firstRequest = (Request) firstObject;

                if (!"JOIN".equals(firstRequest.type)) {
                    write(
                        output,
                        new Event(
                            "ERROR",
                            "O primeiro comando deve ser JOIN.",
                            null
                        )
                    );
                    return;
                }

                try {
                    session = game.join(
                        firstRequest.value,
                        event -> write(output, event)
                    );

                } catch (IllegalArgumentException exception) {
                    write(
                        output,
                        new Event(
                            "ERROR",
                            exception.getMessage(),
                            null
                        )
                    );
                    return;
                }

                while (!connection.isClosed()) {
                    Object received = input.readObject();

                    if (!(received instanceof Request)) {
                        continue;
                    }

                    Request request = (Request) received;

                    if ("COMMAND".equals(request.type)) {
                        game.command(
                            session,
                            request.value
                        );

                    } else if ("LEAVE".equals(request.type)) {
                        break;
                    }
                }
            }

        } catch (EOFException exception) {
            System.out.println(
                "Conexao TCP encerrada."
            );

        } catch (
            IOException |
            ClassNotFoundException exception
        ) {
            System.err.println(
                "Conexao TCP interrompida: "
                + exception.getMessage()
            );

        } finally {
            if (session != null) {
                game.disconnect(session);
            }
        }
    }

    private void write(
        ObjectOutputStream output,
        Event event
    ) throws IOException {
        synchronized (output) {
            output.writeObject(event);
            output.flush();
            output.reset();
        }
    }
}