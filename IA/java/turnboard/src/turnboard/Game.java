package turnboard;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import turnboard.Protocol.CardView;
import turnboard.Protocol.Event;
import turnboard.Protocol.PlayerView;
import turnboard.Protocol.Sink;
import turnboard.Protocol.Snapshot;
import turnboard.Protocol.SpaceView;

public class Game {

    private static final String LOBBY = "LOBBY";
    private static final String PLAY = "PLAY";
    private static final String CHURCH = "CHURCH";
    private static final String FINISHED = "FINISHED";

    private static final List<String> TYPES = Arrays.asList(
        "TERRITORY",
        "BUILDING",
        "CHARACTER",
        "VENTURE"
    );

    private final Properties config;
    private final int requiredPlayers;
    private final int periodCount;
    private final int roundsPerPeriod;
    private final int familiarCount;

    private final Map<String, Player> players =
        new LinkedHashMap<>();

    private final Map<String, Sink> listeners =
        new HashMap<>();

    private final Map<String, Space> spaces =
        new LinkedHashMap<>();

    private final Map<String, Deque<Card>> decks =
        new LinkedHashMap<>();

    private final ExecutorService notifications =
        Executors.newSingleThreadExecutor();

    private String phase = LOBBY;
    private String currentPlayer = "";
    private String finalRanking = "";

    private int period = 1;
    private int round = 1;
    private int currentIndex = 0;
    private int nextCardId = 1;

    public Game(Properties config) {
        this.config = config;

        requiredPlayers = setting("players.required", 2);
        periodCount = setting("periods.count", 3);
        roundsPerPeriod = setting("rounds.perPeriod", 2);
        familiarCount = setting("familiars.perPlayer", 3);

        if (requiredPlayers < 2 || requiredPlayers > 4) {
            throw new IllegalArgumentException(
                "A configuracao permite entre 2 e 4 jogadores."
            );
        }

        if (
            periodCount < 1
            || periodCount > 3
            || roundsPerPeriod < 1
            || familiarCount < 1
            || familiarCount > 9
        ) {
            throw new IllegalArgumentException(
                "A configuracao de periodos, rodadas "
                + "ou familiares e invalida."
            );
        }

        initializeDecks();
    }

    private int setting(String name, int defaultValue) {
        return Integer.parseInt(
            config.getProperty(
                name,
                Integer.toString(defaultValue)
            ).trim()
        );
    }

    private static class Delta {
        int coins;
        int wood;
        int stone;
        int servants;
        int faith;
        int military;
        int victory;

        Delta(
            int coins,
            int wood,
            int stone,
            int servants,
            int faith,
            int military,
            int victory
        ) {
            this.coins = coins;
            this.wood = wood;
            this.stone = stone;
            this.servants = servants;
            this.faith = faith;
            this.military = military;
            this.victory = victory;
        }

        String description() {
            return "moedas " + coins
                + ", madeira " + wood
                + ", pedra " + stone
                + ", servos " + servants
                + ", fe " + faith
                + ", militar " + military
                + ", vitoria " + victory;
        }
    }

    private static class Cost {
        int coins;
        int wood;
        int stone;

        Cost(int coins, int wood, int stone) {
            this.coins = coins;
            this.wood = wood;
            this.stone = stone;
        }

        String description() {
            return coins + " moedas, "
                + wood + " madeira, "
                + stone + " pedra";
        }
    }

    private static class Card {
        int id;
        String name;
        String type;
        Cost primary;
        Cost alternative;
        Delta immediate;
        Delta permanent;
        String permanentArea;
        int printedPoints;
        int requiredMilitary;
    }

    private static class Familiar {
        String id;
        int value;
        boolean used;
    }

    private static class Player {
        String token;
        String name;
        boolean connected = true;
        boolean passed;
        boolean churchAnswered;

        int coins;
        int wood;
        int stone;
        int servants;
        int faith;
        int military;
        int victory;

        List<Familiar> familiars = new ArrayList<>();
        List<Card> cards = new ArrayList<>();
        Set<Integer> excommunications = new HashSet<>();

