package turnboard;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

public class Launcher {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String mode = args[0].toLowerCase();

        switch (mode) {
            case "server":
                startServer(args);
                break;

            case "cli-socket":
                startConsole(args, "socket");
                break;

            case "cli-rmi":
                startConsole(args, "rmi");
                break;

            case "gui":
                SwingUtilities.invokeLater(() -> {
                    SwingUI interfaceWindow = new SwingUI();
                    interfaceWindow.show();
                });
                break;

            default:
                printUsage();
        }
    }

    private static void startServer(
        String[] args
    ) throws Exception {
        String configurationPath = args.length >= 2
            ? args[1]
            : "config/game.example.properties";

        Properties properties = new Properties();

        try (
            InputStream input = Files.newInputStream(
                Paths.get(configurationPath)
            )
        ) {
            properties.load(input);
        }

        Game game = new Game(properties);

        int socketPort = Integer.parseInt(
            properties.getProperty(
                "network.socketPort",
                "5050"
            )
        );

        int rmiPort = Integer.parseInt(
            properties.getProperty(
                "network.rmiPort",
                "1099"
            )
        );

        String rmiName = properties.getProperty(
            "network.rmiName",
            "TurnBoard"
        );

        SocketServer socketServer = new SocketServer(
            game,
            socketPort
        );

        socketServer.start();

        RmiServer.start(
            game,
            rmiPort,
            rmiName
        );

        System.out.println(
            "Servidor de jogo iniciado."
        );

        System.out.println(
            "Socket TCP: porta " + socketPort
        );

        System.out.println(
            "RMI: porta " + rmiPort
            + ", servico " + rmiName
        );

        System.out.println(
            "Ambos os protocolos compartilham a mesma partida."
        );

        System.out.println(
            "Aguardando participantes."
        );

        new CountDownLatch(1).await();
    }

    private static void startConsole(
        String[] args,
        String protocol
    ) throws Exception {
        String host = args.length >= 2
            ? args[1]
            : "localhost";

        int defaultPort = "rmi".equals(protocol)
            ? 1099
            : 5050;

        int port = args.length >= 3
            ? Integer.parseInt(args[2])
            : defaultPort;

        String playerName = args.length >= 4
            ? args[3]
            : null;

        String serviceName = args.length >= 5
            ? args[4]
            : "TurnBoard";

        ConsoleUI.start(
            protocol,
            host,
            port,
            playerName,
            serviceName
        );
    }

    private static void printUsage() {
        System.out.println(
            "Uso:"
        );

        System.out.println(
            "java turnboard.Launcher server "
            + "[arquivo.properties]"
        );

        System.out.println(
            "java turnboard.Launcher cli-socket "
            + "[host] [porta] [nome]"
        );

        System.out.println(
            "java turnboard.Launcher cli-rmi "
            + "[host] [porta] [nome] [servico]"
        );

        System.out.println(
            "java turnboard.Launcher gui"
        );
    }
}