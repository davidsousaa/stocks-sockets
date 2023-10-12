import java.io.*;
import java.net.*;
import java.util.Scanner;

public class InventoryClient {
    static final int DEFAULT_PORT=2000;
	static final String DEFAULT_HOST="127.0.0.1";

	public static void main(String[] args) {
		String servidor=DEFAULT_HOST;
		int port=DEFAULT_PORT;
		String menu = " - Change Inventory - \n1 - Maça\n2 - Banana\n3 - Pera\n0 - Exit\n";
		String itemsMenu = "1 - Maça\n2 - Banana\n3 - Pera\n Choose an option: ";
		int option = -1;
		Scanner sc = new Scanner(System.in);
		
		if (args.length != 1) {
				System.out.println("Error: use java presencesClient <ip>");
				System.exit(-1);
		}

		if (args.length >= 1) servidor = args[0];
		if (args.length >= 2) port = Integer.parseInt(args[1]);
	
		// Create a representation of the IP address of the Server: API java.net.InetAddress

		InetAddress serverAdress = null;

		try {
			serverAdress = InetAddress.getByName(servidor);
		} catch (UnknownHostException e1) {
			System.out.println("Erro ao obter o endereco do servidor: "+e1);
			System.exit(1);
		}

		
		// Create a client sockets (also called just "sockets"). A socket is an endpoint for communication between two machines: API java.net.Socket
		
		Socket client = null;

		try {
			client = new Socket(serverAdress, port);
		} catch (IOException e) {
			System.out.println("Erro ao criar socket para ligacao ao servidor: "+e);
			System.exit(1);
		}

		try {
			
			// Create a java.io.BufferedReader for the Socket; Use java.io.Socket.getInputStream() to obtain the Socket input stream
				
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			// Create a java.io. PrintWriter for the Socket; Use java.io.Socket.etOutputStream() to obtain the Socket output stream
	
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			String request = null;

			ThreadRequest thread = new ThreadRequest();

			do{
				System.out.println(menu);
				System.out.println("Choose an option: ");
				sc = new Scanner(System.in);
				if(sc.hasNextInt())
				option = sc.nextInt();
				sc.nextLine();
			} while(option > 3 || option < 0);

			switch(option){
				case 1:
					request = "STOCK_REQUEST";
					break;
				case 2:
					do {
						System.out.println(itemsMenu);
						if(sc.hasNextInt())
						option = sc.nextInt();
						sc.nextLine();
						System.out.println("Quantity: ");
						int quantity = sc.nextInt();
						sc.nextLine();
						switch (option) {
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
					} while(option > 3 || option < 1);
					
					break;
				case 0:
					System.exit(0);
					break;
			}

			System.out.println("Request=" + request);

			// write the request into the Socket
			
				out.println(request);			
				StringBuilder msg = new StringBuilder();
			
			// Read the server response - read the data until null
			int value = 0;
			while ((value  = in.read()) != -1) {
                msg.append((char) value);
            }

			// inventory = inventory.fromString(msg.toString());

			System.out.println("Response=" + msg.toString());
		// Close the Socket
			
			sc.close();
			client.close();
			System.out.println("Terminou a ligacao!");
		} catch (IOException e) {
			System.out.println("Erro ao comunicar com o servidor: "+e);
			System.exit(1);
		}
	
	}
}
