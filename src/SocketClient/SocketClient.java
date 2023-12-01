package SocketClient;

import java.io.*;
import java.net.*;
import java.security.KeyFactory;
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
	static InetAddress serverAdress = null;
	static int port = 0;

	public static void main(String[] args) throws InterruptedException {
		String servidor=DEFAULT_HOST;
		port=DEFAULT_PORT;
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

		try {
			serverAdress = InetAddress.getByName(servidor);
		} catch (UnknownHostException e1) {
			System.out.println("Erro ao obter o endereco do servidor: "+e1);
			System.exit(1);
		}

		ThreadRequest threadRequest = new ThreadRequest();

		do {

			try {
					
				if (serverPublicKey == null) {
                    try {
                        String pubKey = sendRequest("GET_PUBKEY").trim();
                        serverPublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(pubKey)));
                    } catch (Exception e) {
                        System.out.println("STOCK_ERROR: " + e);
                    }
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
						sendRequest(request);
						System.out.println("Terminou a ligacao!");
						System.exit(0);
						break;
					default:
						System.out.println("STOCK_ERROR: invalid Command");
						break;
				}

				String msg = sendRequest(request);
                if (msg != null) {
                    String msgWithSign = null;
                    String msgWithoutSign = null;
                    String msgSigned = null;
                    if (msg.startsWith("STOCK_UPDATED")) {
                        // Remove "STOCK_UPDATED" from the beginning of the message
                        msgWithSign = msg.substring(14);
                    } else {
                        // If it's a STOCK_REQUEST (because it comes without "STOCK_UPDATED" at the beginning)
                        msgWithSign = msg;
                    }
                    msgWithoutSign = msg.split("-_-")[0];
                    msgSigned = msgWithSign.split("-_-")[1].trim();
                    byte[] signatureDecoded = Base64.getDecoder().decode(msgSigned);

                    System.out.println("\n\n" + msgSigned);

                    if (verifySignature(msgWithoutSign, signatureDecoded, serverPublicKey)) {
                        System.out.println("Valid signature");
                        System.out.println("Processing message: " + msgWithoutSign);
                    } else {
                        System.out.println("STOCK ERROR: Invalid signature");
                    }
                }
                else {
					System.out.println("STOCK ERROR: No response from server");
				}
			} catch (Exception e) {
				System.out.println("STOCK_ERROR: "+ e);
				System.exit(1);
			}
	
		} while (option != 0);
	}

	private static String sendRequest(String request) {
		Socket client = null;
		BufferedReader in = null;	
		PrintWriter out = null;
		StringBuilder msg = new StringBuilder();
		int value = 0;
		try {
			client = new Socket(serverAdress, port);
			out = new PrintWriter(client.getOutputStream(), true);
			out.println(request);
			out.flush();
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch (IOException e) {
			System.out.println("Erro ao criar socket para ligacao ao servidor: "+e);
			return null;
		}
		
		try {
			while ((value  = in.read()) != -1) msg.append((char) value);
			if (request.equals("CLOSE")) return null;
			return msg.toString();
	
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				in.close();
				out.close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean verifySignature(String message, byte[] signature, PublicKey publicKey) {
        try {
			message = message.trim();
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(publicKey);
            sign.update(message.getBytes());

            boolean verified = sign.verify(signature);

            return verified;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
