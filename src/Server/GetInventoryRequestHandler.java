package Server;
import java.net.*;

import java.io.*;

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
			this.in = new BufferedReader (new InputStreamReader(ligacao.getInputStream()));
			
			this.out = new PrintWriter(ligacao.getOutputStream());
		} catch (IOException e) {
			System.out.println("STOCK_ERROR: Erro na execucao do servidor: " + e);
			System.exit(1);
		}
	}
	
	public void run() {                
		try {
			System.out.println("Aceitou ligacao de cliente no endereco " + ligacao.getInetAddress() + " na porta " + ligacao.getPort());

			//String msg = null;
			//do {
				String msg = in.readLine();
				System.out.println("Request=" + msg);

				String [] newMsg = msg.split(" ");
				
				if (msg.startsWith("STOCK_REQUEST")) {
					response = server.stock_request();
					System.out.println(response);
				} else if (msg.startsWith("STOCK_UPDATE")) {
					String key = newMsg[1];
					int newValue = 0;
					newValue = Integer.parseInt(newMsg[2]);
					response = server.stock_update(key, newValue);
					System.out.println(inventory.readInventory());
				}else if (msg.startsWith("CLOSE")) {
					response = "Connection closed with client" + ligacao.getInetAddress();
				} else {
					response = "STOCK_ERROR: invalid Command";
				}
				System.out.println("Response=" + response);
				out.println(response);
			//} while (!msg.equals("CLOSE"));
				
			out.flush();
			in.close();
			out.close();
			ligacao.close();
		} catch (IOException e) {
			System.out.println("STOCK_ERROR: Erro na execucao do servidor: " + e);
			System.exit(1);
		}
	}
}
