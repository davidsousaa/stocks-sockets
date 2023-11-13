package Client;
import java.io.*;
import java.net.*;
import java.util.Scanner;


import Server.ThreadRequest;

public class SocketClient {
    static final int DEFAULT_PORT=2000;
	static final String DEFAULT_HOST="localhost";

	public static void main(String[] args) throws InterruptedException {
		String servidor=DEFAULT_HOST;
		int port=DEFAULT_PORT;
		String menu = " - Change Inventory - \n1 - Pera\n2 - Banana\n3 - Maca\n0 - Exit\nChoose an option: ";
		int option = -1;
		int quantity = -1;
		Scanner sc = new Scanner(System.in);
		
		if (args.length < 1) {
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

		ThreadRequest threadRequest = new ThreadRequest();

		do {

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
			
			do{
				System.out.println(menu);
				sc = new Scanner(System.in);
				if(sc.hasNextInt()) option = sc.nextInt();
				sc.nextLine();
				if(option == 1 || option == 2 || option == 3){
					System.out.println("Quantity: ");
					quantity = sc.nextInt();
					sc.nextLine();
				}
			} while(option > 3 || option < 0);

			switch(option){
				case 1:
					request = "STOCK_UPDATE" + " " + "Pera" + " " + quantity;
					break;
				case 2:
					request = "STOCK_UPDATE" + " " + "Banana" + " " + quantity;
					break;
				case 3:
					request = "STOCK_UPDATE" + " " + "Maca" + " " + quantity;
					break;
				case 0:
					request = "CLOSE";
					out.println(request);
					sc.close();
					client.close();
					System.out.println("Terminou a ligacao!");
					break;
				default:
					System.out.println("STOCK_ERROR: invalid Command");
					break;
			}

			out.println(request);
			String msg = in.readLine();
			System.out.println(msg);

		} catch (IOException e) {
			System.out.println("STOCK_ERROR: "+ e);
			System.exit(1);
		}
	
		} while (option != 0);
	}
}