        void receive(Delta delta) {
            coins += delta.coins;
            wood += delta.wood;
            stone += delta.stone;
            servants += delta.servants;
            faith += delta.faith;
            military += delta.military;
            victory += delta.victory;
        }

        boolean canPay(Cost cost) {
            return coins >= cost.coins
                && wood >= cost.wood
                && stone >= cost.stone;
        }

        void pay(Cost cost) {
            coins -= cost.coins;
            wood -= cost.wood;
            stone -= cost.stone;
        }
    }

    private static class Space {
        String id;
        String type;
        int power;
        String occupiedBy;
        Card card;

        Space(String id, String type, int power) {
            this.id = id;
            this.type = type;
            this.power = power;
        }
    }

    private Delta delta(
        int coins,
        int wood,
        int stone,
        int servants,
        int faith,
        int military,
        int victory
    ) {
        return new Delta(
            coins,
            wood,
            stone,
            servants,
            faith,
            military,
            victory
        );
    }

    private Card card(
        String type,
        String name,
        Cost primary,
        Cost alternative,
        Delta immediate,
        Delta permanent,
        String permanentArea,
        int printedPoints,
        int requiredMilitary
    ) {
        Card result = new Card();

        result.id = nextCardId++;
        result.type = type;
        result.name = name;
        result.primary = primary;
        result.alternative = alternative;
        result.immediate = immediate;
        result.permanent = permanent;
        result.permanentArea = permanentArea;
        result.printedPoints = printedPoints;
        result.requiredMilitary = requiredMilitary;

        return result;
    }

    private void initializeDecks() {
        for (String type : TYPES) {
            decks.put(type, new ArrayDeque<>());
        }

        int cardsPerType = periodCount * roundsPerPeriod * 2;

        for (int number = 1; number <= cardsPerType; number++) {
            decks.get("TERRITORY").add(
                card(
                    "TERRITORY",
                    "Territorio " + number,
                    new Cost(2 + number % 3, 1, 0),
                    null,
                    delta(0, 1, 0, 0, 0, 1, 0),
                    delta(0, 1 + number % 2, 0, 0, 0, 0, 0),
                    "HARVEST",
                    1 + number % 3,
                    number % 3
                )
            );

            decks.get("BUILDING").add(
                card(
                    "BUILDING",
                    "Edificio " + number,
                    new Cost(1 + number % 2, 1, 1),
                    null,
                    delta(0, 0, 1, 0, 0, 0, 1),
                    delta(1, 0, 1, 0, 0, 0, 0),
                    "PRODUCTION",
                    1 + number % 2,
                    0
                )
            );

            decks.get("CHARACTER").add(
                card(
                    "CHARACTER",
                    "Personagem " + number,
                    new Cost(2 + number % 3, 0, 0),
                    null,
                    delta(0, 0, 0, 2, 1, 0, 0),
                    delta(0, 0, 0, 0, 0, 0, 0),
                    "",
                    2 + number % 2,
                    0
                )
            );

            decks.get("VENTURE").add(
                card(
                    "VENTURE",
                    "Empreendimento " + number,
                    new Cost(4, 1, 0),
                    new Cost(2, 0, 2),
                    delta(0, 0, 0, 0, 0, 1, 2),
                    delta(0, 0, 0, 0, 0, 0, 0),
                    "",
                    3 + number % 3,
                    0
                )
            );
        }
    }

    public synchronized String join(
        String name,
        Sink sink
    ) {
        if (!LOBBY.equals(phase)) {
            throw new IllegalArgumentException(
                "A partida ja foi iniciada."
            );
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Informe um nome de jogador."
            );
        }

        name = name.trim();

        if (name.length() > 30) {
            throw new IllegalArgumentException(
                "O nome deve ter ate 30 caracteres."
            );
        }

        for (Player existing : players.values()) {
            if (existing.name.equalsIgnoreCase(name)) {
                throw new IllegalArgumentException(
                    "Este nome ja esta sendo utilizado."
                );
            }
        }

        if (players.size() >= requiredPlayers) {
            throw new IllegalArgumentException(
                "A sala esta completa."
            );
        }

        Player player = new Player();

