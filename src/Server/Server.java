package Server;

import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Server extends UnicastRemoteObject implements StockServer{
    private Inventory inventory;
    private ServerSocket serverSocket;
    static int DEFAULT_SOCKET_PORT=2000;
    static int DEFAULT_RMI_PORT=1999;
    static String SERVICE_NAME="StockServer";
    private List<ClientConnected> directNotifications;
    private PublicKey publicKey;
    private PrivateKey privateKey;
  

    public Server() throws IOException, RemoteException {
        inventory = new Inventory();
        directNotifications = new ArrayList<>();
    }

    public void runSocketServer() throws IOException {
        try {
            serverSocket = new ServerSocket(DEFAULT_SOCKET_PORT);
            System.out.println("SocketServer wating for connections on port " + DEFAULT_SOCKET_PORT);
            while (true) {
                Socket ligacao = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(ligacao.getInputStream()));
                System.out.println("Aceitou ligacao de cliente no endereco " + ligacao.getInetAddress() + " na porta " + ligacao.getPort());
                String msg = in.readLine();
                System.out.println("Recebeu: " + msg);
                
                GetInventoryRequestHandler handler = new GetInventoryRequestHandler(msg, ligacao, inventory, this);
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
        String response = inventory.toString();
        byte[] signature = signMessage(response);
        return response + "-_-" + Base64.getEncoder().encodeToString(signature);
    }

    @Override
    public String stock_update(String key, int newValue) throws RemoteException {
        String response = inventory.changeQuantity(key, newValue) + "\n" + inventory.toString();
        byte[] signature = signMessage(response);
        if (directNotifications != null && directNotifications.size() > 0) {
            this.notifyAllClients(response, Base64.getEncoder().encodeToString(signature));
        }
        System.out.println("response: " + response + "-_-" + Base64.getEncoder().encodeToString(signature));
        return response + "-_-" + Base64.getEncoder().encodeToString(signature);
    }

    @Override
    public String subscribe(SecureDirectNotificationInterface client) throws RemoteException {
        if (client == null) {
            return "client is null";
        }
        ClientConnected clientInfo = new ClientConnected(client);
        this.directNotifications.add(clientInfo);
        return "Subscribed";
    }

    //Not used
    @Override
    public String unsubscribe(SecureDirectNotificationInterface client) throws RemoteException {
        if (client == null)
            return "client is null";
        for (ClientConnected clientInfo : directNotifications) {
            if (clientInfo.getClient().equals(client)) {
                directNotifications.remove(clientInfo);
                return "Unsubscribed";
            }
        }
        return "Client not found";
    }

    public void notifyAllClients(String message, String signed) throws RemoteException {
        for (ClientConnected client : directNotifications) {
            try {
              client.notifyClient(message, signed);
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

    @Override
    public PublicKey get_pubKey() throws RemoteException {
        return this.publicKey;
    }

    public int getInventoryLength() {
        return inventory.toString().length();
    }

    public void generateKeys() throws IOException {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
            System.out.println("Public key: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        } catch (NoSuchAlgorithmException e) { 
            e.printStackTrace();
        }
    }

    byte[] signMessage(String message) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes());
            return signature.sign();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public PublicKey get_pubkey() {
        return this.publicKey;
    }

    public static void main(String[] args) throws IOException, RemoteException {
        try {
            Server server = new Server();
            server.generateKeys();
            server.runRMIServer();
            server.runSocketServer();
            
            System.out.println("Server ready");
        } catch (Exception e) {
            System.out.println("Erro na execucao do servidor: " + e);
            System.exit(1);
        }
        
    }

}
