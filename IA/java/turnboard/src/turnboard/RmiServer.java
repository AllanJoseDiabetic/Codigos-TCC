package turnboard;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import turnboard.Protocol.Event;
import turnboard.Protocol.RmiObserver;
import turnboard.Protocol.RmiService;

public class RmiServer extends UnicastRemoteObject
        implements RmiService {

    private static final long serialVersionUID = 1L;

    private final Game game;

    public RmiServer(Game game) throws RemoteException {
        super();
        this.game = game;
    }

    public static void start(
        Game game,
        int port,
        String serviceName
    ) throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(
            port
        );

        registry.rebind(
            serviceName,
            new RmiServer(game)
        );
    }

    @Override
    public String join(
        String playerName,
        RmiObserver observer
    ) throws RemoteException {
        if (observer == null) {
            throw new RemoteException(
                "O cliente deve fornecer um observador."
            );
        }

        try {
            return game.join(
                playerName,
                event -> observer.receive(event)
            );

        } catch (IllegalArgumentException exception) {
            throw new RemoteException(
                exception.getMessage(),
                exception
            );
        }
    }

    @Override
    public void command(
        String session,
        String command
    ) throws RemoteException {
        game.command(
            session,
            command
        );
    }

    @Override
    public void leave(
        String session
    ) throws RemoteException {
        game.disconnect(session);
    }
}