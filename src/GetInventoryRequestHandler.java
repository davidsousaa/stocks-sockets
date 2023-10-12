import java.net.*;
import java.io.*;

public class GetInventoryRequestHandler extends Thread {
	Socket ligacao;
	Inventory inventory;
	BufferedReader in;
	PrintWriter out;

	public GetInventoryRequestHandler(Socket ligacao, Inventory inventory) {
		this.ligacao = ligacao;
		this.inventory = inventory;
		try
		{	
			this.in = new BufferedReader (new InputStreamReader(ligacao.getInputStream()));
			
			this.out = new PrintWriter(ligacao.getOutputStream());
		} catch (IOException e) {
			System.out.println("Erro na execucao do servidor: " + e);
			System.exit(1);
		}
	}
	
	public void run() {                
		try {
			System.out.println("Aceitou ligacao de cliente no endereco " + ligacao.getInetAddress() + " na porta " + ligacao.getPort());
			
			String response;
			String msg = in.readLine();
			System.out.println("Request=" + msg);

			String [] newMsg = msg.split(" ");
			
			if (msg.startsWith("STOCK_REQUEST")) {
				response = inventory.toString();
				System.out.println(response);
			} else if (msg.startsWith("STOCK_UPDATE")) {
				String key = newMsg[1];
				int newValue = 0;
				newValue = Integer.parseInt(newMsg[2]);
				if (newMsg[2].startsWith("-")) inventory.changeQuantity(key, -newValue); 
				else inventory.changeQuantity(key, newValue);
				response = "STOCK_UPDATED";
				System.out.println(inventory.toString());
			} else {
				response = "STOCK_ERROR: invalid Command";
			}

			out.println(response);
				
			out.flush();
			in.close();
			out.close();
			ligacao.close();
		} catch (IOException e) {
			System.out.println("Erro na execucao do servidor: " + e);
			System.exit(1);
		}
	}
}
