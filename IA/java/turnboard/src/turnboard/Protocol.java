package turnboard;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public final class Protocol {

    private Protocol() {
    }

    public interface Sink {
        void send(Event event) throws Exception;
    }

    public interface RmiService extends Remote {
        String join(String playerName, RmiObserver observer)
            throws RemoteException;

        void command(String session, String command)
            throws RemoteException;

        void leave(String session) throws RemoteException;
    }

    public interface RmiObserver extends Remote {
        void receive(Event event) throws RemoteException;
    }

    public static class Request implements Serializable {
        private static final long serialVersionUID = 1L;

        public String type;
        public String value;

        public Request(String type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    public static class Event implements Serializable {
        private static final long serialVersionUID = 1L;

        public String kind;
        public String text;
        public Snapshot snapshot;

        public Event(
            String kind,
            String text,
            Snapshot snapshot
        ) {
            this.kind = kind;
            this.text = text;
            this.snapshot = snapshot;
        }
    }

    public static class Snapshot implements Serializable {
        private static final long serialVersionUID = 1L;

        public String phase;
        public String self;
        public String currentPlayer;
        public String churchMessage;
        public String ranking;

        public int period;
        public int round;
        public int requiredPlayers;

        public List<PlayerView> players = new ArrayList<>();
        public List<SpaceView> spaces = new ArrayList<>();
    }

    public static class PlayerView implements Serializable {
        private static final long serialVersionUID = 1L;

        public String name;
        public boolean self;
        public boolean connected;
        public boolean passed;
        public boolean churchAnswered;

        public int coins;
        public int wood;
        public int stone;
        public int servants;
        public int faith;
        public int military;
        public int victory;
        public int score;

        public List<String> familiars = new ArrayList<>();
        public List<CardView> cards = new ArrayList<>();
        public List<String> excommunications = new ArrayList<>();
    }

    public static class SpaceView implements Serializable {
        private static final long serialVersionUID = 1L;

        public String id;
        public String occupiedBy;
        public int minimumPower;
        public CardView card;
    }

    public static class CardView implements Serializable {
        private static final long serialVersionUID = 1L;

        public int id;
        public String name;
        public String type;
        public String primaryCost;
        public String alternativeCost;
        public String immediateEffect;
        public String permanentEffect;
        public int printedPoints;
        public int requiredMilitary;

        @Override
        public String toString() {
            return "#" + id + " " + name
                + " [" + type + "]"
                + " custo A: " + primaryCost
                + " custo B: " + alternativeCost
                + " imediato: " + immediateEffect
                + " permanente: " + permanentEffect
                + " pontos finais: " + printedPoints
                + " militar minimo: " + requiredMilitary;
        }
    }
}