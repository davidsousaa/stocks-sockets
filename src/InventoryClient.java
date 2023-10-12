import java.io.*;
import java.net.*;
import java.util.Scanner;

public class InventoryClient {
    static final int DEFAULT_PORT=2000;
	static final String DEFAULT_HOST="127.0.0.1";

	public static void main(String[] args) {
		String servidor=DEFAULT_HOST;
		int port=DEFAULT_PORT;
		String menu = " - Change Inventory - \n1 - Maça\n2 - Banana\n3 - Pera\n0 - Exit\nChoose an option: ";
		int option = -1;
		int quantity = -1;
		Scanner sc = new Scanner(System.in);
		
		if (args.length != 1) {
				System.out.println("Error: use java presencesClient <ip>");
				System.exit(-1);
		}

		if (args.length >= 1) servidor = args[0];
		if (args.length >= 2) port = Integer.parseInt(args[1]);

		InetAddress serverAdress = null;

		try {
			serverAdress = InetAddress.getByName(servidor);
		} catch (UnknownHostException e1) {
			System.out.println("Erro ao obter o endereco do servidor: "+e1);
			System.exit(1);
		}
		
		Socket client = null;

		try {
			client = new Socket(serverAdress, port);
		} catch (IOException e) {
			System.out.println("Erro ao criar socket para ligacao ao servidor: "+e);
			System.exit(1);
		}

		try {
				
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);

			String request = null;

			ThreadRequest thread = new ThreadRequest();

			do{
				System.out.println(menu);
				sc = new Scanner(System.in);
				if(sc.hasNextInt())
				option = sc.nextInt();
				sc.nextLine();
				System.out.println("Quantity: ");
				quantity = sc.nextInt();
				sc.nextLine();
			} while(option > 3 || option < 0);

			switch(option){
				case 1:
					request = "STOCK_UPDATE" + " " + "Maca" + " " + quantity;
					break;
				case 2:
					request = "STOCK_UPDATE" + " " + "Banana" + " " + quantity;
					break;
				case 3:
					request = "STOCK_UPDATE" + " " + "Pera" + " " + quantity;
					break;
				default:
					System.out.println("Invalid option!");
					break;
			}

			System.out.println("Request = " + request);

			out.println(request);			
			StringBuilder msg = new StringBuilder();
			
			int value = 0;
			while ((value  = in.read()) != -1) {
                msg.append((char) value);
            }

			if(msg.toString().equals("STOCK_UPDATED")) thread.restart();

			System.out.println("Response=" + msg.toString());
			
			sc.close();
			client.close();
			System.out.println("Terminou a ligacao!");
		} catch (IOException e) {
			System.out.println("Erro ao comunicar com o servidor: "+e);
			System.exit(1);
		}
	
	}
}
