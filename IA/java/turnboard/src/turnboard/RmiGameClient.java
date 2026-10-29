package turnboard;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.function.Consumer;

import turnboard.Protocol.Event;
import turnboard.Protocol.RmiObserver;
import turnboard.Protocol.RmiService;

public class RmiGameClient implements GameClient {

    private RmiService service;
    private String session;
    private Observer observer;

    private static class Observer extends UnicastRemoteObject
            implements RmiObserver {

        private static final long serialVersionUID = 1L;

        private final Consumer<Event> listener;

        Observer(
            Consumer<Event> listener
        ) throws RemoteException {
            super();
            this.listener = listener;
        }

        @Override
        public void receive(Event event)
                throws RemoteException {
            listener.accept(event);
        }
    }

    @Override
    public void connect(
        String host,
        int port,
        String playerName,
        String serviceName,
        Consumer<Event> eventListener
    ) throws Exception {
        Registry registry = LocateRegistry.getRegistry(
            host,
            port
        );

        service = (RmiService) registry.lookup(
            serviceName
        );

        observer = new Observer(
            eventListener
        );

        try {
            session = service.join(
                playerName,
                observer
            );

        } catch (Exception exception) {
            UnicastRemoteObject.unexportObject(
                observer,
                true
            );

            observer = null;

            throw exception;
        }
    }

    @Override
    public void send(String command) throws Exception {
        if (service == null || session == null) {
            throw new IllegalStateException(
                "O cliente RMI nao esta conectado."
            );
        }

        service.command(
            session,
            command
        );
    }

    @Override
    public void close() {
        if (service != null && session != null) {
            try {
                service.leave(session);
            } catch (Exception exception) {
                System.err.println(
                    "Nao foi possivel encerrar a sessao RMI."
                );
            }
        }

        session = null;
        service = null;

        if (observer != null) {
            try {
                UnicastRemoteObject.unexportObject(
                    observer,
                    true
                );
            } catch (Exception exception) {
                System.err.println(
                    "Nao foi possivel liberar o observador RMI."
                );
            }
        }

        observer = null;
    }
}