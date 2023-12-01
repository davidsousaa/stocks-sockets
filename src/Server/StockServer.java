package Server;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;

public interface StockServer extends Remote, Serializable {
	String stock_request() throws RemoteException;
	String stock_update(String key, int newValue) throws RemoteException;
	String subscribe(SecureDirectNotificationInterface client) throws RemoteException;
	String unsubscribe(SecureDirectNotificationInterface client) throws RemoteException;
	PublicKey get_pubKey() throws RemoteException;
}