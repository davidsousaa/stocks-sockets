package Server;

import java.rmi.RemoteException;

public class ClientConnected {
    private SecureDirectNotificationInterface client;
    private static int id_mem = 0;
    private int id = 0;

    public ClientConnected(SecureDirectNotificationInterface client) {
        this.client = client;
        this.id = ClientConnected.id_mem++;
    }

    public void notifyClient(String message, String signed) throws RemoteException {
        this.client.stock_updated_signed(message, signed);
    }

    public int getId() {
        return this.id;
    }

    public SecureDirectNotificationInterface getClient() {
        return this.client;
    }

}
