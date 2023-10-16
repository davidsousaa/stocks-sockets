import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Inventory {

    private Hashtable<String, Integer> inventory;
    
    public Inventory() throws IOException {
        this.inventory = new Hashtable<String, Integer>();
        if (new File("inventario.txt").exists()) {
            this.inventory = readInventory();
        } else {
            this.inventory.put("Pera", 5);
            this.inventory.put("Banana", 5);
            this.inventory.put("Maça", 10);
            writeInventory(inventory);
        }
    }

    public int getQuantity(String name) {
        return this.inventory.get(name);
    }

    public synchronized String changeQuantity(String key, int newValue) {
        if (inventory.containsKey(key)) {
            inventory.replace(key, (inventory.get(key) + newValue));
            try {
                writeInventory(inventory);
            } catch (IOException e) {
                System.out.println("Erro na execucao do servidor: " + e);
                System.exit(1);
            }
            return "New quantity : " + inventory.get(key) + " " + key + "s";
        } else {
            return "STOCK_ERROR: Quantidade invalida";
        }
    }

    public void writeInventory(Hashtable<String, Integer> inventory) throws IOException {

        File file = new File("inventario.txt");
        file.delete();

        FileWriter writer = new FileWriter("inventario.txt");

        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {

            writer.write(entry.getKey() + "," + entry.getValue() + "\n");
        }

        writer.close();
    }

    public Hashtable<String, Integer> readInventory() throws IOException {
    
        File file = new File("inventario.txt");
    
        FileReader reader = new FileReader(file);
    
        BufferedReader in = new BufferedReader(reader);
    
        String line;
        while ((line = in.readLine()) != null) {
            String[] columns = line.split(",");
    
            if (columns.length == 1) {
                columns = new String[]{line, "1"};
            }
    
            inventory.put(columns[0], Integer.parseInt(columns[1]));
        }
    
        reader.close();
    
        return inventory;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            result.append(entry.getKey());
            result.append(" : ");
            result.append(entry.getValue());
            result.append("\n ");
        }

        if (result.length() > 1) {
            result.setLength(result.length() - 2);
        }

        return result.toString();
    }


}
