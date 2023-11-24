package SocketClient;
import java.io.*;
import java.net.*;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;


import Server.ThreadRequest;

public class SocketClient {
    static final int DEFAULT_PORT=2000;
	static final String DEFAULT_HOST="localhost";
	static PublicKey serverPublicKey;

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

			out.println("GET_PUBKEY");
			out.flush();
			try{ 
				serverPublicKey = KeyFactory.getInstance("RSA")
									.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(in.readLine())));
			} catch (Exception e) {
				System.out.println("STOCK_ERROR: "+ e);
			}
			String request = null;
			int exit = 1;
			
			do{
				System.out.println(menu);
				sc = new Scanner(System.in);
				if(sc.hasNextInt()) option = sc.nextInt();
				sc.nextLine();
				if(option == 1 || option == 2 || option == 3){
					do {
						System.out.println("Quantity: ");
						if(sc.hasNextInt()) {
							quantity = sc.nextInt();
							sc.nextLine();
							exit = -1;
						} else {
							sc.nextLine();
							System.out.println("Write a valid integer!");
						}
					} while(exit == 1);
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
			out.flush();
			String msg = in.readLine();
			String[] parts = msg.split("-_-");
			String response = parts[0];
			byte[] signature = Base64.getDecoder().decode(parts[1]);
			System.out.println(response);

			if(verifySignature(response, signature, serverPublicKey.getEncoded())){
				System.out.println("Valid signature");
				System.out.println("Processing message: " + response);
			} else {
				System.out.println("Invalid signature");
			}

		} catch (IOException e) {
			System.out.println("STOCK_ERROR: "+ e);
			System.exit(1);
		}
	
		} while (option != 0);
	}

	static boolean verifySignature(String message, byte[] signature, byte[] publicKey) {
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKey)));
            sign.update(message.getBytes());
            byte[] decodedSignature = Base64.getDecoder().decode(signature);
            return sign.verify(decodedSignature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
