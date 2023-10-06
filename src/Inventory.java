import java.util.*;

public class Inventory {

    private Hashtable<String, Integer> inventory;
    
    public Inventory() {
        this.inventory = new Hashtable<String, Integer>();
        this.inventory.put("Maca", 5);
        this.inventory.put("Pera", 5);
        this.inventory.put("Banana", 10);
    }

    public int getQuantity(String name) {
        return this.inventory.get(name);
    }

    public synchronized String changeQuantity(String key, int newValue) {
        if (inventory.containsKey(key)) {
            inventory.replace(key, (inventory.get(key) + newValue));
            return "New quantity : " + inventory.get(key) + " " + key + "s";
        } else {
            return "STOCK_ERROR: Quantidade invalida";
        }
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

    public Inventory fromString(String string) {
        Inventory inventory = new Inventory();

        StringBuilder msg = new StringBuilder(string);
        String[] lines = msg.toString().split("\n");

        for (String line : lines) {
            String[] parts = line.split(" : ");
            inventory.inventory.put(parts[0], Integer.parseInt(parts[1]));
        }

        return inventory;
    }

}
