package Server;

import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Server extends UnicastRemoteObject implements StockServer{
    private Inventory inventory;
    private ServerSocket serverSocket;
    static int DEFAULT_SOCKET_PORT=2000;
    static int DEFAULT_RMI_PORT=1999;
    static String SERVICE_NAME="StockServer";
    private List<ClientConnected> directNotifications;
    //private List<String> ipList;

    public Server() throws IOException, RemoteException {
        inventory = new Inventory();
        directNotifications = new ArrayList<>();
        //ipList = new ArrayList<>();
    }

    public void runSocketServer() throws IOException {
        try {
            serverSocket = new ServerSocket(DEFAULT_SOCKET_PORT);
            System.out.println("SocketServer wating for connections on port " + DEFAULT_SOCKET_PORT);
            while (true) {
                Socket ligacao = serverSocket.accept(); 
                GetInventoryRequestHandler handler = new GetInventoryRequestHandler(ligacao, inventory, this);
                handler.start();
                /*String ip = ligacao.getInetAddress().toString();
                if (!ipList.contains(ip) && ip != null) {
                    ipList.add(ip);
                }*/
            }
        } catch (IOException e) {
            System.out.println("Erro na execucao do servidor: " + e);
            System.exit(1);
        }
    }

    public void runRMIServer() throws RemoteException {
        try {
            LocateRegistry.createRegistry(DEFAULT_RMI_PORT);
        } catch (RemoteException e) {
            System.out.println("RMI Server error: " + e);
            System.exit(1);
        }
        try {
            LocateRegistry.getRegistry(DEFAULT_RMI_PORT).rebind(SERVICE_NAME, this);
        } catch (RemoteException e) {
            System.out.println("RMI Server error: " + e);
            System.exit(1);
        }
        System.out.println("RMIServer wating for connections on port " + DEFAULT_RMI_PORT);
    }
        

    @Override
    public String stock_request() throws RemoteException {
        return inventory.toString();
    }

    @Override
    public String stock_update(String key, int newValue) throws RemoteException {
        String response = inventory.changeQuantity(key, newValue) + "\n" + inventory.toString();
        if (directNotifications != null && directNotifications.size() > 0) {
            this.notifyAllClients(response);
        }
        System.out.println(directNotifications.size() + " clients notified");
        return response;
    }

    @Override
    public String subscribe(DirectNotification client) throws RemoteException {
        if (client == null) {
            return "client is null";
        }
        ClientConnected clientInfo = new ClientConnected(client);
        this.directNotifications.add(clientInfo);
        return "Subscribed";
    }

    //Not used
    @Override
    public String unsubscribe(DirectNotification client) throws RemoteException {
        if (client == null)
            return "client is null";
        directNotifications.remove(client);
        return "Unsubscribed";
    }

    public void notifyAllClients(String message) throws RemoteException {
        for (ClientConnected client : directNotifications) {
            try {
              client.notifyClient(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        /*for (int i = 0; i < ipList.size(); i++) {
            try {
                String ip = ipList.get(i);
                if (ip != null) {
                    System.out.println("Trying to connect to: " + ip);
                    Socket socket = new Socket(ip, DEFAULT_SOCKET_PORT);
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    out.println(message);
                    out.flush();
                    out.close();
                    socket.close();
                } else {
                    System.out.println("Skipping null InetAddress at index " + i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        
        
    }

    public static void main(String[] args) throws IOException, RemoteException {
        try {
            Server server = new Server();
            server.runRMIServer();
            server.runSocketServer();
            System.out.println("Server ready");
        } catch (Exception e) {
            System.out.println("Erro na execucao do servidor: " + e);
            System.exit(1);
        }
        
    }
}
