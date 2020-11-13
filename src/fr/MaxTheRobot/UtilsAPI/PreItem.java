package fr.MaxTheRobot.UtilsAPI;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PreItem {

	private ItemStack it;
	private InteractAction ia;
	private ClicAction ca;
	
	public PreItem set(ItemStack it) {
		this.it = it;
		return this;
	}
	
	public PreItem setName(String name) {
		ItemMeta itM = this.it.getItemMeta();
		itM.setDisplayName(name);
		this.it.setItemMeta(itM);
		return this;
	}
	
	public PreItem setEnchented() {
		ItemMeta itM = this.it.getItemMeta();
		itM.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
		itM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		this.it.setItemMeta(itM);
		return this;
	}
	
	public PreItem addEnchent(Enchantment e, int lvl) {
		ItemMeta itM = this.it.getItemMeta();
		itM.addEnchant(e, lvl, true);
		this.it.setItemMeta(itM);
		return this;
	}

	public PreItem addEnchentes(HashMap<Enchantment, Integer> enchs) {
		ItemMeta itM = this.it.getItemMeta();
		for(Entry<Enchantment, Integer> e : enchs.entrySet()) {
			itM.addEnchant(e.getKey(), e.getValue(), true);
		}
		this.it.setItemMeta(itM);
		return this;
	}
	
	public PreItem addItemFlag(ItemFlag flag) {
		ItemMeta itM = this.it.getItemMeta();
		itM.addItemFlags(flag);
		this.it.setItemMeta(itM);
		return this;
	}
	
	public PreItem addItemFlags(List<ItemFlag> flags) {
		ItemMeta itM = this.it.getItemMeta();
		for(ItemFlag flag : flags) {
			itM.addItemFlags(flag);
		}
		this.it.setItemMeta(itM);
		return this;
	}
	
	public PreItem setLore(String lore) {
		ItemMeta itM = this.it.getItemMeta();
		itM.setLore(Arrays.asList(lore));
		this.it.setItemMeta(itM);
		return this;
	}
	
	public PreItem setLore(List<String> lore) {
		ItemMeta itM = this.it.getItemMeta();
		itM.setLore(lore);
		this.it.setItemMeta(itM);
		return this;
	}
	
	public PreItem setInteract(InteractAction ia) {
		this.ia = ia;
		return this;
	}
	
	public PreItem setClic(ClicAction ca) {
		this.ca = ca;
		return this;
	}
	
	public ItemStack toItem() {
		return this.it;
	}
	
	public InteractAction getIa() {
		return ia;
	}
	
	public ClicAction getCa() {
		return ca;
	}
}

