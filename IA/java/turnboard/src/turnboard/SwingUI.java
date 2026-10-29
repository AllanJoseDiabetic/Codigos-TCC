package turnboard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import turnboard.Protocol.CardView;
import turnboard.Protocol.Event;
import turnboard.Protocol.PlayerView;
import turnboard.Protocol.Snapshot;
import turnboard.Protocol.SpaceView;

public class SwingUI {

    private final JFrame frame = new JFrame(
        "Jogo de tabuleiro em rede"
    );

    private final CardLayout screenLayout = new CardLayout();

    private final JPanel screens = new JPanel(
        screenLayout
    );

    private final ExecutorService networkExecutor =
        Executors.newSingleThreadExecutor();

    private final JTextField nameField = new JTextField(15);
    private final JTextField hostField = new JTextField(
        "localhost",
        15
    );

    private final JTextField portField = new JTextField(
        "5050",
        6
    );

    private final JTextField serviceField = new JTextField(
        "TurnBoard",
        12
    );

    private final JComboBox<String> protocolField =
        new JComboBox<>(
            new String[] {
                "Socket",
                "RMI"
            }
        );

    private final JLabel statusLabel = new JLabel(
        "Aguardando conexao"
    );

    private final JLabel selectedLabel = new JLabel(
        "Familiar selecionado: nenhum"
    );

    private final JPanel boardPanel = new JPanel(
        new GridLayout(0, 2, 8, 8)
    );

    private final JPanel familiarsPanel = new JPanel(
        new FlowLayout(FlowLayout.LEFT)
    );

    private final JPanel turnOrderPanel = new JPanel(
        new FlowLayout(FlowLayout.LEFT)
    );

    private final JTextArea informationArea = new JTextArea();
    private final JTextArea messagesArea = new JTextArea();

    private final JTextField chatField = new JTextField(30);

    private GameClient client;
    private Snapshot currentSnapshot;
    private String selectedFamiliar;

