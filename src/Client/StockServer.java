package Client;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StockServer extends Remote, Serializable {
	String stock_request() throws RemoteException;
	String stock_update(String key, int newValue) throws RemoteException;
	String subscribe(DirectNotification client) throws RemoteException;
	String unsubscribe(DirectNotification client) throws RemoteException;
}