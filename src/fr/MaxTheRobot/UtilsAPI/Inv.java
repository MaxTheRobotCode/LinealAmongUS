package fr.MaxTheRobot.UtilsAPI;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Inv {

	private Inventory inventory;
	
	public void setup(String name, int size) {
		this.inventory = Bukkit.createInventory(null, size, name);
	}
	
	public void set(int slot, ItemStack item) {
		this.inventory.setItem(slot, item);
	}
	
	public int getSize() {
		return this.inventory.getSize();
	}
	
	public Inventory toInv() {
		return this.inventory;
	}
}
