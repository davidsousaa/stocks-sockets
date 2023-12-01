package RMIClient;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.Scanner;

import Server.SecureDirectNotificationInterface;
import Server.StockServer;

public class RMIClient extends UnicastRemoteObject implements SecureDirectNotificationInterface{
    private StockServer server;
    static String SERVICE_NAME="StockServer";
    static String SERVICE_HOST="localhost";
    static int SERVICE_PORT=1999;
    private boolean connected = false;
    PublicKey serverPublicKey = null;
    private String menu = " - Change Inventory - \n1 - Stock Request\n2 - Stock Update\n3 - Subscribe\n4 - Unsubscribe\n0 - Exit\nChoose an option:";

    public RMIClient() throws RemoteException {
            connected = false;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        if (!connected) {
            System.out.println("Not connected to server");
            return;
        }

        try {
            serverPublicKey = server.get_pubKey();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        while (true) {
            int option = -1;
            System.out.println(menu);
            do {
                option = -1;
                try {
                    option = scanner.nextInt();
                    scanner.nextLine();
                } catch (Exception e) {
                    System.out.println("Invalid option \nChoose an option: ");
                    scanner.nextLine();
                }
            } while (option < 0 || option > 4);
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
                        String can = server.stock_update(key, newValue);
                    
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
        RMIClient client = null;
        try {
            client = new RMIClient();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        client.connect(SERVICE_HOST, SERVICE_PORT);
        client.run();
    }   

    @Override
    public void stock_updated(String message) throws java.rmi.RemoteException {
        System.out.println(message);
    }

    @Override
    public String stock_updated_signed(String message, String signature) throws java.rmi.RemoteException {
        try {
            message = message.trim();
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(serverPublicKey);
            sig.update(message.getBytes());
            if (sig.verify(Base64.getDecoder().decode(signature))) {
                System.out.println(message);
            } else {
                System.out.println("Signature failed");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return message;
    }
}
