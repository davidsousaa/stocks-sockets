package Server;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StockServer extends Remote {
	String stock_request() throws RemoteException;
	String stock_update(String key, int newValue) throws RemoteException;
	void subscribe(DirectNotification client) throws RemoteException;
	void unsubscribe(DirectNotification client) throws RemoteException;
}