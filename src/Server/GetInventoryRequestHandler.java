package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class GetInventoryRequestHandler extends Thread {
    Socket ligacao;
    Inventory inventory;
    BufferedReader in;
    PrintWriter out;
    String response;
    Server server;

    public GetInventoryRequestHandler(Socket ligacao, Inventory inventory, Server server) {
        this.ligacao = ligacao;
        this.inventory = inventory;
        this.server = server;
        try {
            this.in = new BufferedReader(new InputStreamReader(ligacao.getInputStream()));
            this.out = new PrintWriter(ligacao.getOutputStream());
        } catch (IOException e) {
            System.out.println("STOCK_ERROR: Erro na execucao do servidor: " + e);
            System.exit(1);
        }
    }

    public void run() {
        try {
            System.out.println("Aceitou ligacao de cliente no endereco " + ligacao.getInetAddress() + " na porta " + ligacao.getPort());

            String msg = in.readLine();
			System.out.println("Recebeu: " + msg);
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
			response = Base64.getEncoder().encodeToString(server.get_pubkey());
		} else {
            response = "STOCK_ERROR: invalid Command";
        }

        out.println(response);
        out.flush();

        } catch (IOException e) {
            System.out.println("STOCK_ERROR: Erro na execucao do servidor: " + e);
            System.exit(1);
        } finally {
            try {
                in.close();
                out.close();
                ligacao.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

