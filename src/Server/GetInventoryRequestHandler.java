package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class GetInventoryRequestHandler extends Thread {
    Socket ligacao;
    Inventory inventory;
    PrintWriter out;
    String response;
    Server server;
    String msg;

    public GetInventoryRequestHandler(String msg, Socket ligacao, Inventory inventory, Server server) {
        this.ligacao = ligacao;
        this.inventory = inventory;
        this.server = server;
        this.msg = msg;
        try {
            this.out = new PrintWriter(ligacao.getOutputStream());
        } catch (IOException e) {
            System.out.println("STOCK_ERROR: Erro na execucao do servidor: " + e);
            System.exit(1);
        }
    }

    public void run() {
        try {
			String[] newMsg = msg.split(" ");

			if (msg.startsWith("STOCK_REQUEST")) {
                try {
                    response = server.stock_request();
                    System.out.println(response);
                } catch (IOException e) {
                    System.out.println("STOCK_ERROR: Erro na execucao do servidor: " + e);
                }
            } else if (msg.startsWith("STOCK_UPDATE")) {
                try {
                    String key = newMsg[1];
                    int newValue = Integer.parseInt(newMsg[2]);
                    response = server.stock_update(key, newValue);
                    System.out.println(inventory.readInventory());
                } catch (IOException e) {
                    System.out.println("STOCK_ERROR: Erro na execucao do servidor: " + e);
                }
            } else if (msg.startsWith("CLOSE")) {
                response = "Connection closed with client" + ligacao.getInetAddress();
            } else if (msg.startsWith("GET_PUBKEY")) {
                response = Base64.getEncoder().encodeToString(server.get_pubkey().getEncoded());
            } else {
                response = "STOCK_ERROR: invalid Command";
            }

        out.println(response);
        out.flush();


        } catch (Exception e) {
            System.out.println("STOCK_ERROR: Erro na execucao do servidor: " + e);
            System.exit(1);
        } finally {
            try {
                out.close();
                ligacao.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

