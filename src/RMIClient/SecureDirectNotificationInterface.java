package RMIClient;

import java.io.Serializable;
import java.rmi.Remote;

public interface SecureDirectNotificationInterface extends Remote, Serializable{
    void stock_updated(String message) throws java.rmi.RemoteException;
    String stock_updated_signed(String message, String signed) throws java.rmi.RemoteException;
}
