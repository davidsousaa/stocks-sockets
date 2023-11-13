package Server;

import java.rmi.RemoteException;

public class ClientConnected {
    private DirectNotification client;
    private static int id_mem = 0;
    private int id = 0;

    public ClientConnected(DirectNotification client) {
        this.client = client;
        this.id = ClientConnected.id_mem++;
    }

    public void notifyClient(String message) throws RemoteException {
        this.client.stock_updated(message);
    }

    public DirectNotification getId() {
        return this.client;
    }

}
