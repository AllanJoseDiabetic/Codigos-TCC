package turnboard;

import java.util.Scanner;

import turnboard.Protocol.Event;
import turnboard.Protocol.Snapshot;

public class ConsoleUI {

    private final Scanner scanner;
    private final GameClient client;

    private ConsoleUI(
        Scanner scanner,
        GameClient client
    ) {
        this.scanner = scanner;
        this.client = client;
    }

    public static void start(
        String protocol,
        String host,
        int port,
        String playerName,
        String serviceName
    ) throws Exception {
        Scanner scanner = new Scanner(System.in);

        if (playerName == null || playerName.trim().isEmpty()) {
            System.out.print("Nome do jogador: ");
            playerName = scanner.nextLine().trim();
        }

        GameClient client = "rmi".equalsIgnoreCase(protocol)
            ? new RmiGameClient()
            : new SocketGameClient();

        client.connect(
            host,
            port,
            playerName,
            serviceName,
            ConsoleUI::receiveEvent
        );

        ConsoleUI console = new ConsoleUI(
            scanner,
            client
        );

        try {
            console.run();
        } finally {
            client.close();
        }
    }

    private static void receiveEvent(Event event) {
        synchronized (System.out) {
            System.out.println();
            System.out.println(
                "[" + event.kind + "] " + event.text
            );

            if (event.snapshot != null) {
                Snapshot state = event.snapshot;

                System.out.println(
                    "Fase: " + state.phase
                    + " | Periodo: " + state.period
                    + " | Rodada: " + state.round
                    + " | Vez de: " + state.currentPlayer
                );

                if (!state.churchMessage.isEmpty()) {
                    System.out.println(
                        state.churchMessage
                    );
                }
            }

            System.out.print("> ");
        }
    }

    private void run() {
        boolean running = true;

        System.out.println(
            "Cliente conectado. Aguarde a formacao da partida."
        );

        while (running) {
            printMenu();

            String choice = read("Escolha: ");

            try {
                switch (choice) {
                    case "1":
                        client.send("BOARD");
                        break;

                    case "2":
                        client.send("SELF");
                        break;

                    case "3":
                        client.send("RIVALS");
                        break;

                    case "4":
                        client.send("CARDS");
                        break;

                    case "5":
                        client.send("RESOURCES");
                        break;

                    case "6":
                        client.send("POINTS");
                        break;

                    case "7":
                        client.send("EXCOMMUNICATIONS");
                        break;

                    case "8":
                        guidedPlacement();
                        break;

                    case "9":
                        guidedServants();
                        break;

                    case "10":
                        client.send("PASS");
                        break;

                    case "11":
                        guidedChurch();
                        break;

                    case "12":
                        guidedChat();
                        break;

                    case "13":
                        client.send("RULES");
                        break;

                    case "14":
                        client.send("VIEW");
                        break;

                    case "15":
                        client.send("HELP");
                        break;

                    case "0":
                        running = false;
                        break;

                    default:
                        if (!choice.isEmpty()) {
                            client.send(choice);
                        }
                }

            } catch (Exception exception) {
                System.out.println(
                    "Falha de comunicacao: "
                    + exception.getMessage()
                );
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("========== MENU ==========");
        System.out.println("1  - Consultar tabuleiro");
        System.out.println("2  - Consultar minha area");
        System.out.println("3  - Consultar adversarios");
        System.out.println("4  - Consultar cartas");
        System.out.println("5  - Consultar recursos");
        System.out.println("6  - Consultar pontos");
        System.out.println("7  - Consultar excomunhoes");
        System.out.println("8  - Posicionar familiar");
        System.out.println("9  - Gastar servos");
        System.out.println("10 - Encerrar acoes da rodada");
        System.out.println("11 - Decisao da igreja");
        System.out.println("12 - Enviar mensagem no chat");
        System.out.println("13 - Consultar regras adotadas");
        System.out.println("14 - Consultar estado completo");
        System.out.println("15 - Consultar ajuda");
        System.out.println("0  - Sair");
        System.out.println();
        System.out.println(
            "Tambem e possivel digitar um comando diretamente."
        );
    }

    private String read(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private void guidedPlacement() throws Exception {
        client.send("BOARD");
        client.send("SELF");

        String familiar = read(
            "Familiar disponivel (F1, F2, F3): "
        );

        String area = read(
            "Area (exemplo: MARKET-1, HARVEST-1, "
            + "COUNCIL, TERRITORY-1): "
        ).toUpperCase();

        String command = "PLACE "
            + familiar.toUpperCase()
            + " "
            + area;

        if ("COUNCIL".equals(area)) {
            String privilege = read(
                "Privilegio (COIN, WOOD, STONE, FAITH, "
                + "MILITARY, SERVANTS): "
            );

            command += " " + privilege.toUpperCase();

        } else if (
            area.startsWith("TERRITORY-")
            || area.startsWith("BUILDING-")
            || area.startsWith("CHARACTER-")
            || area.startsWith("VENTURE-")
        ) {
            String alternative = read(
                "Alternativa de custo (A ou B): "
            );

            command += " " + alternative.toUpperCase();

        } else if (
            area.startsWith("HARVEST-")
            || area.startsWith("PRODUCTION-")
        ) {
            String effects = read(
                "IDs das cartas com efeitos permanentes "
                + "(separados por virgula, ou -): "
            );

            if (!effects.isEmpty() && !"-".equals(effects)) {
                command += " - " + effects;
            }
        }

        client.send(command);
    }

    private void guidedServants() throws Exception {
        String familiar = read(
            "Familiar que recebera o aumento: "
        );

        String quantity = read(
            "Quantidade de servos: "
        );

        client.send(
            "SERVANTS "
            + familiar.toUpperCase()
            + " "
            + quantity
        );
    }

    private void guidedChurch() throws Exception {
        System.out.println(
            "SUPPORT: apoiar a igreja, se possuir fe suficiente."
        );

        System.out.println(
            "REFUSE: recusar o apoio e receber excomunhao."
        );

        String decision = read(
            "Escolha SUPPORT ou REFUSE: "
        );

        client.send(
            "CHURCH " + decision.toUpperCase()
        );
    }

    private void guidedChat() throws Exception {
        String message = read(
            "Mensagem: "
        );

        client.send(
            "CHAT " + message
        );
    }
}