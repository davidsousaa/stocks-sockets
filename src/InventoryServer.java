import java.io.*;
import java.net.*;

public class InventoryServer {
	static int DEFAULT_PORT=2000;
	
	public static void main(String[] args) throws IOException {
		int port = DEFAULT_PORT;
        
		Inventory inventory = new Inventory();
			
		ServerSocket servidor = null; 
	
// Create a server socket, bound to the specified port: API java.net.ServerSocket	
	
		servidor = new ServerSocket(port);
	
		System.out.println("Server wating for connections on port " + port);
		
		while(true) {
			try {

// Listen for a connection to be made to the socket and accepts it: API java.net.ServerSocket				
				
				Socket ligacao = servidor.accept();
				
// Start a GetPresencesRequestHandler thread				
				
				GetInventoryRequestHandler handler = new GetInventoryRequestHandler(ligacao, inventory);
				handler.start();
	
			} catch (IOException e) {
				System.out.println("Erro na execucao do servidor: "+e);
				System.exit(1);
			}
		}
	}
}