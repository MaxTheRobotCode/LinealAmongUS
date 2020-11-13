package fr.MaxTheRobot.UtilsAPI;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Item {
	
	public static PreItem create(Material material, int data) {
		return new PreItem().set(new ItemStack(material, 1, (byte) data));
	}
	
	public static PreItem fromMat(Material material) {
		return new PreItem().set(new ItemStack(material, 1, (byte) 0));
	}
}