    public SwingUI() {
        frame.setDefaultCloseOperation(
            WindowConstants.DISPOSE_ON_CLOSE
        );

        frame.setSize(1100, 760);
        frame.setMinimumSize(
            new Dimension(850, 580)
        );

        frame.setLocationRelativeTo(null);

        buildConnectionScreen();
        buildGameScreen();

        frame.setContentPane(screens);

        frame.addWindowListener(
            new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent event) {
                    networkExecutor.execute(() -> {
                        if (client != null) {
                            client.close();
                        }

                        networkExecutor.shutdown();
                    });
                }
            }
        );
    }

    public void show() {
        frame.setVisible(true);
    }

    private void buildConnectionScreen() {
        JPanel wrapper = new JPanel(
            new BorderLayout()
        );

        JPanel fields = new JPanel(
            new GridLayout(0, 2, 12, 12)
        );

        fields.setBorder(
            BorderFactory.createEmptyBorder(
                35,
                35,
                35,
                35
            )
        );

        fields.add(new JLabel("Nome do jogador"));
        fields.add(nameField);

        fields.add(new JLabel("Servidor"));
        fields.add(hostField);

        fields.add(new JLabel("Protocolo"));
        fields.add(protocolField);

        fields.add(new JLabel("Porta"));
        fields.add(portField);

        fields.add(new JLabel("Nome do servico RMI"));
        fields.add(serviceField);

        JButton connectButton = new JButton(
            "Conectar"
        );

        connectButton.addActionListener(
            event -> connect(connectButton)
        );

        JPanel bottom = new JPanel(
            new FlowLayout(FlowLayout.RIGHT)
        );

        bottom.add(connectButton);

        wrapper.add(
            fields,
            BorderLayout.CENTER
        );

        wrapper.add(
            bottom,
            BorderLayout.SOUTH
        );

        screens.add(
            wrapper,
            "connection"
        );
    }

    private void connect(JButton button) {
        String name = nameField.getText().trim();
        String host = hostField.getText().trim();

        String protocol = (
            String
        ) protocolField.getSelectedItem();

        String serviceName = serviceField.getText().trim();

        int port;

        try {
            port = Integer.parseInt(
                portField.getText().trim()
            );

            if (port < 1 || port > 65535) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException exception) {
            showMessage("Porta de rede invalida.");
            return;
        }

        if (name.isEmpty() || host.isEmpty()) {
            showMessage(
                "Informe o nome do jogador e o servidor."
            );
            return;
        }

        button.setEnabled(false);

        networkExecutor.execute(() -> {
            try {
                GameClient connection =
                    "RMI".equals(protocol)
                        ? new RmiGameClient()
                        : new SocketGameClient();

                connection.connect(
                    host,
                    port,
                    name,
                    serviceName,
                    this::receiveEvent
                );

                client = connection;

                SwingUtilities.invokeLater(() -> {
                    screenLayout.show(
                        screens,
                        "game"
                    );
                });

            } catch (Exception exception) {
                SwingUtilities.invokeLater(() -> {
                    button.setEnabled(true);

                    showMessage(
                        "Conexao recusada: "
                        + exception.getMessage()
                    );
                });
            }
        });
    }

    private void buildGameScreen() {
        JPanel root = new JPanel(
            new BorderLayout(8, 8)
        );

        root.setBorder(
            BorderFactory.createEmptyBorder(
                10,
                10,
                10,
                10
            )
        );

        JPanel header = new JPanel(
            new BorderLayout()
        );

        header.add(
            statusLabel,
            BorderLayout.NORTH
        );

        header.add(
            turnOrderPanel,
            BorderLayout.CENTER
        );

        JPanel left = new JPanel(
            new BorderLayout()
        );

        left.setBorder(
            BorderFactory.createTitledBorder(
                "Tabuleiro - clique em uma area"
            )
        );

        left.add(
            new JScrollPane(boardPanel),
            BorderLayout.CENTER
        );

        JPanel right = new JPanel(
            new BorderLayout()
        );

        right.setBorder(
            BorderFactory.createTitledBorder(
                "Participantes, recursos e cartas"
            )
        );

        informationArea.setEditable(false);
        informationArea.setLineWrap(true);
        informationArea.setWrapStyleWord(true);

        right.add(
            new JScrollPane(informationArea),
            BorderLayout.CENTER
        );

        JSplitPane center = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            left,
            right
        );

        center.setResizeWeight(0.48);

        JPanel familiarSelection = new JPanel(
            new BorderLayout()
        );

        familiarSelection.add(
            selectedLabel,
            BorderLayout.NORTH
        );

        familiarSelection.add(
            familiarsPanel,
            BorderLayout.CENTER
        );

        JPanel operations = new JPanel(
            new FlowLayout(FlowLayout.LEFT)
        );

        addOperation(
            operations,
            "Tabuleiro",
            "BOARD"
        );

        addOperation(
            operations,
            "Minha area",
            "SELF"
        );

        addOperation(
            operations,
            "Adversarios",
            "RIVALS"
        );

        addOperation(
            operations,
            "Cartas",
            "CARDS"
        );

        addOperation(
            operations,
            "Recursos",
            "RESOURCES"
        );

        addOperation(
            operations,
            "Pontos",
            "POINTS"
        );

        addOperation(
            operations,
            "Excomunhoes",
            "EXCOMMUNICATIONS"
        );

        addOperation(
            operations,
            "Regras",
            "RULES"
        );

        JButton servantsButton = new JButton(
            "Gastar servos"
        );

        servantsButton.addActionListener(
            event -> spendServants()
        );

        operations.add(servantsButton);

        addOperation(
            operations,
            "Passar",
            "PASS"
        );

        addOperation(
            operations,
            "Apoiar igreja",
            "CHURCH SUPPORT"
        );

        addOperation(
            operations,
            "Recusar igreja",
            "CHURCH REFUSE"
        );

        JPanel chat = new JPanel(
            new BorderLayout(8, 8)
        );

        JButton sendChatButton = new JButton(
            "Enviar chat"
        );

        sendChatButton.addActionListener(
            event -> sendChat()
        );

        chatField.addActionListener(
            event -> sendChat()
        );

        chat.add(
            chatField,
            BorderLayout.CENTER
        );

        chat.add(
            sendChatButton,
            BorderLayout.EAST
        );

        messagesArea.setEditable(false);
        messagesArea.setLineWrap(true);
        messagesArea.setWrapStyleWord(true);

        JPanel bottom = new JPanel(
            new BorderLayout(5, 5)
        );

        JPanel actionContainer = new JPanel(
            new BorderLayout()
        );

        actionContainer.add(
            familiarSelection,
            BorderLayout.NORTH
        );

        actionContainer.add(
            operations,
            BorderLayout.CENTER
        );

        bottom.add(
            actionContainer,
            BorderLayout.NORTH
        );

        JScrollPane messagesScroll = new JScrollPane(
            messagesArea
        );

        messagesScroll.setPreferredSize(
            new Dimension(100, 120)
        );

        bottom.add(
            messagesScroll,
            BorderLayout.CENTER
        );

        bottom.add(
            chat,
            BorderLayout.SOUTH
        );

        root.add(
            header,
            BorderLayout.NORTH
        );

        root.add(
            center,
            BorderLayout.CENTER
        );

        root.add(
            bottom,
            BorderLayout.SOUTH
        );

        screens.add(
            root,
            "game"
        );
    }

    private void addOperation(
        JPanel panel,
        String label,
        String command
    ) {
        JButton button = new JButton(label);

        button.addActionListener(
            event -> send(command)
        );

        panel.add(button);
    }

    private void receiveEvent(Event event) {
        SwingUtilities.invokeLater(() -> {
            appendMessage(
                "[" + event.kind + "] " + event.text
            );

            if (event.snapshot != null) {
                refresh(event.snapshot);
            }
        });
    }

    private void refresh(Snapshot snapshot) {
        currentSnapshot = snapshot;

        statusLabel.setText(
            "Fase: " + snapshot.phase
            + " | Periodo: " + snapshot.period
            + " | Rodada: " + snapshot.round
            + " | Vez de: " + snapshot.currentPlayer
        );

        refreshTurnOrder(snapshot);
        refreshFamiliars(snapshot);
        refreshBoard(snapshot);
        refreshInformation(snapshot);

        if (
            snapshot.ranking != null
            && !snapshot.ranking.isEmpty()
        ) {
            statusLabel.setText(
                "Partida encerrada - classificacao disponivel"
            );
        }
    }

    private void refreshTurnOrder(Snapshot snapshot) {
        turnOrderPanel.removeAll();

        turnOrderPanel.add(
            new JLabel("Ordem: ")
        );

        for (PlayerView player : snapshot.players) {
            JLabel label = new JLabel(
                " " + player.name + " "
            );

            label.setOpaque(true);

            if (
                player.name.equals(
                    snapshot.currentPlayer
                )
            ) {
                label.setBackground(
                    new Color(150, 220, 160)
                );

            } else if (!player.connected) {
                label.setBackground(
                    new Color(220, 220, 220)
                );

            } else {
                label.setBackground(
                    new Color(225, 235, 250)
                );
            }

            label.setBorder(
                BorderFactory.createEmptyBorder(
                    5,
                    8,
                    5,
                    8
                )
            );

            turnOrderPanel.add(label);
        }

        turnOrderPanel.revalidate();
        turnOrderPanel.repaint();
    }

    private void refreshFamiliars(Snapshot snapshot) {
        familiarsPanel.removeAll();

        PlayerView own = null;

        for (PlayerView player : snapshot.players) {
            if (player.self) {
                own = player;
                break;
            }
        }

        if (own == null) {
            return;
        }

        for (String description : own.familiars) {
            String id = description.substring(
                0,
                description.indexOf(':')
            );

            JButton button = new JButton(
                description
            );

            button.setEnabled(
                !description.contains("[usado]")
            );

            button.addActionListener(event -> {
                selectedFamiliar = id;

                selectedLabel.setText(
                    "Familiar selecionado: "
                    + selectedFamiliar
                );
            });

            familiarsPanel.add(button);
        }

        familiarsPanel.revalidate();
        familiarsPanel.repaint();
    }

    private void refreshBoard(Snapshot snapshot) {
        boardPanel.removeAll();

        boolean myTurn = (
            "PLAY".equals(snapshot.phase)
            && snapshot.self.equals(
                snapshot.currentPlayer
            )
        );

        for (SpaceView space : snapshot.spaces) {
            String description = space.id
                + " | Poder " + space.minimumPower;

            if (space.card != null) {
                description += " | " + space.card.name;
            }

            if (space.occupiedBy != null) {
                description += " | " + space.occupiedBy;
            }

            JButton button = new JButton(
                description
            );

            button.setToolTipText(
                space.card == null
                    ? space.id
                    : space.card.toString()
            );

            button.setEnabled(
                myTurn && space.occupiedBy == null
            );

            button.addActionListener(
                event -> selectSpace(space)
            );

            boardPanel.add(button);
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private void refreshInformation(Snapshot snapshot) {
        StringBuilder text = new StringBuilder();

        text.append("Participantes: ")
            .append(snapshot.players.size())
            .append("/")
            .append(snapshot.requiredPlayers)
            .append("\n\n");

        if (!snapshot.churchMessage.isEmpty()) {
            text.append(snapshot.churchMessage)
                .append("\n\n");
        }

        for (PlayerView player : snapshot.players) {
            text.append("Jogador: ")
                .append(player.name);

            if (player.self) {
                text.append(" (voce)");
            }

            if (!player.connected) {
                text.append(" (desconectado)");
            }

            text.append('\n')
                .append("Moedas: ").append(player.coins)
                .append(" | Madeira: ").append(player.wood)
                .append(" | Pedra: ").append(player.stone)
                .append("\nServos: ").append(player.servants)
                .append(" | Fe: ").append(player.faith)
                .append(" | Militar: ").append(player.military)
                .append(" | Vitoria: ").append(player.victory)
                .append("\nPontuacao estimada: ")
                .append(player.score)
                .append('\n');

            text.append("Familiares:\n");

            for (String familiar : player.familiars) {
                text.append("  ")
                    .append(familiar)
                    .append('\n');
            }

            text.append("Cartas:\n");

            for (CardView card : player.cards) {
                text.append("  ")
                    .append(card.toString())
                    .append('\n');
            }

            text.append("Excomunhoes:\n");

            for (String penalty : player.excommunications) {
                text.append("  ")
                    .append(penalty)
                    .append('\n');
            }

            text.append('\n');
        }

        if (
            snapshot.ranking != null
            && !snapshot.ranking.isEmpty()
        ) {
            text.append(snapshot.ranking);
        }

        informationArea.setText(text.toString());
        informationArea.setCaretPosition(0);
    }

    private void selectSpace(SpaceView space) {
        if (selectedFamiliar == null) {
            showMessage(
                "Selecione um familiar antes de escolher a area."
            );
            return;
        }

        String command = "PLACE "
            + selectedFamiliar
            + " "
            + space.id;

        if ("COUNCIL".equals(space.id)) {
            String[] choices = {
                "COIN",
                "WOOD",
                "STONE",
                "FAITH",
                "MILITARY",
                "SERVANTS"
            };

            String choice = (String) JOptionPane.showInputDialog(
                frame,
                "Selecione o privilegio do conselho:",
                "Conselho",
                JOptionPane.QUESTION_MESSAGE,
                null,
                choices,
                choices[0]
            );

            if (choice == null) {
                return;
            }

            command += " " + choice;

        } else if (
            space.id.startsWith("TERRITORY-")
            || space.id.startsWith("BUILDING-")
            || space.id.startsWith("CHARACTER-")
            || space.id.startsWith("VENTURE-")
        ) {
            if (space.card != null) {
                String[] choices = (
                    "indisponivel".equals(
                        space.card.alternativeCost
                    )
                )
                    ? new String[] {"A"}
                    : new String[] {"A", "B"};

                String choice = (String) JOptionPane.showInputDialog(
                    frame,
                    space.card.toString(),
                    "Escolha o custo da carta",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    choices,
                    choices[0]
                );

                if (choice == null) {
                    return;
                }

                command += " " + choice;
            }

        } else if (
            space.id.startsWith("HARVEST-")
            || space.id.startsWith("PRODUCTION-")
        ) {
            String effects = JOptionPane.showInputDialog(
                frame,
                "Informe os IDs das cartas cujos efeitos "
                + "permanentes deseja aplicar.\n"
                + "Separe por virgulas ou use -.",
                "-"
            );

            if (effects == null) {
                return;
            }

            effects = effects.trim();

            if (!effects.isEmpty() && !"-".equals(effects)) {
                command += " - " + effects;
            }
        }

        send(command);
    }

    private void spendServants() {
        if (selectedFamiliar == null) {
            showMessage(
                "Selecione um familiar antes de gastar servos."
            );
            return;
        }

        String quantity = JOptionPane.showInputDialog(
            frame,
            "Quantidade de servos para "
            + selectedFamiliar + ":"
        );

        if (quantity == null) {
            return;
        }

        send(
            "SERVANTS "
            + selectedFamiliar
            + " "
            + quantity.trim()
        );
    }

    private void sendChat() {
        String message = chatField.getText().trim();

        if (!message.isEmpty()) {
            send("CHAT " + message);
            chatField.setText("");
        }
    }

    private void send(String command) {
        networkExecutor.execute(() -> {
            try {
                if (client == null) {
                    throw new IllegalStateException(
                        "Nao existe conexao ativa."
                    );
                }

                client.send(command);

            } catch (Exception exception) {
                SwingUtilities.invokeLater(() -> {
                    appendMessage(
                        "[ERRO DE REDE] "
                        + exception.getMessage()
                    );
                });
            }
        });
    }

    private void appendMessage(String message) {
        messagesArea.append(
            message + "\n"
        );

        messagesArea.setCaretPosition(
            messagesArea.getDocument().getLength()
        );
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(
            frame,
            message
        );
    }
}