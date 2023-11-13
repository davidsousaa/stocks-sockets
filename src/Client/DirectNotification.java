package Client;

import java.io.Serializable;
import java.rmi.Remote;

public interface DirectNotification extends Remote, Serializable{
    void stock_updated(String message) throws java.rmi.RemoteException;
}
