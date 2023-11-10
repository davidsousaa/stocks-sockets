package Server;

import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Server extends UnicastRemoteObject implements Serializable, StockServer{
    private Inventory inventory;
    private ServerSocket serverSocket;
    static int DEFAULT_SOCKET_PORT=2000;
    static int DEFAULT_RMI_PORT=1999;
    private List<DirectNotification> directNotifications;

    public Server() throws IOException, RemoteException {
        inventory = new Inventory();
        directNotifications = new ArrayList<>();
    }

    public void runStockServer() throws IOException {
        try {
            serverSocket = new ServerSocket(DEFAULT_SOCKET_PORT);
            System.out.println("Server wating for connections on port " + DEFAULT_SOCKET_PORT);

            while (true) {
                Socket ligacao = serverSocket.accept();
                GetInventoryRequestHandler handler = new GetInventoryRequestHandler(ligacao, inventory, this);
                handler.start();
            }
        } catch (IOException e) {
            System.out.println("Erro na execucao do servidor: " + e);
            System.exit(1);
        }

    }

    public void runRMIServer() throws RemoteException {
        try {
            LocateRegistry.createRegistry(DEFAULT_RMI_PORT);
            Registry registry = LocateRegistry.getRegistry(DEFAULT_RMI_PORT);
            registry.rebind("StockServer", this);
            System.out.println("RMI Server ready");
        } catch (Exception e) {
            System.out.println("RMI Server error: " + e);
            System.exit(1);
        }
    }

    @Override
    public String stock_request() throws RemoteException {
        return inventory.toString();
    }

    @Override
    public String stock_update(String key, int newValue) throws RemoteException {
        return inventory.changeQuantity(key, newValue);
    }

    @Override
    public void subscribe(DirectNotification client) throws RemoteException {
        directNotifications.add(client);
    }

    @Override
    public void unsubscribe(DirectNotification client) throws RemoteException {
        directNotifications.remove(client);
    }

    public void notifyAllClients(String message) throws RemoteException {
        for (DirectNotification client : directNotifications) {
            try {
              client.stock_updated(message);  
            } catch (RemoteException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) throws IOException, RemoteException {
        try {
            Server server = new Server();
            server.runStockServer();
            server.runRMIServer();
            System.out.println("Server ready");
        } catch (Exception e) {
            System.out.println("Erro na execucao do servidor: " + e);
            System.exit(1);
        }
        
    }
}
