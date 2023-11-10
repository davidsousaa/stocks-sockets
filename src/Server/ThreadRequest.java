package Server;
import java.io.*;
import java.net.*;

public class ThreadRequest implements Runnable {
    Thread t;
    String servidor = "127.0.0.1";
    int port = 2000;

    public ThreadRequest() {
        t = new Thread(this, "ThreadRequest");
        t.start();
    }

    public void run () {
        InetAddress serverAdress = null;
        
        try {
            serverAdress = Inet4Address.getByName(servidor);
        } catch (UnknownHostException e){
            System.out.println("Erro ao obter o endereco do servidor: "+e);
			System.exit(1);
        }

        try {  
            while (true) {
                StringBuilder msg = new StringBuilder();
                int value = 0;
                Socket client = null;
                try {
			        client = new Socket(serverAdress, port);
		        } catch (IOException e) {
			        System.out.println("Erro ao criar socket para ligacao ao servidor: "+e);
			        System.exit(1);
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                out.println("STOCK_REQUEST");
			    while ((value  = in.read()) != -1) msg.append((char) value);
                System.out.println(msg.toString());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    System.out.println("Erro na execucao do servidor: " + e);
                    System.exit(1);
                }
                out.flush();
                in.close();
                out.close();
                client.close();
            }

        } catch (IOException e) {
            System.out.println("Erro na execucao do servidor: " + e);
            System.exit(1);
        }
    }

}
