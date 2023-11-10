package Server;

import java.rmi.Remote;

public interface DirectNotification extends Remote{
    void stock_updated(String message) throws java.rmi.RemoteException;
}