        player.token = UUID.randomUUID().toString();
        player.name = name;

        player.coins = setting("initial.coins", 6);
        player.wood = setting("initial.wood", 3);
        player.stone = setting("initial.stone", 3);
        player.servants = setting("initial.servants", 4);
        player.faith = setting("initial.faith", 2);

        players.put(player.token, player);
        listeners.put(player.token, sink);

        sendTo(
            player,
            "WELCOME",
            "Conectado como " + player.name
        );

        if (players.size() == requiredPlayers) {
            phase = PLAY;
            initializeRound();

            broadcast(
                "Todos os participantes entraram. "
                + "A partida comecou."
            );
        } else {
            broadcast(
                player.name + " entrou na sala. "
                + "Aguardando "
                + (requiredPlayers - players.size())
                + " participante(s)."
            );
        }

        return player.token;
    }

    private void initializeRound() {
        spaces.clear();

        addSpace("MARKET-1", "MARKET", 1);
        addSpace("MARKET-2", "MARKET", 2);
        addSpace("HARVEST-1", "HARVEST", 1);
        addSpace("PRODUCTION-1", "PRODUCTION", 1);
        addSpace("COUNCIL", "COUNCIL", 1);

        for (String type : TYPES) {
            for (int floor = 1; floor <= 2; floor++) {
                String id = type + "-" + floor;

                Space space = addSpace(
                    id,
                    "TOWER",
                    floor == 1 ? 1 : 3
                );

                space.card = decks.get(type).pollFirst();
            }
        }

        for (Player player : players.values()) {
            player.passed = !player.connected;

            player.familiars.clear();

            for (int i = 1; i <= familiarCount; i++) {
                Familiar familiar = new Familiar();

                familiar.id = "F" + i;
                familiar.value = i;

                player.familiars.add(familiar);
            }
        }

        currentIndex = (
            (period - 1) * roundsPerPeriod + round - 1
        ) % players.size();

        List<Player> order = orderedPlayers();

        currentPlayer = order.get(currentIndex).token;

        if (!canAct(order.get(currentIndex))) {
            advanceTurn();
        }
    }

    private Space addSpace(
        String id,
        String type,
        int power
    ) {
        Space space = new Space(id, type, power);

        spaces.put(id, space);

        return space;
    }

    private List<Player> orderedPlayers() {
        return new ArrayList<>(players.values());
    }

    private boolean canAct(Player player) {
        if (!player.connected || player.passed) {
            return false;
        }

        for (Familiar familiar : player.familiars) {
            if (!familiar.used) {
                return true;
            }
        }

        return false;
    }

    public synchronized void command(
        String token,
        String input
    ) {
        Player player = players.get(token);

        if (player == null || !player.connected) {
            return;
        }

        if (input == null || input.trim().isEmpty()) {
            reject(player, "Informe um comando.");
            return;
        }

        String trimmed = input.trim();

        if (trimmed.toUpperCase(Locale.ROOT).startsWith("CHAT ")) {
            String message = trimmed.substring(5).trim();

            if (message.isEmpty()) {
                reject(player, "A mensagem esta vazia.");
            } else if (message.length() > 500) {
                reject(player, "A mensagem excede 500 caracteres.");
            } else {
                broadcast(
                    "[CHAT] " + player.name + ": " + message
                );
            }

            return;
        }

        String[] arguments = trimmed.split("\\s+");

        String operation = arguments[0]
            .toUpperCase(Locale.ROOT);

        if (isInformationCommand(operation)) {
            sendTo(
                player,
                "INFO",
                information(player, operation)
            );
            return;
        }

        try {
            if ("SERVANTS".equals(operation)) {
                spendServants(player, arguments);
            } else if ("PLACE".equals(operation)) {
                place(player, arguments);
            } else if ("PASS".equals(operation)) {
                checkTurn(player);

                if (arguments.length != 1) {
                    fail("Use PASS sem argumentos.");
                }

                player.passed = true;

                broadcast(
                    player.name + " encerrou suas acoes da rodada."
                );

                advanceTurn();
                broadcast("Ordem de turno atualizada.");
            } else if ("CHURCH".equals(operation)) {
                decideChurch(player, arguments);
            } else {
                reject(
                    player,
                    "Comando desconhecido. Utilize HELP."
                );
            }
        } catch (IllegalArgumentException exception) {
            reject(player, exception.getMessage());
        }
    }

    private boolean isInformationCommand(String operation) {
        return Arrays.asList(
            "HELP",
            "VIEW",
            "BOARD",
            "SELF",
            "RIVALS",
            "CARDS",
            "RESOURCES",
            "POINTS",
            "EXCOMMUNICATIONS",
            "RULES"
        ).contains(operation);
    }

    private String information(
        Player requester,
        String operation
    ) {
        if ("HELP".equals(operation)) {
            return "Consultas: VIEW, BOARD, SELF, RIVALS, "
                + "CARDS, RESOURCES, POINTS, "
                + "EXCOMMUNICATIONS, RULES.\n"
                + "Acoes: PLACE F1 MARKET-1; "
                + "PLACE F1 COUNCIL WOOD; "
                + "PLACE F1 TERRITORY-1 A; "
                + "PLACE F1 HARVEST-1 - 1,2; "
                + "SERVANTS F1 2; PASS; "
                + "CHURCH SUPPORT; CHURCH REFUSE; "
                + "CHAT mensagem.";
        }

        if ("RULES".equals(operation)) {
            return "Regras numericas de exemplo, nao historicas.\n"
                + "Mercado 1: +3 moedas.\n"
                + "Mercado 2: +2 servos.\n"
                + "Colheita: +1 madeira, mais efeitos escolhidos.\n"
                + "Producao: +1 pedra, mais efeitos escolhidos.\n"
                + "Conselho: COIN +2, WOOD +2, STONE +2, "
                + "FAITH +1, MILITARY +1, SERVANTS +2.\n"
                + "Cada servo aumenta em 1 o valor de um familiar.\n"
                + "Apoio a igreja consome os pontos de fe exigidos.\n"
                + "Excomunhao do periodo 1: -3 pontos finais.\n"
                + "Periodo 2: -1 no poder dos familiares.\n"
                + "Periodo 3: -2 pontos por territorio.\n"
                + "Pontuacao: vitoria + pontos impressos "
                + "+ militar/2 + fe/2 + moedas/3 "
                + "+ madeira/4 + pedra/4 + servos/3 "
                + "- penalidades.";
        }

        StringBuilder result = new StringBuilder();

        if ("BOARD".equals(operation) || "VIEW".equals(operation)) {
            result.append("Tabuleiro:\n");

            for (Space space : spaces.values()) {
                result.append(space.id)
                    .append(" | minimo ")
                    .append(space.power)
                    .append(" | ocupado: ")
                    .append(space.occupiedBy == null
                        ? "nao"
                        : space.occupiedBy)
                    .append('\n');

                if (space.card != null) {
                    result.append(
                        cardView(space.card).toString()
                    ).append('\n');
                }
            }
        }

        if ("VIEW".equals(operation)) {
            result.append("Periodo ")
                .append(period)
                .append(", rodada ")
                .append(round)
                .append(", fase ")
                .append(phase)
                .append('\n');
        }

        for (Player player : players.values()) {
            boolean show = "VIEW".equals(operation)
                || ("SELF".equals(operation)
                    && player == requester)
                || ("RIVALS".equals(operation)
                    && player != requester)
                || ("RESOURCES".equals(operation))
                || ("POINTS".equals(operation))
                || ("CARDS".equals(operation))
                || ("EXCOMMUNICATIONS".equals(operation));

            if (!show) {
                continue;
            }

            result.append("\nJogador: ")
                .append(player.name)
                .append('\n');

            if (
                "VIEW".equals(operation)
                || "SELF".equals(operation)
                || "RIVALS".equals(operation)
                || "RESOURCES".equals(operation)
            ) {
                result.append("Moedas: ")
                    .append(player.coins)
                    .append(" | Madeira: ")
                    .append(player.wood)
                    .append(" | Pedra: ")
                    .append(player.stone)
                    .append(" | Servos: ")
                    .append(player.servants)
                    .append('\n');
            }

            if (
                "VIEW".equals(operation)
                || "SELF".equals(operation)
                || "RIVALS".equals(operation)
                || "POINTS".equals(operation)
            ) {
                result.append("Vitoria: ")
                    .append(player.victory)
                    .append(" | Militar: ")
                    .append(player.military)
                    .append(" | Fe: ")
                    .append(player.faith)
                    .append(" | Pontuacao estimada: ")
                    .append(score(player))
                    .append('\n');
            }

            if (
                "VIEW".equals(operation)
                || "SELF".equals(operation)
                || "RIVALS".equals(operation)
                || "CARDS".equals(operation)
            ) {
                result.append("Cartas:\n");

                for (Card card : player.cards) {
                    result.append(
                        cardView(card).toString()
                    ).append('\n');
                }
            }

            if (
                "VIEW".equals(operation)
                || "SELF".equals(operation)
                || "EXCOMMUNICATIONS".equals(operation)
            ) {
                result.append("Excomunhoes: ")
                    .append(player.excommunications)
                    .append('\n');
            }
        }

        return result.toString();
    }

    private void checkTurn(Player player) {
        if (!PLAY.equals(phase)) {
            fail("A partida nao esta em fase de acoes.");
        }

        if (!player.token.equals(currentPlayer)) {
            fail("Nao e a sua vez.");
        }

        if (!canAct(player)) {
            fail("Voce nao possui familiares disponiveis.");
        }
    }

    private Familiar availableFamiliar(
        Player player,
        String id
    ) {
        for (Familiar familiar : player.familiars) {
            if (familiar.id.equalsIgnoreCase(id)) {
                if (familiar.used) {
                    fail("Este familiar ja foi utilizado.");
                }

                return familiar;
            }
        }

        fail("Familiar inexistente.");

        return null;
    }

    private void spendServants(
        Player player,
        String[] arguments
    ) {
        checkTurn(player);

        if (arguments.length != 3) {
            fail("Use SERVANTS F1 quantidade.");
        }

        Familiar familiar = availableFamiliar(
            player,
            arguments[1]
        );

        int quantity = positiveInteger(arguments[2]);

        if (quantity > player.servants) {
            fail("Servos insuficientes.");
        }

        player.servants -= quantity;
        familiar.value += quantity;

        broadcast(
            player.name + " gastou " + quantity
            + " servo(s) para aumentar o valor de "
            + familiar.id + ". O turno continua."
        );
    }

    private int positiveInteger(String value) {
        try {
            int result = Integer.parseInt(value);

            if (result <= 0) {
                fail("Informe uma quantidade positiva.");
            }

            return result;
        } catch (NumberFormatException exception) {
            fail("A quantidade informada e invalida.");
            return 0;
        }
    }

    private void place(
        Player player,
        String[] arguments
    ) {
        checkTurn(player);

        if (arguments.length < 3 || arguments.length > 5) {
            fail(
                "Use PLACE familiar area [opcao] [efeitos]."
            );
        }

        Familiar familiar = availableFamiliar(
            player,
            arguments[1]
        );

        Space space = spaces.get(
            arguments[2].toUpperCase(Locale.ROOT)
        );

        if (space == null) {
            fail("Area de acao inexistente.");
        }

        if (space.occupiedBy != null) {
            fail("Esta posicao ja esta ocupada.");
        }

        int power = familiar.value;

        if (player.excommunications.contains(2)) {
            power = Math.max(0, power - 1);
        }

        if (power < space.power) {
            fail(
                "Valor insuficiente. Necessario: "
                + space.power + ", disponivel: " + power
            );
        }

        String option = arguments.length >= 4
            ? arguments[3].toUpperCase(Locale.ROOT)
            : "-";

        if ("MARKET".equals(space.type)) {
            if (arguments.length > 3) {
                fail("O mercado nao exige opcoes adicionais.");
            }

            if ("MARKET-1".equals(space.id)) {
                player.coins += 3;
            } else {
                player.servants += 2;
            }

        } else if (
            "HARVEST".equals(space.type)
            || "PRODUCTION".equals(space.type)
        ) {
            if (arguments.length == 4 && !"-".equals(option)) {
                fail("Use - antes da lista de efeitos.");
            }

            if (arguments.length == 5 && !"-".equals(option)) {
                fail("A opcao da area deve ser -.");
            }

            List<Card> selected = selectedEffects(
                player,
                space.type,
                arguments.length == 5
                    ? arguments[4]
                    : ""
            );

            if ("HARVEST".equals(space.type)) {
                player.wood += 1;
            } else {
                player.stone += 1;
            }

            for (Card card : selected) {
                player.receive(card.permanent);
            }

        } else if ("COUNCIL".equals(space.type)) {
            if (arguments.length != 4) {
                fail("Escolha um privilegio do conselho.");
            }

            applyCouncilPrivilege(player, option);

        } else if ("TOWER".equals(space.type)) {
            if (arguments.length > 4) {
                fail("A torre aceita somente a alternativa de custo.");
            }

            buyCard(player, space, option);

        } else {
            fail("Area de acao invalida.");
        }

        familiar.used = true;

        space.occupiedBy = player.name + " " + familiar.id;

        broadcast(
            player.name + " colocou " + familiar.id
            + " em " + space.id + "."
        );

        advanceTurn();

        broadcast("Ordem de turno atualizada.");
    }

    private List<Card> selectedEffects(
        Player player,
        String area,
        String identifiers
    ) {
        List<Card> selected = new ArrayList<>();

        if (
            identifiers.isEmpty()
            || "-".equals(identifiers)
        ) {
            return selected;
        }

        Set<Integer> usedIdentifiers = new HashSet<>();

        for (String value : identifiers.split(",")) {
            int id = positiveInteger(value);

            if (!usedIdentifiers.add(id)) {
                fail("Um efeito permanente foi repetido.");
            }

            Card found = null;

            for (Card card : player.cards) {
                if (card.id == id) {
                    found = card;
                    break;
                }
            }

            if (found == null) {
                fail(
                    "A carta #" + id
                    + " nao pertence ao jogador."
                );
            }

            if (!area.equals(found.permanentArea)) {
                fail(
                    "A carta #" + id
                    + " nao possui efeito nesta area."
                );
            }

            selected.add(found);
        }

        return selected;
    }

    private void applyCouncilPrivilege(
        Player player,
        String privilege
    ) {
        switch (privilege) {
            case "COIN":
                player.coins += 2;
                break;

            case "WOOD":
                player.wood += 2;
                break;

            case "STONE":
                player.stone += 2;
                break;

            case "FAITH":
                player.faith += 1;
                break;

            case "MILITARY":
                player.military += 1;
                break;

            case "SERVANTS":
                player.servants += 2;
                break;

            default:
                fail("Privilegio do conselho inexistente.");
        }
    }

    private void buyCard(
        Player player,
        Space space,
        String option
    ) {
        Card card = space.card;

        if (card == null) {
            fail("Nao existe carta neste andar.");
        }

        if (player.military < card.requiredMilitary) {
            fail(
                "Pontos militares insuficientes. "
                + "Necessario: " + card.requiredMilitary
            );
        }

        Cost cost;

        if ("-".equals(option) || "A".equals(option)) {
            cost = card.primary;
        } else if (
            "B".equals(option)
            && card.alternative != null
        ) {
            cost = card.alternative;
        } else {
            fail("Alternativa de custo invalida.");
            return;
        }

        if (!player.canPay(cost)) {
            fail(
                "Recursos insuficientes para a carta. "
                + "Custo: " + cost.description()
            );
        }

        player.pay(cost);
        player.cards.add(card);
        player.receive(card.immediate);

        space.card = null;
    }

    private void advanceTurn() {
        if (!PLAY.equals(phase)) {
            return;
        }

        List<Player> order = orderedPlayers();

        for (int step = 1; step <= order.size(); step++) {
            int index = (
                currentIndex + step
            ) % order.size();

            Player candidate = order.get(index);

            if (canAct(candidate)) {
                currentIndex = index;
                currentPlayer = candidate.token;
                return;
            }
        }

        completeRound();
    }

    private void completeRound() {
        if (round < roundsPerPeriod) {
            round++;
            initializeRound();

            broadcast(
                "Nova rodada iniciada: "
                + round + "."
            );

            return;
        }

        phase = CHURCH;
        currentPlayer = "";

        for (Player player : players.values()) {
            player.churchAnswered = false;

            if (!player.connected) {
                refuseChurch(player);
            }
        }

        broadcast(
            "Periodo " + period
            + " encerrado. Cada jogador deve decidir: "
            + "CHURCH SUPPORT ou CHURCH REFUSE."
        );

        completeChurchIfReady();
    }

    private void decideChurch(
        Player player,
        String[] arguments
    ) {
        if (!CHURCH.equals(phase)) {
            fail("Nao existe decisao da igreja pendente.");
        }

        if (player.churchAnswered) {
            fail("Voce ja tomou sua decisao neste periodo.");
        }

        if (arguments.length != 2) {
            fail("Use CHURCH SUPPORT ou CHURCH REFUSE.");
        }

        String decision = arguments[1]
            .toUpperCase(Locale.ROOT);

        if ("SUPPORT".equals(decision)) {
            int threshold = setting(
                "church.threshold." + period,
                period + 1
            );

            if (player.faith < threshold) {
                fail(
                    "Fe insuficiente para apoiar a igreja. "
                    + "Necessario: " + threshold
                );
            }

            player.faith -= threshold;
            player.churchAnswered = true;

            broadcast(
                player.name + " apoiou a igreja."
            );

        } else if ("REFUSE".equals(decision)) {
            refuseChurch(player);

            broadcast(
                player.name
                + " recusou apoio e recebeu a excomunhao "
                + period + "."
            );

        } else {
            fail("Escolha SUPPORT ou REFUSE.");
        }

        completeChurchIfReady();
    }

    private void refuseChurch(Player player) {
        player.excommunications.add(period);
        player.churchAnswered = true;
    }

    private void completeChurchIfReady() {
        if (!CHURCH.equals(phase)) {
            return;
        }

        for (Player player : players.values()) {
            if (!player.churchAnswered) {
                return;
            }
        }

        if (period < periodCount) {
            period++;
            round = 1;
            phase = PLAY;

            initializeRound();

            broadcast(
                "Novo periodo iniciado: " + period + "."
            );
        } else {
            finishGame();
        }
    }

    private int score(Player player) {
        int total = player.victory;

        total += player.military / 2;
        total += player.faith / 2;
        total += player.coins / 3;
        total += player.wood / 4;
        total += player.stone / 4;
        total += player.servants / 3;

        int territoryCount = 0;

        for (Card card : player.cards) {
            total += card.printedPoints;

            if ("TERRITORY".equals(card.type)) {
                territoryCount++;
            }
        }

        if (player.excommunications.contains(1)) {
            total -= 3;
        }

        if (player.excommunications.contains(3)) {
            total -= territoryCount * 2;
        }

        return total;
    }

    private void finishGame() {
        phase = FINISHED;
        currentPlayer = "";

        List<Player> ranking = orderedPlayers();

        ranking.sort(
            (a, b) -> {
                int comparison = Integer.compare(
                    score(b),
                    score(a)
                );

                if (comparison != 0) {
                    return comparison;
                }

                return a.name.compareToIgnoreCase(b.name);
            }
        );

        StringBuilder text = new StringBuilder(
            "Classificacao final:\n"
        );

        for (int index = 0; index < ranking.size(); index++) {
            Player player = ranking.get(index);

            text.append(index + 1)
                .append(" - ")
                .append(player.name)
                .append(": ")
                .append(score(player))
                .append(" pontos\n");
        }

        finalRanking = text.toString();

        broadcast(finalRanking);
    }

    public synchronized void disconnect(String token) {
        Player player = players.get(token);

        listeners.remove(token);

        if (player == null || !player.connected) {
            return;
        }

        player.connected = false;

        if (LOBBY.equals(phase)) {
            players.remove(token);

            broadcast(
                player.name + " saiu da sala."
            );

            return;
        }

        if (PLAY.equals(phase)) {
            player.passed = true;

            if (token.equals(currentPlayer)) {
                advanceTurn();
            }
        }

        if (
            CHURCH.equals(phase)
            && !player.churchAnswered
        ) {
            refuseChurch(player);
            completeChurchIfReady();
        }

        broadcast(
            player.name
            + " foi desconectado e nao recebera novos turnos."
        );
    }

    private void reject(Player player, String reason) {
        sendTo(player, "ERROR", reason);
    }

    private void fail(String reason) {
        throw new IllegalArgumentException(reason);
    }

    private CardView cardView(Card card) {
        CardView view = new CardView();

        view.id = card.id;
        view.name = card.name;
        view.type = card.type;

        view.primaryCost = card.primary.description();

        view.alternativeCost = card.alternative == null
            ? "indisponivel"
            : card.alternative.description();

        view.immediateEffect = card.immediate.description();

        view.permanentEffect = card.permanentArea.isEmpty()
            ? "nenhum"
            : card.permanentArea + ": "
                + card.permanent.description();

        view.printedPoints = card.printedPoints;
        view.requiredMilitary = card.requiredMilitary;

        return view;
    }

    private Snapshot snapshot(Player recipient) {
        Snapshot result = new Snapshot();

        result.phase = phase;
        result.self = recipient.name;

        result.currentPlayer = currentPlayer.isEmpty()
            ? ""
            : players.get(currentPlayer).name;

        result.period = period;
        result.round = round;
        result.requiredPlayers = requiredPlayers;
        result.ranking = finalRanking;

        result.churchMessage = CHURCH.equals(phase)
            ? "Apoio necessario: "
                + setting(
                    "church.threshold." + period,
                    period + 1
                )
                + " pontos de fe."
            : "";

        for (Player player : players.values()) {
            PlayerView view = new PlayerView();

            view.name = player.name;
            view.self = player == recipient;
            view.connected = player.connected;
            view.passed = player.passed;
            view.churchAnswered = player.churchAnswered;

            view.coins = player.coins;
            view.wood = player.wood;
            view.stone = player.stone;
            view.servants = player.servants;
            view.faith = player.faith;
            view.military = player.military;
            view.victory = player.victory;
            view.score = score(player);

            for (Familiar familiar : player.familiars) {
                view.familiars.add(
                    familiar.id
                    + ": valor " + familiar.value
                    + (familiar.used ? " [usado]" : " [livre]")
                );
            }

            for (Card card : player.cards) {
                view.cards.add(cardView(card));
            }

            List<Integer> penalties = new ArrayList<>(
                player.excommunications
            );

            Collections.sort(penalties);

            for (Integer penalty : penalties) {
                if (penalty == 1) {
                    view.excommunications.add(
                        "Periodo 1: -3 pontos finais"
                    );
                } else if (penalty == 2) {
                    view.excommunications.add(
                        "Periodo 2: -1 no valor dos familiares"
                    );
                } else if (penalty == 3) {
                    view.excommunications.add(
                        "Periodo 3: -2 pontos por territorio"
                    );
                }
            }

            result.players.add(view);
        }

        for (Space space : spaces.values()) {
            SpaceView view = new SpaceView();

            view.id = space.id;
            view.minimumPower = space.power;
            view.occupiedBy = space.occupiedBy;
            view.card = space.card == null
                ? null
                : cardView(space.card);

            result.spaces.add(view);
        }

        return result;
    }

    private void sendTo(
        Player player,
        String kind,
        String text
    ) {
        Sink sink = listeners.get(player.token);

        if (sink == null) {
            return;
        }

        Event event = new Event(
            kind,
            text,
            snapshot(player)
        );

        notifications.execute(() -> {
            try {
                sink.send(event);
            } catch (Exception exception) {
                System.err.println(
                    "Falha ao notificar participante: "
                    + player.name
                );
            }
        });
    }

    private void broadcast(String text) {
        for (Player player : players.values()) {
            if (player.connected) {
                sendTo(
                    player,
                    "STATE",
                    text
                );
            }
        }
    }
}