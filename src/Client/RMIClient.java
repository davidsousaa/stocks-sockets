package Client;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import Server.DirectNotification;
import Server.StockServer;

public class RMIClient implements DirectNotification, Serializable{
    private StockServer server;
    static String SERVICE_NAME="StockServer";
    static String SERVICE_HOST="localhost";
    static int SERVICE_PORT=1999;
    private boolean connected = false;
    private String menu = " - Change Inventory - \n1 - Stock Request\n2 - Stock Update\n3 - Subscribe\n4 - Unsubscribe\n0 - Exit\nChoose an option:";

    public RMIClient() {
            connected = false;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        if (!connected) {
            System.out.println("Not connected to server");
            return;
        }
        while (true) {
            System.out.println(menu);
            int option = scanner.nextInt();
            switch (option) {
                case 1:
                    try {
                        System.out.println(server.stock_request());
                    } catch (Exception e) {
                        System.out.println("Error: " + e);
                    }
                    break;
                case 2:
                    System.out.println("Product:");
                    String key = scanner.next();
                    System.out.println("Value:");
                    int newValue = scanner.nextInt();
                    try {
                        System.out.println(server.stock_update(key, newValue));
                    
                    } catch (Exception e) {
                        System.out.println("Error: " + e);
                    }
                    break;
                case 3:
                    try {
                        System.out.println(server.subscribe(this));
                    } catch (Exception e) {
                        System.out.println("Error: " + e);
                    }
                    break;
                case 4:
                    try {
                        System.out.println(server.unsubscribe(this));
                    } catch (Exception e) {
                        System.out.println("Error: " + e);
                    }
                    break;
                case 0:
                    try {
                        server.unsubscribe(this);
                    } catch (Exception e) {
                        System.out.println("Error: " + e);
                    }
                    System.out.println("Disconnecting...");
                    this.connected = false;
                    return;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }

    public void connect(String host, int port) {
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            server = (StockServer) registry.lookup(SERVICE_NAME);
            connected = true;
        } catch (Exception e) {
            System.out.println("Erro na execucao do servidor: " + e);
            System.exit(1);
        }
    }

    public static void main(String args[]) {
        RMIClient client = new RMIClient();
        client.connect(SERVICE_HOST, SERVICE_PORT);
        client.run();
    }   

    @Override
    public void stock_updated(String message) throws java.rmi.RemoteException {
        System.out.println(message);
    }

}
