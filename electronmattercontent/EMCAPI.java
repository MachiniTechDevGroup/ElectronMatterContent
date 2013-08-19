package electronmattercontent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class EMCAPI {
	private static EMCAPI instance = new EMCAPI();
	private HashMap<List<Integer>, EMCEntry> emc = new HashMap<List<Integer>, EMCEntry>();
	private HashMap<String, EMCEntry> ore = new HashMap<String, EMCEntry>();

	private EMCAPI() {

	}

	/**
	 * Retrieves the static instance of the {@code EMCAPI}
	 * @return The static instance of the {@code EMCAPI}
	 */
	public static EMCAPI instance() {
		return instance;
	}

	/**
	 * Adds an {@code EMCEntry} to the specified ID and Meta
	 * @param id The item ID to add
	 * @param meta The metadata to add
	 * @param ent The {@code EMCEntry} to add
	 */
	public void addEMCtoItem(int id, int meta, EMCEntry ent) {// Base of addEMCtoItem
		EMC.println("Adding EMC of " + ent.getEMC() + " to id " + id + " and meta " + meta);
		emc.put(Arrays.asList(id, meta), ent);
	}

	/**
	 * Adds an {@code EMCEntry} to the specified ID (Metadata insensitive)
	 * @param id The item ID to add
	 * @param ent The {@code EMCEntry} to add
	 */
	public void addEMCtoItem(int id, EMCEntry ent) {
		addEMCtoItem(id, -1, ent);
	}

	/**
	 * Adds an {@code EMCEntry} to the specified {@code ItemStack}, metadata determinable
	 * @param stack The {@code ItemStack} to add
	 * @param ent The {@code EMCEntry} to add
	 * @param meta {@code true} if metadata sensitive, {@code false} if not
	 */
	public void addEMCtoItem(ItemStack stack, EMCEntry ent, boolean meta) {
		if (meta) {
			addEMCtoItem(stack.itemID, stack.getItemDamage(), ent);
		} else {
			addEMCtoItem(stack.itemID, -1, ent);
		}
	}

	/**
	 * Adds an {@code EMCEntry} to the specified {@code Item}, metadata determinable
	 * @param item The {@code Item} to add
	 * @param ent The {@code EMCEntry} to add
	 * @param meta {@code true} if metadata sensitive, {@code false} if not
	 */
	public void addEMCtoItem(Item item, EMCEntry ent, boolean meta) {
		addEMCtoItem(new ItemStack(item), ent, meta);
	}

	/**
	 * Adds an {@code EMCEntry} to the specified {@code Block}, metadata determinable
	 * @param block The {@code Block} to add
	 * @param ent The {@code EMCEntry} to add
	 * @param meta {@code true} if metadata sensitive, {@code false} if not
	 */
	public void addEMCtoItem(Block block, EMCEntry ent, boolean meta) {
		addEMCtoItem(new ItemStack(block), ent, meta);
	}

	/**
	 * Retrieves the {@code EMCEntry} associated with the given ID and Meta
	 * @param id The Item ID to get
	 * @param meta The Item metadata to get
	 * @return The {@code EMCEntry} associated with it, or the empty one if there is none
	 */
	public EMCEntry getEMCEntofItem(int id, int meta) {// Base of getEMCEntofItem
		if (emc.containsKey(Arrays.asList(id, meta))) {
			return emc.get(Arrays.asList(id, meta));
		} else if (emc.containsKey(Arrays.asList(id, -1))) {
			return emc.get(Arrays.asList(id, -1));
		}
		return new EMCEntry();// Empty entry
	}

	/**
	 * Retrieves the {@code EMCEntry} associated with the given ID (Metadata insensitive)
	 * @param id The Item ID to get
	 * @return he {@code EMCEntry} associated with it, or the empty one if there is none
	 */
	public EMCEntry getEMCEntofItem(int id) {
		return getEMCEntofItem(id, -1);
	}

	/**
	 * Retrieves the {@code EMCEntry} associated with the given {@code ItemStack}
	 * @param stack The {@code ItemStack} to get
	 * @return The {@code EMCEntry} associated with it, or the empty one if there is none
	 */
	public EMCEntry getEMCEntofItem(ItemStack stack) {
		return getEMCEntofItem(stack.itemID, stack.getItemDamage());
	}

	/**
	 * Retrieves whether or not there is a valid {@code EMCEntry}
	 * @param id The Item ID to get
	 * @param meta The Item metadata to get
	 * @return Whether or not there is a valid {@code EMCEntry}
	 */
	public boolean hasEMCEnt(int id, int meta) {// Base of hasEMCEnt
		if (emc.containsKey(Arrays.asList(id, meta))) {
			return !emc.get(Arrays.asList(id, meta)).isEmptyEntry();
		} else if (emc.containsKey(Arrays.asList(id, -1))) {
			return !emc.get(Arrays.asList(id, -1)).isEmptyEntry();
		}
		return false;
	}

	/**
	 * Retrieves whether or not there is a valid {@code EMCEntry} (Metadata insensitive)
	 * @param id The Item ID to get
	 * @return Whether or not there is a valid {@code EMCEntry}
	 */
	public boolean hasEMCEnt(int id) {
		return hasEMCEnt(id, -1);
	}

	/**
	 * Retrieves whether or not there is a valid {@code EMCEntry}
	 * @param stack The {@code ItemStack} to get
	 * @return Whether or not there is a valid {@code EMCEntry}
	 */
	public boolean hasEMCEnt(ItemStack stack) {
		return hasEMCEnt(stack.itemID, stack.getItemDamage());
	}
	
	//OreDict support
	
	public void addEMCtoOreDictName(String name, EMCEntry ent) {
		ore.put(name, ent);
		for (ItemStack s : OreDictionary.getOres(name)) {
			addEMCtoItem(s, ent, true);
		}
	}
	
	public EMCEntry getEMCEntofOreDictName(String name) {
		if (ore.containsKey(name)) {
			return ore.get(name);
		}
		return new EMCEntry();
	}
}
